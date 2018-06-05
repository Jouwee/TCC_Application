package com.github.jouwee.tcc_projeto;

/**
 * Chromossome
 */
public class Chromossome {
    
    /** Genes */
    private final Gene[] genes;
    /** Result */
    private transient IndividualResult result;

    /**
     * Creates a new chromossome
     * 
     * @param genes 
     */
    public Chromossome(Gene[] genes) {
        this.genes = genes;
    }

    /**
     * Return the genes
     * 
     * @return Gene[]
     */
    public Gene[] getGenes() {
        return genes;
    }

    /**
     * Returns the chromossome result
     * 
     * @return IndividualResult
     */
    public IndividualResult getResult() {
        return result;
    }

    public void setResult(IndividualResult result) {
        this.result = result;
    }
    
    
    
}
