/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jouwee.tcc_projeto;

/**
 * Gene numérico
 */
public class NumericGene implements Gene<Double> {

    /** Value */
    final Double value;

    /**
     * Cria um novo gene numérico
     * 
     * @param value 
     */
    public NumericGene(Double value) {
        this.value = value;
    }
    
    @Override
    public Double value() {
        return value;
    }
    
}
