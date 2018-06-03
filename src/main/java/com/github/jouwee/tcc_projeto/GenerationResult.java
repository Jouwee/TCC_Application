/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jouwee.tcc_projeto;

/**
 *
 * @author Pichau
 */
public class GenerationResult {
    
    private final int number;
    private final double average;
    private final IndividualResult best;
    private final IndividualResult worst;

    public GenerationResult(int number, double average, IndividualResult best, IndividualResult worst) {
        this.number = number;
        this.average = average;
        this.best = best;
        this.worst = worst;
    }

    public double getAverage() {
        return average;
    }

    public int getNumber() {
        return number;
    }    

    @Override
    public String toString() {
        return "GenerationResult{" + "average=" + average + ',' + best +'}';
    }
    
    
    
}
