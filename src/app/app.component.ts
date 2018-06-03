import { Component, OnInit } from '@angular/core';
import { Observable, Observer } from 'rxjs';
import { ModelService } from './model.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  constructor(public model: ModelService) { };

}
