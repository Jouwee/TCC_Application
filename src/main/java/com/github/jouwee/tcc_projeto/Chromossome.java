/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jouwee.tcc_projeto;

import org.paim.commons.Image;

/**
 *
 * @author Pichau
 */
public class Chromossome {
    
    private Gene[] genes;
    private IndividualResult result;

    /**
     * Cria um novo chromossomo com os genes informados
     * 
     * @param genes 
     */
    public Chromossome(Gene[] genes) {
        this.genes = genes;
    }

    public Gene[] getGenes() {
        return genes;
    }

    public IndividualResult getResult() {
        return result;
    }

    public void setResult(IndividualResult result) {
        this.result = result;
    }
    
    
    
}
