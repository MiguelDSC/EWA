import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { GreenhouseService } from 'src/app/service/greenhouse-data-service/greenhouse.service';
import * as d3 from 'd3';
import { GreenhouseSettingInterface } from 'src/app/interfaces/greenhouse-setting';

@Component({
  selector: 'app-data-page-edit',
  templateUrl: './data-page-edit.component.html',
  styleUrls: ['./data-page-edit.component.scss']
})
export class DataPageEditComponent implements OnInit {

  originalForm: string;
  form: FormGroup = this.fb.group({
    airTemperature: [10, [Validators.min(10.0), Validators.max(40.0), Validators.required]],
    airHumidity: [12, [Validators.min(12.0), Validators.max(99.0), Validators.required]],
    soilTemperature: [10, [Validators.min(10.0), Validators.max(40.0), Validators.required]],
    soilHumidity: [12, [Validators.min(12.0), Validators.max(99.0), Validators.required]],
    soilMixId: [1, [Validators.min(1), Validators.max(10000), Validators.required]],
    waterPH: [5, [Validators.min(5.0), Validators.max(8.0), Validators.required]],
    waterMixId: [1, [Validators.min(1), Validators.max(10000), Validators.required]],
    lightingHex: ['#ffffff', [Validators.minLength(7), Validators.required]],
    exposure: [1, [Validators.min(1.0), Validators.max(24.0), Validators.required]],
  });

  constructor(private fb: FormBuilder, public greenhouse: GreenhouseService) { }

  ngOnInit(): void {
    // Get settings from the back end.
    this.greenhouse.getGreenhouseSetting().subscribe((d: GreenhouseSettingInterface) => {
      delete d.timestamp;                           // Delete property of object.
      this.form.setValue(d);                        // Set form value.
      this.originalForm = this.form.getRawValue();  // Set original values for reset.
    });
  }

  onSubmit() {
    if (!this.updateCheck()) {
      this.greenhouse.setGreenhouseSetting(this.form.getRawValue()).subscribe(() => {
        this.originalForm = this.form.getRawValue();
        this.generateAlert("Updated greenhouse values.", 'alert-success');
      }, () => this.generateAlert("Something went wrong.", 'alert-danger'));
    }
  }

  onReset() {
    this.form.reset(this.originalForm);
  }

  resetCheck() {
    return !((JSON.stringify(this.form.getRawValue()) != JSON.stringify(this.originalForm)))
  }

  updateCheck() {
    return !((JSON.stringify(this.form.getRawValue()) != JSON.stringify(this.originalForm)) && this.form.valid);
  }

  errorMessage(): string {

    let names = new Map([
      ['airTemperature', 'Air temperature'],
      ['soilTemperature', 'Soil temperature'],
      ['airHumidity', 'Air humidity'],
      ['soilHumidity', 'Soil humidity'],
      ['soilMixId', 'Soil mix ID'],
      ['waterMixId', 'Water mix ID'],
      ['lightingHex', 'Lighting'],
      ['exposure', 'Exposure'],
      ['waterPH', 'Water pH']
    ]);

    let message: string = "";
    Object.keys(this.form.controls).forEach(key => {
      if (this.form.get(key).invalid) {
        if (this.form.get(key).errors?.['min']) message += `<p><b>${names.get(key)}</b> must be greater than or equal to ${this.form.get(key).errors.min.min} (currently ${this.form.get(key).errors.min.actual}).</p>`;
        if (this.form.get(key).errors?.['max']) message += `<p><b>${names.get(key)}</b> must be less than or equal to ${this.form.get(key).errors.max.max} (currently ${this.form.get(key).errors.max.actual}).</p>`;
        if (this.form.get(key).errors?.['required']) message += `<p><b>${names.get(key)}</b> is required!</p>`;
      }
    });

    return message;
  }

  generateAlert(text: string, type: string) {
    let alertBox = d3.selectAll('.alert-box');
    alertBox.selectAll('*').remove();
    alertBox.append('div')
      .attr('class', 'alert show fade alert-dismissible ' + type)
      .text(text)
      .append('button')
      .attr('type', 'button')
      .attr('data-bs-dismiss', 'alert')
      .attr('class', 'btn-close');

    setTimeout(() => {
      alertBox.selectAll('*').remove();
    }, 5000);
  }
}
