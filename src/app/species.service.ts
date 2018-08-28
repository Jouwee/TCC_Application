import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class SpeciesService {

  constructor() { }

  getSpecies(chromossome) {
    let dictionary = {
      "Grayscale": "Gr",
      "WeightedGrayscale": "We",
      "Threshold": "Th",
      "InvertColor": "In",
      "Opening": "Op",
      "Closing": "Cl",
      "Dilation": "Di",
      "Erosion": "Er",
      "Brightness": "Br",
      "Contrast": "Co",
      "Sobel": "So",
      "Roberts": "Ro",
      "Robinson": "Rn",
      "Prewitt": "Pr",
      "Canny": "Ca",
      "Snake": "Sn",
      "ZhangSuen": "Zh",
      "Stentiford": "St",
      "Holt": "Ho",
      "AverageBlur": "Av",
      "MedianBlur": "Me",
      "GaussianBlur": "Ga",
      'FloodFill': 'Ff', 
      'ThresholdLimit': 'Tl'
    }
    let species = "";
    for (let gene of chromossome) {
      if (gene == null) {
        break;
      }
      if (isNaN(gene)) {
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
