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
    
    private double average;
    private IndividualResult best;

    public GenerationResult(double average, IndividualResult best) {
        this.average = average;
        this.best = best;
    }

    public double getAverage() {
        return average;
    }

    @Override
    public String toString() {
        return "GenerationResult{" + "average=" + average + ',' + best +'}';
    }
    
    
    
}
