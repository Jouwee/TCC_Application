import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';

const SERVER_URL = 'http://localhost:8180/TCC_Projeto/rest/';
const SOCKET_SERVER_URL = 'ws://localhost:8180/TCC_Projeto/';
const ENDPOINT_URL = SOCKET_SERVER_URL + 'teste';

@Injectable({
  providedIn: 'root'
})
export class ModelService {

  model = {};
  socket;
  connectionStatus:string = "connecting";
  listeners: Array<any> = [];
  public webServiceHost = SERVER_URL;

  constructor(public http: HttpClient) {
    this.socket = new WebSocket(ENDPOINT_URL);
    this.socket.onmessage = (event) => {
      this.connectionStatus = "upToDate";
      try {
        let data = JSON.parse(event.data);      
        if (data.message == "updateModel") {
          for (let key in data.payload) {
            this[key] = data.payload[key];
            for (let l of this.listeners) {
              l(key, data.payload[key]);
            }
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

  addModelListener(listener) {
    this.listeners.push(listener);
  }

  send(message) {
    this.socket.send(JSON.stringify(message));
  }

  getOriginalImage(id) {
    return this.http.get(SERVER_URL + 'image/original/' + id)
      .pipe(map(res => (<any>res).payload));
  }

  getResultImage(id, chromossome) {
    return this.http.get(SERVER_URL + 'image/processed/' + id + "?" + JSON.stringify(chromossome))
      .pipe(map(res => (<any>res).payload));
  }

  open(binaryString) {
    this.http.post(SERVER_URL + 'simulation/load', binaryString).subscribe((x) => {});;
  }

}
