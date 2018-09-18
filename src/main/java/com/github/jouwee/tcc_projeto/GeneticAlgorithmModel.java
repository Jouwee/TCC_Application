package com.github.jouwee.tcc_projeto;

import com.github.jouwee.tcc_projeto.model.GenerationParameters;
import com.github.jouwee.tcc_projeto.model.SimulationParameters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneticAlgorithmModel {

    /** Parâmetros da simulação */
    private final SimulationParameters simulationParameters;
    /** População atual */
    private Population currentPopulation;
    private final List<GenerationResult> generationResults;
    private transient final List<MessageProcessor> messageProcessors;
    private int currentGeneration;
    private double currentGenerationProgress;
    private String state;

    public GeneticAlgorithmModel() {
        this.messageProcessors = new ArrayList<>();
        this.simulationParameters = SimulationParameters.create();
        this.generationResults = new ArrayList<>();
        this.state = "idle";
    }

    public void initialize() {
        setCurrentGeneration(0);
        setCurrentGenerationProgress(0);
        clearGenerationResults();
        state = "idle";
    }

    public void initialize(GeneticAlgorithmModel another) {
        setCurrentGeneration(another.currentGeneration);
        setCurrentGenerationProgress(0);
        clearGenerationResults();
        this.generationResults.addAll(another.generationResults);
        sendModelUpdate("generationResults", generationResults);
        setCurrentPopulation(another.getCurrentPopulation());
        state = "idle";
    }

    /**
     * Adiciona um listener de mensagens
     * 
     * @param listener 
     */
    public synchronized void onMessage(MessageProcessor listener) {
        this.messageProcessors.add(listener);
    }

    /**
     * Remove um listener de mensagens
     * 
     * @param listener 
     */
    public synchronized void removeOnMessage(MessageProcessor listener) {
        this.messageProcessors.remove(listener);
    }

    public synchronized void sendMessage(Message message) {
        sendMessage(JsonHelper.get().toJson(message));
    }

    public synchronized void sendMessage(String message) {
        for (MessageProcessor listener : new ArrayList<>(messageProcessors)) {
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
        if (currentGeneration >= 0) {
            this.currentGeneration = currentGeneration;
            sendModelUpdate("currentGeneration", currentGeneration);
        }
    }

    public void incrementCurrentGeneration() {
        setCurrentGeneration(getCurrentGeneration() + 1);
    }
    
    public void decrementCurrentGeneration() {
        setCurrentGeneration(getCurrentGeneration() - 1);
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        sendModelUpdate("state", state);
    }

    public Population getCurrentPopulation() {
        return currentPopulation;
    }

    public void setCurrentPopulation(Population currentPopulation) {
        this.currentPopulation = currentPopulation;
        sendModelUpdate("currentPopulation", currentPopulation);
    }
    
    /**
     * Retorna os parâmetros da geração atual
     * 
     * @return GenerationParameters
     */
    public GenerationParameters getCurrentGenerationParameters() {
        return simulationParameters.getForGeneration(currentGeneration);
    }
    
}