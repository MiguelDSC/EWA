import { TestBed } from '@angular/core/testing';

import { GreenhouseWebsocketService } from './greenhouse-websocket.service';

describe('GreenhouseWebsocketService', () => {
  let service: GreenhouseWebsocketService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GreenhouseWebsocketService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
