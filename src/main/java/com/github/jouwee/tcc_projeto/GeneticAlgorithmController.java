package com.github.jouwee.tcc_projeto;

import com.github.jouwee.tcc_projeto.model.GenerationParameters;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GeneticAlgorithmController {
    
    private static GeneticAlgorithmController instance;
    private Population currentPopulation;
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

    public void onMessage(MessageProcessor processor) {
        model.onMessage(processor);
    }
    
    /**
     * Runs a single generation
     * 
     * @return CompletableFuture
     */
    public CompletableFuture<Void> runGeneration() {
        if (model.getCurrentGeneration() == 0) {
            model.initialize();
            generateStartPopulation();
        } else {
            createNextGeneration();
        }
        try {
            return simulateGeneration().thenAccept((res) -> {
                model.addGenerationResults(res);
            });
        } catch (Throwable e) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }
    
    /**
     * Executa as simulações em Loop
     */
    public void keepRunning() {
        runGeneration().thenAccept((v) -> {
           keepRunning();
        });
    }
    
    public void interrupt() {
        generationFuture.cancel(true);
    }

    public void sendWelcomeMessage() {
        model.sendMessage(new Message("updateModel", model));
    }

    public void generateStartPopulation() {
        currentPopulation = new Population();
        for (int i = 0; i < model.getCurrentGenerationParameters().getPopulationSize(); i++) {
            currentPopulation.add(ChromossomeFactory.random());
        }
    }

    public CompletableFuture<GenerationResult> simulateGeneration() {
        model.setState("simulating");
        model.incrementCurrentGeneration();
        generationFuture = new CompletableFuture<>();
        List<CompletableFuture> futures = new ArrayList<>();
        List<IndividualResult> results = new ArrayList<>();
        double oldPct = 0;
        double i = 0;
        for (Chromossome chromossome : currentPopulation.getChromossomes()) {
            i++;
            double pct = (i / currentPopulation.size());
            if (pct != oldPct) {
                model.setCurrentGenerationProgress(pct);
                oldPct = pct;
            }
            CompletableFuture<IndividualResult> cFuture = new IndividualEvaluator().evaluate(chromossome);
            futures.add(cFuture);
            cFuture.thenAccept((r) -> {
                chromossome.setResult(r);
                results.add(r);
            }).join();
        }
        CompletableFuture.runAsync(() -> {
            futures.forEach((future1) -> {
                future1.join();
            });
            double sum = results.stream().map((r) -> r.getAverage()).reduce(0d, (s, avg) -> s + avg);
            model.setState("idle");
            generationFuture.complete(new GenerationResult(model.getCurrentGeneration(), sum / currentPopulation.size(), results));
        });
        return generationFuture;
    }
    
    public void createNextGeneration() {
        GenerationParameters parameters = model.getCurrentGenerationParameters();
        List<Chromossome> parentPool = getParentPool(sortChromossomesByFitness(currentPopulation.getChromossomes()));
        Population newPopulation = new Population();
       
        int numberOfMutations = (int) (parameters.getMutationPercentage() * parameters.getPopulationSize());
        for (Chromossome selected : selectFittests(parentPool, numberOfMutations)) {
            newPopulation.add(ChromossomeFactory.mutate(selected, parameters.getMutationChance()));
        }
        
        int numberOfCrossovers = (int) (parameters.getCrossoverPercentage() * parameters.getPopulationSize());
        for (int i = 0; i < numberOfCrossovers; i++) {
            newPopulation.add(ChromossomeFactory.uniformCrossover(selectFittest(parentPool), selectFittest(parentPool)));
        }
        
        int numberOfSurvivors = parameters.getPopulationSize() - newPopulation.size();
        for (Chromossome selected : selectFittests(parentPool, numberOfSurvivors)) {
            newPopulation.add(selected);
        }

        currentPopulation = newPopulation;
    }
    
    /**
     * Ordena os cromossomos pela sua função de avaliação
     * 
     * @param chromossomes
     * @return List
     */
    private List<Chromossome> sortChromossomesByFitness(List<Chromossome> chromossomes) {
        chromossomes.sort((c1, c2) -> {
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

}
