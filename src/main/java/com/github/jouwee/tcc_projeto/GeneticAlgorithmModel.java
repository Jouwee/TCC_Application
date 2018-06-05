package com.github.jouwee.tcc_projeto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneticAlgorithmModel {

    private transient final List<MessageProcessor> messageProcessors;
    private int maxGenerations;
    private int populationSize;
    private int currentGeneration;
    private double currentGenerationProgress;
    private final List<GenerationResult> generationResults;

    public GeneticAlgorithmModel() {
        this.messageProcessors = new ArrayList<>();
        this.maxGenerations = 10;
        this.populationSize = 15;
        this.generationResults = new ArrayList<>();
    }

    public void initialize() {
        setCurrentGeneration(0);
        setCurrentGenerationProgress(0);
        clearGenerationResults();
    }

    public synchronized void onMessage(MessageProcessor listener) {
        this.messageProcessors.add(listener);
    }

    public synchronized void sendMessage(Message message) {
        sendMessage(JsonHelper.get().toJson(message));
    }

    public synchronized void sendMessage(String message) {
        for (MessageProcessor listener : messageProcessors) {
            listener.process(message);
        }
    }

    public void sendModelUpdate(String name, Object value) {
        Map message = new HashMap();
        message.put(name, value);
        sendMessage(new Message("updateModel", message));
    }

    public int getCurrentGeneration() {
        return this.currentGeneration;
    }

    public void setCurrentGeneration(int currentGeneration) {
        this.currentGeneration = currentGeneration;
        sendModelUpdate("currentGeneration", currentGeneration);
    }

    public void incrementCurrentGeneration() {
        setCurrentGeneration(getCurrentGeneration() + 1);
    }

    public void setMaxGenerations(int maxGenerations) {
        this.maxGenerations = maxGenerations;
        sendModelUpdate("maxGenerations", maxGenerations);
    }

    public int getMaxGenerations() {
        return this.maxGenerations;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
        sendModelUpdate("populationSize", populationSize);
    }

    public int getPopulationSize() {
        return this.populationSize;
    }

    public double getCurrentGenerationProgress() {
        return currentGenerationProgress;
    }

    public void setCurrentGenerationProgress(double currentGenerationProgress) {
        this.currentGenerationProgress = currentGenerationProgress;
        sendModelUpdate("currentGenerationProgress", currentGenerationProgress);
    }

    public void addGenerationResults(GenerationResult res) {
        generationResults.add(0, res);
        sendModelUpdate("generationResults", generationResults);
    }
    
    private void clearGenerationResults() {
        generationResults.clear();
        sendModelUpdate("generationResults", generationResults);
    }
}