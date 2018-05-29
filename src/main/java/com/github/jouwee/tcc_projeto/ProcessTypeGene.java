/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jouwee.tcc_projeto;

import visnode.pdi.Process;

/**
 * Gene que representa o tipo de processo
 */
public class ProcessTypeGene implements Gene<Class> {

    /** Tipo de processo */
    private final Class processType;

    /**
     * Cria um novo gene
     * 
     * @param processType 
     */
    public ProcessTypeGene(Class processType) {
        this.processType = processType;
    }
    
    @Override
    public Class value() {
        return processType;
    }
    
}
