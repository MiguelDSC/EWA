import { Component, HostListener, OnInit } from '@angular/core';
import * as d3 from 'd3';
import { forkJoin } from 'rxjs';
import { MeasurementInterface } from 'src/app/interfaces/measurement';
import { Chart } from 'src/app/models/data/chart/chart';
import { UserSettings } from 'src/app/models/userSettings';
import { GreenhouseService } from 'src/app/service/greenhouse-data-service/greenhouse.service';
import { SettingsService } from 'src/app/service/settings/settings.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  loadPage: boolean = false;

  measurements: any = {};

  titles: any = {
    "air-temp": "Air temperature",
    "co2-level": "CO2 level",
    "soil-temp": "Soil temperature",
    "water-ph": "Water pH",
    "soil-humidity": "Soil humidity",
    "air-humidity": "Air humidity"
  }

  graphMap: Map<string, boolean>;
  teamName: any = JSON.parse(localStorage.getItem("team"));

  constructor(private service: GreenhouseService, private settings: SettingsService) { }

  ngOnInit(): void {
    this.settings.getSettings(parseInt(localStorage.getItem("user"))).subscribe((data) => {
      this.graphMap = data.graphPreferences;
      this.loadPage = true;

      this.service.getCurrentMeasurments().subscribe(data => {
        this.measurements = data;
        this.generateCharts();
      });
    });
  }

  /**
   * Generate the charts.
   */
  @HostListener('window:resize')
  generateCharts() {
    forkJoin({
      airTemp: this.service.getAverages("hourly", "air-temp"),
      soilTemp: this.service.getAverages("hourly", "soil-temp"),
      waterPH: this.service.getAverages("hourly", "water-ph"),
      co2Level: this.service.getAverages("hourly", "co2-level"),
      soilHumidity: this.service.getAverages("hourly", "soil-humidity"),
      airHumidity: this.service.getAverages("hourly", "air-humidity")
    }).subscribe((d: any) => {

      if (this.graphMap.get("co2-level"))
        new Chart('.co2-level-graph', '.chart-container', d.co2Level, 300)
          .generatePlane(2500, "")
          .area("#6a0dad");

      if (this.graphMap.get("air-temp"))
        new Chart('.air-temp-graph', '.chart-container', d.airTemp, 300)
          .generatePlane(40, "")
          .area("#ADD8E6");

      if (this.graphMap.get("soil-temp"))
        new Chart('.soil-temp-graph', '.chart-container', d.soilTemp, 300)
          .generatePlane(40, "")
          .area("#9b7653");

      if (this.graphMap.get("water-ph"))
        new Chart('.water-ph-graph', '.chart-container', d.waterPH, 300)
          .generatePlane(10, "")
          .area("#0000FF");

      if (this.graphMap.get("soil-humidity"))
        new Chart('.soil-humidity-graph', '.chart-container', d.soilHumidity, 300)
          .generatePlane(100, "")
          .area("#9b7653");

      if (this.graphMap.get("air-humidity"))
        new Chart('.air-humidity-graph', '.chart-container', d.airHumidity, 300)
          .generatePlane(100, "")
          .area("#015c92");
    });
  }
}
