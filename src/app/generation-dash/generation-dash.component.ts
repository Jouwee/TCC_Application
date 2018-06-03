import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ModelService } from '../model.service';

@Component({
  selector: 'app-generation-dash',
  templateUrl: './generation-dash.component.html',
  styleUrls: ['./generation-dash.component.css']
})
export class GenerationDashComponent implements OnInit, OnDestroy {

  number: number;
  generation;
  private sub: any;

  constructor(private route: ActivatedRoute, public model: ModelService) {}

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      this.number = +params['number'];
      let gens = this.model["generationResults"];
      this.generation = gens[this.number];
    });
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

}
