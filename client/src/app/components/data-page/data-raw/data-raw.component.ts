import { Component, OnInit } from '@angular/core';
import { GreenhouseWebsocketService } from 'src/app/service/greenhouse-websocket/greenhouse-websocket.service';

@Component({
  selector: 'app-data-raw',
  templateUrl: './data-raw.component.html',
  styleUrls: ['./data-raw.component.scss']
})
export class DataRawComponent implements OnInit {

  public data: any[];

  constructor(private socket: GreenhouseWebsocketService) { 
    this.socket.connect();
    this.data = this.socket.data;
  }

  ngOnInit(): void {
    
  }

  ngAfterViewInit(): void { 

  }

  ngOnDestroy(): void {
    this.socket.close();
  }

  arrToTime(data: any): string {
    return `${data[0]}-${data[1]}-${data[2]} ${data[3]}:${data[4]}:${data[5]}`;
  }
}
