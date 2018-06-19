import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class SpeciesService {

  constructor() { }

  getSpecies(chromossome) {
    let dictionary = {
      "visnode.pdi.process.GrayscaleProcess": "Gr",
      "visnode.pdi.process.WeightedGrayscaleProcess": "We",
      "visnode.pdi.process.ThresholdProcess": "Th",
      "visnode.pdi.process.InvertColorProcess": "In",
      "visnode.pdi.process.OpeningProcess": "Op",
      "visnode.pdi.process.ClosingProcess": "Cl",
      "visnode.pdi.process.DilationProcess": "Di",
      "visnode.pdi.process.ErosionProcess": "Er",
      "visnode.pdi.process.BrightnessProcess": "Br",
      "visnode.pdi.process.ContrastProcess": "Co",
      "visnode.pdi.process.SobelProcess": "So",
      "visnode.pdi.process.RobertsProcess": "Ro",
      "visnode.pdi.process.RobinsonProcess": "Rn",
      "visnode.pdi.process.PrewittProcess": "Pr",
      "visnode.pdi.process.CannyProcess": "Ca",
      "visnode.pdi.process.SnakeProcess": "Sn",
      "visnode.pdi.process.ZhangSuenProcess": "Zh",
      "visnode.pdi.process.StentifordProcess": "St",
      "visnode.pdi.process.HoltProcess": "Ho",
      "visnode.pdi.process.AverageBlurProcess": "Av",
      "visnode.pdi.process.MedianBlurProcess": "Me",
      "visnode.pdi.process.GaussianBlurProcess": "Ga"
    }
    let species = "";
    for (let gene of chromossome) {
      if (gene == null) {
        break;
      }
      if (gene.startsWith("visnode")) {
        if (species.length > 0) {
          species += '-'
        }
        species += dictionary[gene];
        // species += gene.replace(/visnode.pdi.process.(.*)Process/, "$1");
      }
    }
    return species;
  }
  
}
