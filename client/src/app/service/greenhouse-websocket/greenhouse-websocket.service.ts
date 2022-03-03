import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class GreenhouseWebsocketService {

  private socket: WebSocket;
  subject = new Subject<string>();

  public data: any[] = [];

  constructor() { }

  public connect(): void {
    this.socket = new WebSocket(`ws://${environment.api.substring(7)}/socket/greenhouse`);
    this.socket.onopen = () => console.log("Open");
    this.socket.onmessage = (msg) => {
      this.data.unshift(JSON.parse(msg.data));
      if (this.data.length > 10) this.data.pop();
    };
    this.socket.onerror = (err) => console.log(err);
    this.socket.onclose = () => console.log("Socket closed");    
  }

  public close(): void {
    console.log("Closing socket");
    this.socket.close();
    this.socket = null;
  }
}
