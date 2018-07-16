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
    /** Percentual de indivíduos que serão criados aleatóriamente */
    private final double randomPercentage;
    /** Percentual de indivíduos que serão criados com crossover */
    private final double crossoverPercentage;
    /** Percentual de indivíduos que serão criados com mutação */
    private final double mutationPercentage;
    /** Chance de mutar um gene (Se mutação) */
    private final double mutationChance;
    /** Percentual máximo da população que uma única espécie pode tomar */
    private final double maxSpeciesPercentage;

    /**
     * Cria os parâmetros da geração 
     * 
     * @param populationSize
     * @param randomPercentage
     * @param crossoverPercentage
     * @param mutationPercentage
     * @param mutationChance 
     * @param maxSpeciesPercentage 
     */
    public GenerationParameters(int populationSize, double randomPercentage, double crossoverPercentage, double mutationPercentage, double mutationChance, double maxSpeciesPercentage) {
        this.populationSize = populationSize;
        this.randomPercentage = randomPercentage;
        this.crossoverPercentage = crossoverPercentage;
        this.mutationPercentage = mutationPercentage;
        this.mutationChance = mutationChance;
        this.maxSpeciesPercentage = maxSpeciesPercentage;
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

    public double getRandomPercentage() {
        return randomPercentage;
    }

    public double getMaxSpeciesPercentage() {
        return maxSpeciesPercentage;
    }
    
}
