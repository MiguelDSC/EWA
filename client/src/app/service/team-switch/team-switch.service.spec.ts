import { TestBed } from '@angular/core/testing';

import { TeamSwitchService } from './team-switch.service';

describe('TeamSwitchService', () => {
  let service: TeamSwitchService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TeamSwitchService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
