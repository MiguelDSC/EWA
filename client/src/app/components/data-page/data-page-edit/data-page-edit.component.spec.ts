import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, ComponentFixtureAutoDetect, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { of } from 'rxjs';
import { AuthorizationService } from 'src/app/service/authorization/authorization.service';
import { GreenhouseService } from 'src/app/service/greenhouse-data-service/greenhouse.service';

import { DataPageEditComponent } from './data-page-edit.component';
import { GreenhouseSettingInterface } from 'src/app/interfaces/greenhouse-setting';

/**
 * Test by Bart Salfischberger <bart.salfischberger@hva.nl>
 */
fdescribe('DataPageEditComponent', () => {
  let component: DataPageEditComponent;
  let fixture: ComponentFixture<DataPageEditComponent>;

  beforeEach(async () => {

    // Override part of AuthorizationService.
    let fakeAuthService = jasmine.createSpyObj<AuthorizationService>('AuthorizationService', {
      isAdmin: of(true)
    })

    // Override parts of GreenhouseService.
    let fakeGreenhouseService = jasmine.createSpyObj<GreenhouseService>('GreenhouseService', {
      getGreenhouseSetting: undefined,
      setGreenhouseSetting: undefined
    });

    // Generate a fake greenhouse setting.
    let greenhouse: GreenhouseSettingInterface = {
      airTemperature: 21,
      airHumidity: 51,
      soilTemperature: 16,
      soilHumidity: 50,
      soilMixId: 1,
      waterPH: 5,
      waterMixId: 4,
      lightingHex: "#ffffff",
      exposure: 4,
      timestamp: new Date()
    };

    // Generate a fake role so form can be loaded.
    fakeGreenhouseService.decodedToken = {
      role: 4
    };

    fakeGreenhouseService.getGreenhouseSetting.and.returnValue(of(greenhouse));

    await TestBed.configureTestingModule({
      declarations: [
        DataPageEditComponent
      ],
      imports: [
        HttpClientTestingModule
      ],
      providers: [
        FormBuilder,
        [{ provide: GreenhouseService, useValue: fakeGreenhouseService }],
        [{ provide: AuthorizationService, useValue: fakeAuthService }],
        { provide: ComponentFixtureAutoDetect, useValue: true }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DataPageEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    fixture.detectChanges();
    fixture.isStable();
    expect(component).toBeTruthy();
  });

  it('form should be loaded', () => {

    // Select form and check if truthy.
    let form: HTMLElement = fixture.debugElement.query(By.css('form')).nativeElement;
    expect(form).toBeTruthy();
  });

  it('form validators should mark form as invalid', () => {
    component.form.patchValue({
      airTemperature: 999
    });

    fixture.detectChanges();

    expect(component.form.invalid).toBeTrue();
  });

  it('error message should appear if data is incorrect', () => {
    component.form.patchValue({
      airTemperature: 999
    });

    fixture.detectChanges();

    // Check if the correct message is shown.
    const alert: HTMLElement = fixture.debugElement.query(By.css('div.form-error')).nativeElement;
    expect(alert.innerText).toEqual("Air temperature must be less than or equal to 40 (currently 999).");
  });

  it('buttons should update accordingly on change', () => {
    component.form.patchValue({
      soilTemperature: 999
    });

    fixture.detectChanges();

    // Check if update button is disabled.
    const updateButton: HTMLButtonElement = fixture.debugElement.query(By.css('button#update-a')).nativeElement;
    expect(updateButton.disabled).toBeTrue();
  });

  it('form should be able to reset', () => {
    const formBefore: string = JSON.stringify(component.form.getRawValue());

    component.form.patchValue({
      soilTemperature: 999
    });

    const resetButton: HTMLButtonElement = fixture.debugElement.query(By.css('button#reset-btn')).nativeElement

    fixture.detectChanges();

    expect(resetButton.disabled).toBeFalse();
    
    // Reset form.
    resetButton.click();

    // Compare to old (before) value.
    expect(JSON.stringify(component.form.getRawValue())).toEqual(formBefore);
  });
});