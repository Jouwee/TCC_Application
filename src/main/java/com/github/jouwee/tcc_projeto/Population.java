package com.github.jouwee.tcc_projeto;

import java.util.ArrayList;
import java.util.List;

/**
 * População
 */
public class Population {
    
    /** Cromossomos da população */
    private final List<Chromossome> chromossomes;

    /**
     * Cria uma nova população
     */
    public Population() {
        chromossomes = new ArrayList<>();
    }
    
    /**
     * Adiciona um cromossomo a população
     * 
     * @param chromossome 
     */
    public void add(Chromossome chromossome)  {
        chromossomes.add(chromossome);
    }
    
    /**
     * Retorna o tamanho da população
     * 
     * @return size
     */
    public int size() {
        return chromossomes.size();
    }

    /**
     * Retorna os cromossomos da população
     * 
     * @return List
     */
    public List<Chromossome> getChromossomes() {
        return chromossomes;
    }

    Chromossome remove(int j) {
        return chromossomes.remove(j);
    }

    void prepend(Chromossome best) {
        chromossomes.add(0, best);
    }
    
}
