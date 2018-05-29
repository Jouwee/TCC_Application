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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GeneticAlgorithmController {
    
    private static GeneticAlgorithmController instance;

    int SIZE = 5;
    int MAX_GENS = 5;
    
    private Population currentPopulation;
    private final GeneticAlgorithmModel model;

    public GeneticAlgorithmController() {
        this.model = new GeneticAlgorithmModel();
    }

    public synchronized static GeneticAlgorithmController get() {
        if (instance == null) {
            instance = new GeneticAlgorithmController();
            instance.startUp();
        }        
        return instance;
    }

    public void onMessage(MessageProcessor processor) {
        model.onMessage(processor);
    }

    public void startUp() {
        try {
            model.initialize();
            generateStartPopulation();
            runGenerations();
        } catch (Throwable e) {
            e.printStackTrace();
            model.sendMessage(e.getMessage());
        }
    }

    private ExecutorService pool = Executors.newSingleThreadExecutor();
    long l;
    
    public void runGenerations() {
        l = System.currentTimeMillis();
        model.sendMessage("start");
        simulateGeneration().thenAccept((res) -> {
            if (isDone()) {
                model.sendMessage("finish");
                return;
            }
            model.sendMessage("Generation " + model.getCurrentGeneration() + " " + (System.currentTimeMillis() - l) + "ms");
            model.sendMessage(res.toString());
            pool.submit(() -> loop());
        });
    }
    
    public void loop() {
        createNextGeneration();
        runGenerations();
    }

    public void sendWelcomeMessage() {
        model.sendMessage("welcome");
    }

    public void generateStartPopulation() {
        currentPopulation = new Population();
        for (int i = 0; i < SIZE; i++) {
            currentPopulation.add(ChromossomeFactory.random());
        }
    }

    public CompletableFuture<GenerationResult> simulateGeneration() {
        CompletableFuture<GenerationResult> future = new CompletableFuture<>();
        List<CompletableFuture> futures = new ArrayList<>();
        List<IndividualResult> results = new ArrayList<>();
        System.out.println("Simulating individuals...");
        System.out.print("%");
        int oldPct = 0;
        double i = 0;
        for (Chromossome chromossome : currentPopulation.getChromossomes()) {
            
            int pct = (int) ((i / SIZE) * 100);
            if (pct != oldPct) {
                if (pct % 5 == 0) {
                    System.out.print(pct);
                } else {
                    System.out.print(".");
                }
                oldPct = pct;
            }
            i++;
            CompletableFuture<IndividualResult> cFuture = new IndividualEvaluator().evaluate(chromossome);
            futures.add(cFuture);
            cFuture.thenAccept((r) -> {
                chromossome.setResult(r);
                results.add(r);
            }).join();
        }
        System.out.println("");
        CompletableFuture.runAsync(() -> {
            for (CompletableFuture future1 : futures) {
                future1.join();
            }
            double sum = 0;
            IndividualResult best = null;
            for (IndividualResult result : results) {
                sum += result.getAverage();
                if (best == null || best.getAverage() < result.getAverage()) {
                    best = result;
                }
            }
            future.complete(new GenerationResult(sum / currentPopulation.size(), best));
        });
        return future;
    }
    
    public boolean isDone() {
        return model.getCurrentGeneration() >= MAX_GENS;
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
        for (int i = 0; i < SIZE * 0.7; i++) {
            for (int j = 0; j < newPopulation.size(); j++) {
                if (Math.random() > 0.5) {
                    newPopulation.remove(j);
                    break;
                }
            }
        }
        // make sure the best survives
        newPopulation.add(best);
        
        System.out.println("after remove " + newPopulation.size());
        
        
        List<Chromossome> parentPool = new ArrayList<>();
        for (int i = 0; i < chromossomes.size(); i++) {
            for (int j = 0; j < i; j++) {
                parentPool.add(chromossomes.get(i));
            }
        }        
        
        for (int i = newPopulation.size(); i < SIZE; i++) {
            Chromossome parent1 = parentPool.get((int) (Math.random() * parentPool.size()));
            Chromossome parent2 = parentPool.get((int) (Math.random() * parentPool.size()));
            Chromossome child = ChromossomeFactory.uniformCrossover(parent1, parent2);
            child = ChromossomeFactory.mutate(child);
            newPopulation.add(child);
        }
        currentPopulation = newPopulation;
        System.out.println("final size " + currentPopulation.size());

        model.incrementCurrentGeneration();
    }
    
}
