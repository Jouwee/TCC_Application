package com.github.jouwee.tcc_projeto;

import com.github.jouwee.tcc_projeto.helper.Compressor;
import com.github.jouwee.tcc_projeto.model.GenerationParameters;
import com.github.jouwee.tcc_projeto.model.SpeciesMap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GeneticAlgorithmController {
    
    private static GeneticAlgorithmController instance;
    private final GeneticAlgorithmModel model;
    private CompletableFuture<GenerationResult> generationFuture;

    public GeneticAlgorithmController() {
        this.model = new GeneticAlgorithmModel();
    }

    public synchronized static GeneticAlgorithmController get() {
        if (instance == null) {
            instance = new GeneticAlgorithmController();
        }
        return instance;
    }

    /**
     * Adds a listener for messages
     * 
     * @param processor 
     */
    public void onMessage(MessageProcessor processor) {
        model.onMessage(processor);
    }

    /**
     * Removes a listener for messages
     * 
     * @param processor 
     */
    public void removeOnMessage(MessageProcessor processor) {
        model.removeOnMessage(processor);
    }

    /**
     * Runs a single generation
     *
     * @return CompletableFuture
     */
    public CompletableFuture<Void> runGeneration() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        try {
            if (model.getCurrentGeneration() == 0) {
                model.initialize();
                generateStartPopulation();
            } else {
                createNextGeneration();
            }
            simulateGeneration().thenAccept((res) -> {
                model.addGenerationResults(res);
                future.complete(null);
                saveBackup();
            }).exceptionally(ex -> {
                resetGeneration();
                future.completeExceptionally(ex);
                return null;
            });
        } catch (Throwable e) {
            e.printStackTrace();
            future.completeExceptionally(e);
        }
        return future;
    }

    /**
     * Run the generations in a loop
     */
    public void keepRunning() {
        runGeneration().thenAccept((v) -> {
            keepRunning();
        });
    }

    /**
     * Interrupt the execution
     */
    public void interrupt() {
        generationFuture.cancel(true);
    }

    public void sendWelcomeMessage() {
        model.sendMessage(new Message("updateModel", model));
    }

    /**
     * Generates the start population
     */
    public void generateStartPopulation() {
        Population pop = new Population();
        for (int i = 0; i < model.getCurrentGenerationParameters().getPopulationSize(); i++) {
            pop.add(ChromossomeFactory.random());
        }
        model.setCurrentPopulation(pop);
    }
    
    /**
     * Resets the generation
     */
    public void resetGeneration() {
        model.setState("idle");
        model.setCurrentGenerationProgress(0);
        model.decrementCurrentGeneration();
    }

    public CompletableFuture<GenerationResult> simulateGeneration() {
        model.setState("simulating");
        model.incrementCurrentGeneration();
        generationFuture = new CompletableFuture<>();
        List<IndividualResult> results = new ArrayList<>();
        double oldPct = 0;
        double i = 0;
        Population population = model.getCurrentPopulation();
        for (Chromossome chromossome : population.getChromossomes()) {
            i++;
            double pct = (i / population.size());
            if (pct != oldPct) {
                model.setCurrentGenerationProgress(pct);
                oldPct = pct;
            }
            if (chromossome.getResult() == null || chromossome.getResult().getAverage() <= 0) {
                CompletableFuture<IndividualResult> cFuture = new IndividualEvaluator().evaluate(chromossome);
                cFuture.thenAccept((r) -> {
                    chromossome.setResult(r);
                }).join();
            }
            if (generationFuture.isCancelled()) {
                generationFuture.completeExceptionally(new InterruptedException());
                return generationFuture;
            }
            results.add(chromossome.getResult());
        }
        double sum = results.stream().map((r) -> r.getAverage()).reduce(0d, (s, avg) -> s + avg);
        model.setState("idle");
        generationFuture.complete(new GenerationResult(model.getCurrentGeneration(), sum / population.size(), results));
        return generationFuture;
    }

    public void createNextGeneration() {
        try {
            GenerationParameters parameters = model.getCurrentGenerationParameters();
            List<Chromossome> sorted = sortChromossomesByFitness(model.getCurrentPopulation().getChromossomes());
            List<Chromossome> parentPool = getParentPool(sorted);
            Population newPopulation = new Population();

            // Keep the best
            newPopulation.add(sorted.get(sorted.size() - 1));

            int numberOfRandoms = newPopulation.size() + (int) (parameters.getRandomPercentage() * parameters.getPopulationSize());
            while (newPopulation.size() < numberOfRandoms) {
                addLimited(ChromossomeFactory.random(), newPopulation, parameters);
            }

            int numberOfCrossovers = newPopulation.size() + (int) (parameters.getCrossoverPercentage() * parameters.getPopulationSize());
            while (newPopulation.size() < numberOfCrossovers) {
                addLimited(ChromossomeFactory.singlePointCrossover(selectFittest(parentPool), selectFittest(parentPool)), newPopulation, parameters);
            }
            while (newPopulation.size() < parameters.getPopulationSize()) {
                Chromossome selected = selectFittest(parentPool);
                addLimited(ChromossomeFactory.mutate(selected, parameters.getMutationChance()), newPopulation, parameters);
            }

            model.setCurrentPopulation(newPopulation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adiciona um indivíduo de forma limitada a população
     *
     * @param population
     * @param chromossome
     * @param parameters
     */
    private void addLimited(Chromossome chromossome, Population population, GenerationParameters parameters) {
        int count = 0;
        for (Chromossome chromossome1 : population.getChromossomes()) {
            if (SpeciesMap.getSpecies(chromossome1).equals(SpeciesMap.getSpecies(chromossome))) {
                count++;
            }
        }
        if (count < parameters.getMaxSpeciesPercentage() * parameters.getPopulationSize()) {
            population.add(chromossome);
        } else {
            System.out.println(SpeciesMap.getSpecies(chromossome) + " has too many " + count);
        }
    }

    /**
     * Ordena os cromossomos pela sua função de avaliação
     *
     * @param chromossomes
     * @return List
     */
    private List<Chromossome> sortChromossomesByFitness(List<Chromossome> chromossomes) {
        chromossomes.sort((c1, c2) -> {
            if (c1 == null || c1.getResult() == null) {
                return -1;
            }
            if (c2 == null || c2.getResult() == null) {
                return 1;
            }
            if (c1.getResult().getAverage() > c2.getResult().getAverage()) {
                return 1;
            }
            if (c1.getResult().getAverage() < c2.getResult().getAverage()) {
                return -1;
            }
            return 0;
        });
        return chromossomes;
    }

    /**
     * Cria o Pool de chromossomos
     *
     * @param chromossomes
     * @return List
     */
    private List<Chromossome> getParentPool(List<Chromossome> chromossomes) {
        List<Chromossome> parentPool = new ArrayList<>();
        for (int i = 0; i < chromossomes.size(); i++) {
            for (int j = 0; j < i; j++) {
                parentPool.add(chromossomes.get(i));
            }
        }
        return parentPool;
    }

    /**
     * Seleciona os N indivíduos com maior Fitness
     *
     * @param parentPool
     * @param count
     * @return List
     */
    private List<Chromossome> selectFittests(List<Chromossome> parentPool, int count) {
        List<Chromossome> ret = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ret.add(parentPool.get((int) (Math.random() * parentPool.size())));
        }
        return ret;
    }

    /**
     * Seleciona um indivíduos com maior Fitness
     *
     * @param parentPool
     * @return List
     */
    private Chromossome selectFittest(List<Chromossome> parentPool) {
        return selectFittests(parentPool, 1).get(0);
    }
    
    /**
     * Inicializa a simulação
     */
    
    public void initialize() {
        this.model.initialize();
        interrupt();
    }
    
    /**
     * Salva um Backup
     */
    public void saveBackup() {
        save("Backup_" + System.currentTimeMillis() + ".model");
    }

    /**
     * Salva o modelo com o nome especificado
     *
     * @param fileName
     */
    public void save(String fileName) {
        try {
            Files.write(getPath(fileName), save());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retorna os bytes do modelo
     *
     * @return byte
     */
    public byte[] save() {
        return Compressor.compress(JsonHelper.get().toJson(model).getBytes());
    }

    /**
     * Carrega o modelo a partir de um arquivo
     *
     * @param fileName
     */
    public void load(String fileName) {
        try {
            load(Files.readAllBytes(getPath(fileName)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carrega o modelo a partir de um arquivo
     *
     * @param bytes
     */
    public void load(byte[] bytes) {
        try {
            String buffer = new String(Compressor.decompress(bytes));
            GeneticAlgorithmModel newModel = JsonHelper.get().fromJson(buffer, GeneticAlgorithmModel.class);
            this.model.initialize(newModel);
        } catch (Exception e) {
            
        }
    }

    /**
     * Retorna o path de um arquivo salvo
     *
     * @param fileName
     * @return String
     */
    private Path getPath(String fileName) {
        return Paths.get("c:\\users\\pichau\\Desktop\\" + fileName);
    }

}
