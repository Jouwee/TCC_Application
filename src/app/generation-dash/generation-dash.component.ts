import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ModelService } from '../model.service';
import { SpeciesService } from '../species.service';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-generation-dash',
  templateUrl: './generation-dash.component.html',
  styleUrls: ['./generation-dash.component.css']
})
export class GenerationDashComponent implements OnInit, OnDestroy {

  number: number;
  generation;
  private sub: any;
  public image;

  constructor(private route: ActivatedRoute, public model: ModelService, private species: SpeciesService, private _sanitizer: DomSanitizer) {}

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      this.number = +params['number'];
      let gens = this.model["generationResults"];
      this.generation = gens[gens.length - this.number];
    });
  }

  loadImages() {
    this.generation.individuals.forEach(individual => {
      this.model.getResultImage('1', individual.chromossome).subscribe((i) => {
        individual.image = this._sanitizer.bypassSecurityTrustResourceUrl('data:image/jpg;base64,' + i);
      });
    });
  }

  open(chromossome) {
    this.model.send({message: "openChromossome", payload: chromossome});
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

}
