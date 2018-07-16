/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jouwee.tcc_projeto.model;

import com.github.jouwee.tcc_projeto.Chromossome;
import com.github.jouwee.tcc_projeto.Gene;
import com.github.jouwee.tcc_projeto.ProcessTypeGene;
import java.util.HashMap;
import java.util.Map;
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
import visnode.pdi.process.SnakeProcess;
import visnode.pdi.process.SobelProcess;
import visnode.pdi.process.StentifordProcess;
import visnode.pdi.process.ThresholdProcess;
import visnode.pdi.process.WeightedGrayscaleProcess;
import visnode.pdi.process.ZhangSuenProcess;

/**
 * Mapa de espécies
 */
public class SpeciesMap {
    
    /** Mapa de espécies */
    private static final Map<Class, String> SPECIES_MAP = new HashMap<>();
    static {
        SPECIES_MAP.put(GrayscaleProcess.class, "Gr");
        SPECIES_MAP.put(WeightedGrayscaleProcess.class, "We");
        SPECIES_MAP.put(ThresholdProcess.class, "Th");
        SPECIES_MAP.put(InvertColorProcess.class, "In");
        SPECIES_MAP.put(OpeningProcess.class, "Op");
        SPECIES_MAP.put(ClosingProcess.class, "Cl");
        SPECIES_MAP.put(DilationProcess.class, "Di");
        SPECIES_MAP.put(ErosionProcess.class, "Er");
        SPECIES_MAP.put(BrightnessProcess.class, "Br");
        SPECIES_MAP.put(ContrastProcess.class, "Co");
        SPECIES_MAP.put(SobelProcess.class, "So");
        SPECIES_MAP.put(RobertsProcess.class, "Ro");
        SPECIES_MAP.put(RobinsonProcess.class, "Rn");
        SPECIES_MAP.put(PrewittProcess.class, "Pr");
        SPECIES_MAP.put(CannyProcess.class, "Ca");
        SPECIES_MAP.put(SnakeProcess.class, "Sn");
        SPECIES_MAP.put(ZhangSuenProcess.class, "Zh");
        SPECIES_MAP.put(StentifordProcess.class, "St");
        SPECIES_MAP.put(HoltProcess.class, "Ho");
        SPECIES_MAP.put(AverageBlurProcess.class, "Av");
        SPECIES_MAP.put(MedianBlurProcess.class, "Me");
        SPECIES_MAP.put(GaussianBlurProcess.class, "Ga");
    }
    
    /**
     * Retorna a espécie completa de um indivíduo
     * 
     * @param chromossome
     * @return String
     */
    public static String getSpecies(Chromossome chromossome) {
        String species = "";
        for (Gene gene : chromossome.getGenes()) {
            if (gene == null || gene.value() == null) {
                break;
            }
            if (gene instanceof ProcessTypeGene) {
                ProcessTypeGene pgene = (ProcessTypeGene) gene;
                if (species.length() > 0) {
                    species += '-';
                }
                species += abbreviate(pgene.value());
            }
        }
        return species;
    }
    
    /**
     * Abrevia uma classe
     * 
     * @param clazz
     * @return String
     */
    public static String abbreviate(Class clazz) {
        return SPECIES_MAP.get(clazz);
    }

}
