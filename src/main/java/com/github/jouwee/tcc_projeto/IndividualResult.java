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
public class IndividualResult {
    
    public double average;

    public IndividualResult(double average) {
        this.average = average;
    }

    public double getAverage() {
        return average;
    }

    @Override
    public String toString() {
        return "IndividualResult{" + "average=" + average + '}';
    }
    
}
