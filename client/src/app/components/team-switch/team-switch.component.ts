import { Component, OnInit } from '@angular/core';
import { TeamSelectionService } from 'src/app/service/authentication/team-selection.service';

@Component({
  selector: 'app-team-switch',
  templateUrl: './team-switch.component.html',
  styleUrls: ['./team-switch.component.scss']
})
export class TeamSwitchComponent implements OnInit {

  teamList: any[];

  constructor(private teamSelection: TeamSelectionService) { }

  ngOnInit(): void {
    document.body.classList.add('green-body');
    
    this.teamSelection.getTeams(parseInt(localStorage.getItem("user"))).subscribe(data => this.teamList = data);
  }

  ngOnDestroy(): void {
    document.body.classList.remove('green-body');
  }

  selectTeam(teamId: number): void {
    this.teamSelection.authTeam(teamId);
  }
}
function jwt_decode(token: any): void {
  throw new Error('Function not implemented.');
}

