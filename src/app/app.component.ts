import { Component, OnInit } from '@angular/core';
import { Observable, Observer } from 'rxjs';

const SERVER_URL = 'ws://localhost:8180/TCC_Projeto/teste';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'app';
  model = {};
  socket;
  connectionStatus:string = "connecting";

  ngOnInit() {
    this.socket = new WebSocket(SERVER_URL);
    this.socket.onmessage = (event) => {
      this.connectionStatus = "upToDate";
      try {
        let data = JSON.parse(event.data);      
        if (data.message == "updateModel") {
          for (let key in data.payload) {
            this.model[key] = data.payload[key];
          }
          console.log(this.model);
        }
        console.log(data);
      } catch (e) {
        console.log(event.data);
      }
    }

    this.socket.onopen = (event) => {
      this.connectionStatus = "connected";
    }  
  }

  start() {
    this.send({message: "start"});
  }

  send(message) {
    this.socket.send(JSON.stringify(message));
  }

}
