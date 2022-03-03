import { Component, HostListener, OnInit } from '@angular/core';
import { TimescaleInterface } from 'src/app/interfaces/timescale';
import { Chart } from 'src/app/models/data/chart/chart';
import { User } from "src/app/models/user.model";
import { StatisticsService } from 'src/app/service/statistics-service/statistics-service.service';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss']
})
export class AdminDashboardComponent implements OnInit {
  members: User[];
  todayApiCall: Number;
  todayLoginAmount: Number;

  constructor(private service: StatisticsService) {   }

  ngOnInit(): void {
    this.generateCharts();
  }

  @HostListener('window:resize')
  generateCharts() {
    /*this.service.getLogins().subscribe((d: TimescaleInterface[]) => {
      new Chart('.logins', '.chart-container', d, 250)
        .generatePlane(50, "")
        .line("#4ca46a", 3);
      this.todayLoginAmount = d[365].value;
    });

    this.service.getChanges().subscribe((d: TimescaleInterface[]) => {
      new Chart('.apicall', '.chart-container', d, 250)
        .generatePlane(25, "")
        .line("#4ca46a", 3);
      this.todayApiCall = d[365].value;
    });

    this.service.getChangesRoles().subscribe((d: any[]) => {
      new Chart('.apiteam', '.chart-container', d, 250).pie(125);
    });

    this.service.getCaptains().subscribe((d: Member[]) => {
      this.members = d;
    });*/
  }
}
