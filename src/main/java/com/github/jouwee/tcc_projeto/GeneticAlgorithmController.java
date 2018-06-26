package com.github.jouwee.tcc_projeto;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import visnode.application.NodeNetwork;
import visnode.application.parser.NodeNetworkParser;

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
        for (int i = 0; i < model.getPopulationSize(); i++) {
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
            double pct = (i / model.getPopulationSize());
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
        List<Chromossome> chromossomes = currentPopulation.getChromossomes();
        chromossomes.sort((c1, c2) -> {
            if (c1.getResult().getAverage() > c2.getResult().getAverage()) {
                return 1;
            }
            if (c1.getResult().getAverage() < c2.getResult().getAverage()) {
                return -1;
            }
            return 0;
        });
        
        for (int i = 0; i < chromossomes.size(); i++) {
            int gen = model.getCurrentGeneration();
            Chromossome c = chromossomes.get(i);
            NodeNetwork network = new ChromossomeNetworkConverter(true).convert(c);
            NodeNetworkParser parser = new NodeNetworkParser();
            String path = System.getProperty("user.home") + "/Desktop/gens/" + gen + "/";
            try {
                Files.createDirectories(Paths.get(path));
                try (PrintWriter writer = new PrintWriter(new File(path + i + "_" + c.getResult().getAverage() + ".vnp"), "UTF-8")) {
                    writer.print(parser.toJson(network));
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        Population newPopulation = new Population();
        for (Chromossome chromossome : chromossomes) {
            newPopulation.add(chromossome);
        }
        
        Chromossome best = newPopulation.remove(newPopulation.size() - 1);
        
        // Kills 80% of the population
        for (int i = 0; i < model.getPopulationSize() * 0.7; i++) {
            for (int j = 0; j < newPopulation.size(); j++) {
                if (Math.random() > 0.5) {
                    newPopulation.remove(j);
                    break;
                }
            }
        }
        // make sure the best survives
        newPopulation.add(best);
        
        
        List<Chromossome> parentPool = new ArrayList<>();
        for (int i = 0; i < chromossomes.size(); i++) {
            for (int j = 0; j < i; j++) {
                parentPool.add(chromossomes.get(i));
            }
        }        
        
        for (int i = newPopulation.size(); i < model.getPopulationSize(); i++) {
            Chromossome parent1 = parentPool.get((int) (Math.random() * parentPool.size()));
            Chromossome parent2 = parentPool.get((int) (Math.random() * parentPool.size()));
            Chromossome child = ChromossomeFactory.uniformCrossover(parent1, parent2);
            child = ChromossomeFactory.mutate(child);
            newPopulation.add(child);
        }
        currentPopulation = newPopulation;
    }

}
