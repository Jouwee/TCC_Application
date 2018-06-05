package com.github.jouwee.tcc_projeto;

/**
 * Result of an individual
 */
public class IndividualResult {
    
    /** Chromossome */
    private final Chromossome chromossome;
    /** Average value of the individual */
    private final double average;

    /**
     * Creates the result
     * 
     * @param chromossome
     * @param average 
     */
    public IndividualResult(Chromossome chromossome, double average) {
        this.average = average;
        this.chromossome = chromossome;
    }

    /**
     * Returns the average assertion value of the individual
     * 
     * @return double
     */
    public double getAverage() {
        return average;
    }

    /**
     * Returns the chromossome of this individual
     * 
     * @return Chromossome
     */
    public Chromossome getChromossome() {
        return chromossome;
    }
    
    @Override
    public String toString() {
        return "IndividualResult{" + "average=" + average + '}';
    }
    
}
