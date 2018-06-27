/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jouwee.tcc_projeto.model;

/**
 * Parâmetros utilizados para simulação criação de uma nova geração
 */
public final class GenerationParameters {
    
    /** Tamanho da população */
    private final int populationSize;
    /** Percentual de indivíduos que serão criados com crossover */
    private final double crossoverPercentage;
    /** Percentual de indivíduos que serão criados com mutação */
    private final double mutationPercentage;
    /** Chance de mutar um gene (Se mutação) */
    private final double mutationChance;

    /**
     * Cria os parâmetros da geração 
     * 
     * @param populationSize
     * @param crossoverPercentage
     * @param mutationPercentage
     * @param mutationChance 
     */
    public GenerationParameters(int populationSize, double crossoverPercentage, double mutationPercentage, double mutationChance) {
        this.populationSize = populationSize;
        this.crossoverPercentage = crossoverPercentage;
        this.mutationPercentage = mutationPercentage;
        this.mutationChance = mutationChance;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public double getCrossoverPercentage() {
        return crossoverPercentage;
    }

    public double getMutationPercentage() {
        return mutationPercentage;
    }

    public double getSurvivalPercentage() {
        return 1 - mutationPercentage - crossoverPercentage;
    }

    public double getMutationChance() {
        return mutationChance;
    }
    
}
