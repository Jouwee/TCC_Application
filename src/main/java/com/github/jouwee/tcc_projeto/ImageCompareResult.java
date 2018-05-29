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
public class ImageCompareResult {
    
    private final double correctPercentage;

    public ImageCompareResult(double pct) {
        this.correctPercentage = pct;
    }

    public double getCorrectPercentage() {
        return correctPercentage;
    }
    
    @Override
    public String toString() {
        return "ImageCompareResult{" + "correctPercentage=" + correctPercentage + '}';
    }
    
}
