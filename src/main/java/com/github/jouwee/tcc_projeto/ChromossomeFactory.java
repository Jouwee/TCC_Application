/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jouwee.tcc_projeto;

import java.util.Random;
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
import visnode.pdi.process.PrewittProcess;
import visnode.pdi.process.RobertsProcess;
import visnode.pdi.process.RobinsonProcess;
import visnode.pdi.process.FloodFillProcess;
import visnode.pdi.process.SnakeProcess;
import visnode.pdi.process.SobelProcess;
import visnode.pdi.process.StentifordProcess;
import visnode.pdi.process.ThresholdLimitProcess;
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
        ThresholdLimitProcess.class,
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
        ZhangSuenProcess.class,
        StentifordProcess.class,
        HoltProcess.class,
        AverageBlurProcess.class,
        MedianBlurProcess.class,
        GaussianBlurProcess.class,
        FloodFillProcess.class,
        SnakeProcess.class,
        null
    };
    /** Maximum number of processes */
    public static final int MAX_PROCESSES = 15;
    /** Random */
    private static Random random = new Random(System.currentTimeMillis());
    
    /**
     * Cria um novo cromossomo aleatório
     * 
     * @return Chromossome
     */
    public static Chromossome random() {
        int size = MAX_PROCESSES * (1 + 5);
        Gene[] genes = new Gene[size];
        for (int i = 0; i < size;) {
            genes[i++] = new ProcessTypeGene(PROCESSES[(int)(random.nextDouble() * PROCESSES.length)]);
            genes[i++] = new NumericGene(random.nextDouble());
            genes[i++] = new NumericGene(random.nextDouble());
            genes[i++] = new NumericGene(random.nextDouble());
            genes[i++] = new NumericGene(random.nextDouble());
            genes[i++] = new NumericGene(random.nextDouble());
        }
        return new Chromossome(genes);
    }

    static Chromossome uniformCrossover(Chromossome parent1, Chromossome parent2) {
        Gene[] genes = new Gene[parent1.getGenes().length];
        for (int i = 0; i < genes.length; i++) {
            if (random.nextDouble() < 0.5) {
                genes[i] = parent1.getGenes()[i];
            } else {
                genes[i] = parent2.getGenes()[i];
            }
        }
        return new Chromossome(genes);
    }
    
    static Chromossome singlePointCrossover(Chromossome parent1, Chromossome parent2) {
        Gene[] genes = new Gene[parent1.getGenes().length];
        int cutPosition = random.nextInt(genes.length - 1) + 1;
        for (int i = 0; i < genes.length; i++) {
            if (i < cutPosition) {
                genes[i] = parent1.getGenes()[i];
            } else {
                genes[i] = parent2.getGenes()[i];
            }
        }
        return new Chromossome(genes);
    }
    
    static Chromossome doublePointCrossover(Chromossome parent1, Chromossome parent2) {
        Gene[] genes = new Gene[parent1.getGenes().length];
        int cutPosition1 = random.nextInt(genes.length - 2) + 1;
        int cutPosition2 = random.nextInt(genes.length - cutPosition1 - 1) + cutPosition1;
        for (int i = 0; i < genes.length; i++) {
            if (i < cutPosition1 || i > cutPosition2) {
                genes[i] = parent1.getGenes()[i];
            } else {
                genes[i] = parent2.getGenes()[i];
            }
        }
        return new Chromossome(genes);
    }

    static Chromossome mutate(Chromossome c, double rate) {
        Gene[] genes = new Gene[c.getGenes().length];
        for (int i = 0; i < genes.length; i++) {
            if (random.nextDouble() < rate) {
                if (c.getGenes()[i] instanceof ProcessTypeGene) {
                    genes[i] = new ProcessTypeGene(PROCESSES[(int)(random.nextDouble() * PROCESSES.length)]);
                } else {
                    genes[i] = new NumericGene(random.nextDouble());
                }
            } else {
                genes[i] = c.getGenes()[i];
            }
        }
        return new Chromossome(genes);
    }
    
}
