import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TeamSwitchComponent } from './team-switch.component';

describe('TeamSwitchComponent', () => {
  let component: TeamSwitchComponent;
  let fixture: ComponentFixture<TeamSwitchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TeamSwitchComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TeamSwitchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
