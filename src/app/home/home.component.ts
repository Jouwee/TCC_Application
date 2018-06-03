import { Component, OnInit } from '@angular/core';
import { ModelService } from '../model.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  constructor(public model: ModelService) { }

  ngOnInit() {
  }

}
