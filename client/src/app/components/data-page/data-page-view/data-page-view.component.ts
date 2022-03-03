import { Component, HostListener, OnInit } from '@angular/core';
import { Chart } from 'src/app/models/data/chart/chart';
import noUiSlider from 'node_modules/nouislider/dist/nouislider';
import { HttpClient } from '@angular/common/http';
import { GreenhouseService } from 'src/app/service/greenhouse-data-service/greenhouse.service';
import { ActivatedRoute, Route, Router } from '@angular/router';
import { TimescaleInterface } from 'src/app/interfaces/timescale';
import { forkJoin, Observable, Observer } from 'rxjs';

@Component({
  selector: 'app-data-page-view',
  templateUrl: './data-page-view.component.html',
  styleUrls: ['./data-page-view.component.scss']
})
export class DataPageViewComponent implements OnInit {

  private time = new Set<string>(["daily", "hourly"]);

  originalSet: any = [];
  searchSet: any = [];

  loaded: boolean = false;

  parsedUrl: string;
  selected: string = "co2-level";
  type: string;

  tabs: any = [
    { title: "CO2 level", target: "co2-level" },
    { title: "Air temperature", target: "air-temp" },
    { title: "Soil temperature", target: "soil-temp" },
    { title: "Water pH", target: "water-ph" },
    { title: "Air humidity", target: "air-humidity" },
    { title: "Soil humidity", target: "soil-humidity" }
  ];

  dateFormat(date: any) {
    const options = (this.type == "daily") ? 
      { year: 'numeric', month: 'long', day: 'numeric' } : 
      { year: 'numeric', month: 'long', day: 'numeric',  hour: 'numeric', minute: 'numeric'};
    return date.toLocaleDateString("en-US", options);
  }

  constructor(private service: GreenhouseService, private route: ActivatedRoute, public router: Router) {    
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
  }

  ngOnInit(): void { 
    this.route.params.subscribe(params => {
      if (!params['set'] || !params['time'] || !this.time.has(params['time'])) {
        this.router.navigate(['404']);
        return;
      }
        
      this.selected = params['set'];
      this.type = params['time'];
    });

    const rawUrl = this.router.url.split("/");
    this.parsedUrl = `/${rawUrl[1]}/${rawUrl[2]}`;
  }

  /**
   * Only load after the view is initialized.
   */
  ngAfterViewInit(): void {
    this.route.params.subscribe(params => {
      if (params['time'] == "daily") {
        this.service.getReplay(0).subscribe(data => {  
          this.searchSet = this.originalSet = data;
          this.chartConstructor();
          this.makeSlider();
        });   
      } else {
        this.service.getReplay(1).subscribe(data => {  
          this.searchSet = this.originalSet = data;
          this.chartConstructor();
          this.makeSlider();
        });
      }
    });
  }

  /**
   * Generate charts.
   */
  chartConstructor() {
    new Chart('.co2-level-chart', '.tab-content', this.searchSet.co2Level, 500)
      .generatePlane(2500, "")
      .area("#6a0dad")
      .labeler(" ppm");

    new Chart('.air-temp-chart', '.tab-content', this.searchSet.airTemp, 500)
      .generatePlane(40, "")
      .line("url(#line-gradient)", 3)
      .labeler("°C");

    new Chart('.soil-temp-chart', '.tab-content', this.searchSet.soilTemp, 500)
      .generatePlane(40, "")
      .line("#654321", 3)
      .labeler("°C");

    new Chart('.water-ph-chart', '.tab-content', this.searchSet.waterPH, 500)
      .generatePlane(14, "")
      .area("#0000FF")
      .labeler(" pH");

    new Chart('.air-humidity-chart', '.tab-content', this.searchSet.airHumidity, 500)
      .generatePlane(100, "%")
      .area("#0000FF")
      .labeler("%");

    new Chart('.soil-humidity-chart', '.tab-content', this.searchSet.soilHumidity, 500)
      .generatePlane(100, "%")
      .area("#654321")
      .labeler("%");
  }

  /**
   * Generate the slider.
   */
  makeSlider() {
    let sliderId = document.getElementById("slider");

    // Slider is lenght of array.
    noUiSlider
      .create(sliderId, {
        start: [
          0,
          this.originalSet.co2Level.length
        ],
        step: 1,
        connect: true,
        range: {
          'min': 0,
          'max': this.originalSet.co2Level.length
        }
      }).on('change', (e: any[]) => this.updateEvent(e));
    
    // Show date boolean.
    this.loaded = true;
  }

  /**
   * Slice the search set from the original set.
   * @param e 
   */
  updateEvent(e: number[]) {
    this.searchSet = Object.assign([], this.originalSet);
    
    // Iterate through data set array for slicing. 
    Object.keys(this.searchSet).forEach(key => {
      this.searchSet[key] = this.searchSet[key].slice().slice(e[0], e[1]);
    });

    // Re-construct charts.
    this.chartConstructor();
  }

  /**
   * Refresh charts based upon new screen size.
   */
  @HostListener('window:resize')
  resize() {
    this.chartConstructor();
  }
}