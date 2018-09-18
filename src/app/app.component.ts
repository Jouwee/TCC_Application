import { Component, OnInit } from '@angular/core';
import { ModelService } from './model.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

    constructor(public model: ModelService) { };

    ngOnInit() {
        let model = this.model;
        document.getElementById('loadFile').addEventListener('change', function () {
            var reader = new FileReader();
            reader.onload = function () {
                model.open(this.result);
            }
            reader.readAsArrayBuffer((<any>this).files[0]);
        }, false);
    }

    open() {
        document.getElementById('loadFile').click();
    }

    newSim() {
        this.model.newSimulation();
    }

}
