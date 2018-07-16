/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jouwee.tcc_projeto.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parâmetros para toda a simulação
 */
public class SimulationParameters {
    
    /** Tamanho da população */
    private static final int POP_SIZE = 50;
    /** Parâmetros por geração */
    private final Map<Integer, GenerationParameters> parametersPerGeneration;

    /**
     * Cria os parâmetros default da simulação
     * 
     * @return SimulationParameters
     */
    public static SimulationParameters create() {
        SimulationParameters parameters = new SimulationParameters();
        parameters.put(1, new GenerationParameters(POP_SIZE, 0, 0, 0, 0, 0.15));
        parameters.put(5, new GenerationParameters(POP_SIZE, 0.1, 0.75, 0.05, 0.01, 0.15));
        parameters.put(10, new GenerationParameters(POP_SIZE, 0.1, 0.70, 0.10, 0.01, 0.15));
        parameters.put(20, new GenerationParameters(POP_SIZE, 0.1, 0.60, 0.20, 0.01, 0.15));
        parameters.put(30, new GenerationParameters(POP_SIZE, 0.1, 0.50, 0.30, 0.01, 0.15));
        parameters.put(40, new GenerationParameters(POP_SIZE, 0.1, 0.40, 0.40, 0.01, 0.15));
        parameters.put(50, new GenerationParameters(POP_SIZE, 0.1, 0.30, 0.50, 0.01, 0.15));
        parameters.put(60, new GenerationParameters(POP_SIZE, 0.1, 0.20, 0.60, 0.01, 0.15));
        parameters.put(99999, new GenerationParameters(POP_SIZE, 0.1, 0.10, 0.70, 0.01, 0.15));
        return parameters;
    }
    
    /**
     * Cria os parâmetros da simulação
     */
    public SimulationParameters() {
        this.parametersPerGeneration = new HashMap<>();
    }
    
    /**
     * Adiciona parâmetros de uma geração
     * 
     * @param untilGeneration
     * @param parameters 
     */
    public void put(int untilGeneration, GenerationParameters parameters) {
        parametersPerGeneration.put(untilGeneration, parameters);
    }
    
    /**
     * Retorna os parâmetros para uma geração
     * 
     * @param generationNumber
     * @return GenerationParameters
     */
    public GenerationParameters getForGeneration(int generationNumber) {
        for (Integer untilGeneration : getKeys()) {
            if (generationNumber < untilGeneration) {
                return parametersPerGeneration.get(untilGeneration);
            }
        }
        return null;
    }
    
    /**
     * Retorna as chaves ordenadas
     * 
     * @return 
     */
    private Collection<Integer> getKeys() {
        List<Integer> keys = new ArrayList<>(parametersPerGeneration.keySet());
        Collections.sort(keys);
        return keys;
    }
    
}
