import { Component, OnInit } from '@angular/core';
import { Observable, Observer } from 'rxjs';
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
                var arrayBuffer = this.result,
                    array = new Uint8Array(arrayBuffer),
                    binaryString = String.fromCharCode.apply(null, array);
                model.open(binaryString);
            }
            reader.readAsArrayBuffer((<any>this).files[0]);
        }, false);
    }

    open() {
        document.getElementById('loadFile').click();
    }

}
