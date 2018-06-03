/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jouwee.tcc_projeto;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pichau
 */
public class GenerationResult {
    
    private final int number;
    private final double average;
    private final IndividualResult best;
    private final IndividualResult worst;
    private final List<IndividualResult> individuals;

    public GenerationResult(int number, double average, List<IndividualResult> individuals) {
        this.number = number;
        this.average = average;
        this.individuals = new ArrayList<>(individuals);
        this.individuals.sort((o1, o2) -> {
            if (o2.getAverage() < o1.getAverage()) {
                return -1;
            } 
            if (o2.getAverage() > o1.getAverage()) {
                return 1;
            } 
            return 0;
        });
        this.best = this.individuals.get(0);
        this.worst = this.individuals.get(this.individuals.size() - 1);
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
