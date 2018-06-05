import { Injectable } from '@angular/core';

const SERVER_URL = 'ws://localhost:8180/TCC_Projeto/teste';

@Injectable({
  providedIn: 'root'
})
export class ModelService {

  model = {};
  socket;
  connectionStatus:string = "connecting";

  constructor() {
    this.socket = new WebSocket(SERVER_URL);
    this.socket.onmessage = (event) => {
      this.connectionStatus = "upToDate";
      try {
        let data = JSON.parse(event.data);      
        if (data.message == "updateModel") {
          for (let key in data.payload) {
            this[key] = data.payload[key];
          }
        }
      } catch (e) {
        console.log(e);
      }
    }

    this.socket.onopen = (event) => {
      this.connectionStatus = "connected";
    }  
  }

  send(message) {
    this.socket.send(JSON.stringify(message));
  }

}
