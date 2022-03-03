import * as d3 from 'd3';
import { MeasurementInterface } from 'src/app/interfaces/measurement';

export class Chart {

  // Generale chart container and settings.
  private d3Container: any;
  private data: any;
  private height: number;
  private width: number;

  private datatype: any = null;

  // Stores different line properties.
  private pathProp: any = ({
    fill: "none",
    stroke: "none",
    strokewidth: 0,
    strokelinejoin: "round",
    strokelinecap: "round"
  });

  // Stores lines properties for lines.
  private lineProp: any = ({
    x: null,
    y: null,
    xScale: null,
    yScale: null
  });

  // Inner SVG margin settings.
  private margin: any = ({ top: 20, right: 30, bottom: 30, left: 40 });

  /**
   * Generates a chart based on given data and chart type.
   * 
   * @param d3Container CSS selector string.
   * @param d3Parent CSS of the parent class (for determining the width).
   * @param data dataset used in the chart.
   * @param height height of the SVG and graph.
   */
  constructor(d3Container: string, d3Parent: string, data: any, height: number) {
    this.d3Container = d3.selectAll(d3Container);
    this.data = data;
    this.width = parseInt(d3.selectAll(d3Parent).style('width'), 10);
    this.height = height;

    // Delete any existing, all elements in the container.
    this.d3Container.selectAll("*").remove();

    // Generate the SVG.
    this.d3Container = this.d3Container
      .append('svg')
      .attr("width", '100%')
      .attr("height", this.height)
      .attr("viewbox", [0, 0, this.width, this.height]);
  }

  static dateFormat(date: any) {
    const options = { year: 'numeric', month: 'long', day: 'numeric' }; 
    return date.toLocaleDateString("en-US", options);
  }

  /**
   * Generate a positive cartesian plane.
   * 
   * TODO: Seperate this function so line and
   * area can exist in one plane.
   */
  generatePlane(maxValue: number, unit: string) {

    // Set x-axis line property.
    this.lineProp.x = d3.scaleTime()
      .domain(d3.extent(this.data, (d: any) => new Date(d.date)))
      .range([this.margin.left, this.width - this.margin.right]);

    // Set y-axis line property.
    this.lineProp.y = d3.scaleLinear()
      .domain([0, maxValue])
      .range([this.height - this.margin.bottom, this.margin.top]);

    // Append the x axis.
    this.lineProp.xScale = this.d3Container.append("g")
      .call((g: any) => {
        g.attr("transform", `translate(0,${this.height - this.margin.bottom})`)
          .call(d3.axisBottom(this.lineProp.x).ticks(this.width / 80).tickSizeOuter(0));
      });

    // Append the y axis.
    this.lineProp.yScale = this.d3Container.append("g")
      .call((g: any) => {
        g.attr("transform", `translate(${this.margin.left},0)`)
          .call(d3.axisLeft(this.lineProp.y))
      });

    return this;
  }

  /**
   * Generate a linear curve area.
   * @param fillColor area color.
   */
  area(fillColor: string) {
    this.pathProp.fill = fillColor;

    // Generate 2d area.
    this.datatype = d3.area()
      .curve(d3.curveLinear)
      .x((d: any) => this.lineProp.x(d.date))
      .y0(this.lineProp.y(0))
      .y1((d: any) => this.lineProp.y(d.value))

    this.path();          // Graph the path.   
    return this;
  }
  
  /**
   * Generate a bar chart.
   */
  histogram() {
    this.d3Container.append("g")
        .attr("class", "bars")
        .attr("fill", "steelblue")
      .selectAll("rect")
      .data(this.data)
      .join("rect")
        .attr("x", (d: any) => this.lineProp.x(d.date))
        .attr("y", (d: any) => this.lineProp.y(d.value))
        .attr("height", (d: any) => this.lineProp.y(0) - this.lineProp.y(d.value))
        .attr("width", "10px")
      .append("title")
        .text((d: any) => `${d.date}\n${d.value}`);
  }

  /**
   * Generate a linear line.
   * @param strokeColor color of the line.
   * @param strokeWidth line width.
   */
  line(strokeColor: string, strokeWidth: number) {
    this.pathProp.stroke = strokeColor;
    this.pathProp.strokewidth = strokeWidth;

    // Generate line.
    this.datatype = d3.line()
      .defined((d: any) => !isNaN(d.value))
      .x((d: any) => this.lineProp.x(d.date))
      .y((d: any) => this.lineProp.y(d.value));
    
    this.gradient([
      { offset: "0%", color: "black" },
      { offset: "10%", color: "blue"},
      { offset: "50%", color: "green" },
      { offset: "75%", color: "orange"},
      { offset: "100%", color: "red" }
    ]);

    this.path();    // Graph the path.
    return this;
  }

  labeler(unit: string) {

    // Attach the listeners for labels.
    this.d3Container
      .on("pointerenter pointermove", (event: Event)=>{
        const X: any[] = d3.map(this.data, (d: any) => d.date);
        const Y: any[] = d3.map(this.data, (d: any) => d.value);

        // Array index.
        const i = d3.bisectCenter(X, this.lineProp.x.invert(d3.pointer(event)[0]));

        tooltip.style("display", null);
        tooltip.attr("transform", `translate(${this.lineProp.x(X[i])},${this.lineProp.y(Y[i])})`);

        const formatDate = this.lineProp.x.tickFormat(null, "%b %-d, %Y");
        const formatValue = this.lineProp.y.tickFormat(100, "g");
        const title = (i:any) => `${formatDate(X[i])}\n${formatValue(Y[i])}${unit}`;
    
        // Select path and join the label box.
        const path = tooltip.selectAll("path")
          .data([,])
          .join("path")
            .attr("fill", "white")        // Background.
            .attr("stroke", "#dbdbdb");   // Stroke color.      

        const text = tooltip.selectAll("text")
          .data([,])
          .join("text")
          .call((text:any) => text
            .selectAll("tspan")
            .data(`${title(i)}`.split(/\n/))
            .join("tspan")
              .attr("x", 0)
              .attr("y", (_:any, i:any) => `${i * 1.1}em`)
              .attr("class", "small")
              .attr("font-weight", (_:any, i:any) => i ? null : "bold")
              .text((d: any) => d));

        // Set transform values.
        const {y, width: w, height: h} = text.node().getBBox();
        text.attr("transform", `translate(${-w / 2},${15 - y})`);
        path.attr("d", `M${-w / 2 - 10},5H-5l5,-5l5,5H${w / 2 + 10}v${h + 20}h-${w + 20}z`);
      })
      .on("pointerleave", ()=> tooltip.style("display", "none"))
      .on("touchstart", (event: any) => event.preventDefault());

    const tooltip = this.d3Container
      .append("g")
      .style("pointer-events", "none");
  }

  /**
   * Builds a stacked area chart.
   */
  stacked(key: any) {

    // Generate a plane with max value of 100.
    this.generatePlane(100, "%");

    let data_arr: any[] = [];

    this.data.forEach((e: any) => {
      let o = e.value.reduce(function(prev: any,curr:any){prev[curr[0]]=curr[1];return prev;},{});
      o.date = e.date;
      data_arr.push(o);
    });

    // Generate stack.
    const stack = d3.stack().keys(key)(data_arr);
    const colorScale = d3.scaleOrdinal().domain(key).range(d3.schemeTableau10);

    const legend = d3.select('.legend');
    legend.selectAll("*").remove();

    const label = legend
      .append("p")
      .style("margin", "0 40px")

    function addLegend(color: any, name: string) {
      label
        .append("svg")
          .attr("height", 20)
          .attr("width", 20)
        .append("rect")
          .attr("fill", color)
          .attr("height", 20)
          .attr("width", 20)

      label.append("span")
        .style("margin", "0px 20px 0px 5px")
        .text(name);
    }

    // Area per dataset
    const area = d3.area()
      .x((d: any) => this.lineProp.x(d.data.date))  // Corresponding x.
      .y0((d: any) => this.lineProp.y(d[0]))        // Y lower bound.
      .y1((d: any) => this.lineProp.y(d[1]))        // Y upper bound.

    // Make sets.
    const sets = this.d3Container
      .selectAll("g.sets")
      .data(stack)
      .enter()
        .append("g")
        .attr("class", "sets");

    // Append data paths in the sets.
    sets.append("path")
      .style("fill", (d: any) => {
        let color = colorScale(d);
        addLegend(color, d.key);
        return color;
      })
      .attr("d", (d: any) => area(d));

    // Data from the line hover.
    const textBox = d3.select(this.d3Container._groups[0][0].parentNode)
      .append("p")
        .attr("class", "chart-info")
        .style("display", "none")
        .style("margin", "0 40px");

    // Hovering line.    
    const selectline = this.d3Container
      .append("rect")
        .attr("height", this.height - 50)
        .attr("width", "2px")
        .style("display", "none");

    // Attach the listeners for labels.
    this.d3Container
      .on("pointerenter pointermove", (event: Event)=>{
        const X: any[] = d3.map(data_arr, (d: any) => d.date);

        // Array index.
        const i = d3.bisectCenter(X, this.lineProp.x.invert(d3.pointer(event)[0]));
        selectline.style("display", null);
        selectline.attr("transform", `translate(${this.lineProp.x(X[i])-0.5}, 20)`);

        const info: any[] = Object.entries(data_arr[0][i]);
        info.splice(-1);

        let string: string = "";

        // Make string
        info.forEach((e:any, i:any) => {
          string += i + ' ' + e;
        });

        // Remove 0 from string.
        textBox.style("display", null);
        textBox.text(string.substring(2));
        
      })
      .on("touchstart", (event: any) => event.preventDefault());
  }

  /**
   * Generate a pie chart.
   * @param radius radius of the pie chart.
   */
  pie(radius: number) {

    // Construct the center of the pie chart.
    const center = this.d3Container.append('g')
      .attr('transform', `translate(${this.width/2}, ${this.height/2})`); // Center pie chart.

    const colorScale = d3.scaleOrdinal().range(d3.schemeSet2);
    const data = d3.pie().value((d: any) => d.value)(this.data[0]);
    const arc = d3.arc().outerRadius(radius).innerRadius(0);

    // Pin arcs to the center.
    const arcs = center
      .selectAll('.arc')
      .data(data)
      .enter()
      .append('g')
      .attr('class', 'arc');

    // Make the 'pie slices' arcs.
    arcs
      .append('path')
      .attr('d', arc)
      .attr('fill', (d: any) => colorScale(d.data.value))
      .append("title")
        .text((d: any) => `${d.data.name}:\n${d.data.value}`);

    // Append text to the same arcs.
    arcs
      .data(data)
      .append('text')
        .text((d: any) => d.data.name)
        .attr('transform', (d: any) => `translate(${arc.centroid(d)})`)
        .attr("font-weight", "bold")
        .style('text-anchor', 'middle');
  }

  private gradient(gradient: any) {
    this.d3Container.append("linearGradient")
      .attr("id", "line-gradient")
      .attr("gradientUnits", "userSpaceOnUse")
      .attr("x1", 0)
      .attr("y1", this.lineProp.y(0))
      .attr("x2", 0)
      .attr("y2", this.lineProp.y(30))
      .selectAll("stop")
      .data(gradient)
      .enter().append("stop")
        .attr("offset", (d: any) => d.offset)
        .attr("stop-color", (d: any) => d.color);
  }

  /**
   * Generate a path on given functions.
   */
  private path() {

    // Generate the path element.
    const path = this.d3Container.append("path")
      .datum(this.data)
      .attr("fill", this.pathProp.fill)
      .attr("stroke", this.pathProp.stroke)
      .attr("stroke-width", this.pathProp.strokewidth)
      .attr("stroke-linejoin", "round")
      .attr("stroke-linecap", "round")
      .attr("d", this.datatype);

    return path;
  }
}
