/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jouwee.tcc_projeto;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import visnode.pdi.process.AverageBlurProcess;
import visnode.pdi.process.BrightnessProcess;
import visnode.pdi.process.CannyProcess;
import visnode.pdi.process.ClosingProcess;
import visnode.pdi.process.ContrastProcess;
import visnode.pdi.process.DilationProcess;
import visnode.pdi.process.ErosionProcess;
import visnode.pdi.process.GaussianBlurProcess;
import visnode.pdi.process.GrayscaleProcess;
import visnode.pdi.process.HoltProcess;
import visnode.pdi.process.InvertColorProcess;
import visnode.pdi.process.MedianBlurProcess;
import visnode.pdi.process.OpeningProcess;
// import visnode.pdi.process.PointInPolygonFillProcess;
import visnode.pdi.process.PrewittProcess;
import visnode.pdi.process.RobertsProcess;
import visnode.pdi.process.RobinsonProcess;
import visnode.pdi.process.SnakeProcess;
import visnode.pdi.process.SobelProcess;
import visnode.pdi.process.StentifordProcess;
import visnode.pdi.process.ThresholdProcess;
import visnode.pdi.process.WeightedGrayscaleProcess;
import visnode.pdi.process.ZhangSuenProcess;

/**
 *
 * @author Pichau
 */
public class ChromossomeFactory {

    public static final Class[] PROCESSES = {
        GrayscaleProcess.class,
        WeightedGrayscaleProcess.class,
        ThresholdProcess.class,
        InvertColorProcess.class,
        OpeningProcess.class,
        ClosingProcess.class,
        DilationProcess.class,
        ErosionProcess.class,
        BrightnessProcess.class,
        ContrastProcess.class,
        SobelProcess.class,
        RobertsProcess.class,
        RobinsonProcess.class,
        PrewittProcess.class,
        CannyProcess.class,
        SnakeProcess.class,
        ZhangSuenProcess.class,
        StentifordProcess.class,
        HoltProcess.class,
        AverageBlurProcess.class,
        MedianBlurProcess.class,
        GaussianBlurProcess.class,
        // PointInPolygonFillProcess.class,
        null
    };
    /** Maximum number of processes */
    public static final int MAX_PROCESSES = 9;
    
    /**
     * Cria um novo cromossomo aleatório
     * 
     * @return Chromossome
     */
    public static Chromossome random() {
        int size = MAX_PROCESSES * (1 + 5);
        Gene[] genes = new Gene[size];
        for (int i = 0; i < size;) {
            genes[i++] = new ProcessTypeGene(PROCESSES[(int)(Math.random() * PROCESSES.length)]);
            genes[i++] = new NumericGene(Math.random());
            genes[i++] = new NumericGene(Math.random());
            genes[i++] = new NumericGene(Math.random());
            genes[i++] = new NumericGene(Math.random());
            genes[i++] = new NumericGene(Math.random());
        }
        return new Chromossome(genes);
    }
    
    /**
     * Returns a chromossome from a message
     * 
     * @param list
     * @return Chromossome
     */
    public static Chromossome fromMessage(ArrayList<Object> list) {
        return new Chromossome(list.stream()
                .map((object) -> geneFromObject(object))
                .collect(Collectors.toList()).toArray(new Gene[] {}));
    }
    
    private static Gene geneFromObject(Object object) {
        try {
            return new NumericGene(Double.parseDouble(object.toString()));
        } catch (NumberFormatException e) {
            try {
                return new ProcessTypeGene(Class.forName(object.toString()));
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ChromossomeFactory.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
    }

    static Chromossome uniformCrossover(Chromossome parent1, Chromossome parent2) {
        Gene[] genes = new Gene[parent1.getGenes().length];
        for (int i = 0; i < genes.length; i++) {
            if (Math.random() < 0.5) {
                genes[i] = parent1.getGenes()[i];
            } else {
                genes[i] = parent2.getGenes()[i];
            }
        }
        return new Chromossome(genes);
    }

    static Chromossome mutate(Chromossome c) {
        Gene[] genes = new Gene[c.getGenes().length];
        for (int i = 0; i < genes.length; i++) {
            if (Math.random() < 0.005) {
                if (c.getGenes()[i] instanceof ProcessTypeGene) {
                    genes[i] = new ProcessTypeGene(PROCESSES[(int)(Math.random() * PROCESSES.length)]);
                } else {
                    genes[i] = new NumericGene(Math.random());
                }
            } else {
                genes[i] = c.getGenes()[i];
            }
        }
        return new Chromossome(genes);
    }
    
}
