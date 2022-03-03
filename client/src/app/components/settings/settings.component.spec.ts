import { HttpClient, HttpHandler } from '@angular/common/http';
import { ComponentFixture, ComponentFixtureAutoDetect, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { DistanceUnit, TemperatureUnit, UserSettings } from 'src/app/models/userSettings';
import { SettingsService } from 'src/app/service/settings/settings.service';
import { SettingsComponent } from './settings.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';


fdescribe('SettingsComponent', () => {
  function sendInput(inputElement: any, text: string) {
    inputElement.value = text;
    inputElement.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    return fixture.whenStable();
  }

  let component: SettingsComponent;
  let fixture: ComponentFixture<SettingsComponent>;
  let componentHtml: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        SettingsService,
        FormBuilder,
        { provide: ComponentFixtureAutoDetect, useValue: true }
      ],
      imports: [HttpClientTestingModule],
      declarations: [SettingsComponent]
    }) .compileComponents();

    fixture = TestBed.createComponent(SettingsComponent);
    component = fixture.componentInstance;
    componentHtml = fixture.debugElement.nativeElement;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SettingsComponent);
    component = fixture.componentInstance;
    componentHtml = fixture.debugElement.nativeElement;
  });

  it('Component load', async () => {
    fixture.detectChanges();
    fixture.isStable();

    expect(component).toBeTruthy();
  });


  it('Load settings', async () => {
    const settingsService = fixture.debugElement.injector.get(SettingsService);
    const spy = spyOn(settingsService, 'getSettings').and.returnValue(of({
      "displayName": "Ronald",
      "displayStatus": "Test",
      "distanceUnit": DistanceUnit.feet,
      "temperatureUnit": TemperatureUnit.celcius,
      "graphPreferences": new Map(),
      "profilePicture": null
    }));
    spyOn(settingsService, 'postSettings').and.returnValue(null);

    fixture = TestBed.createComponent(SettingsComponent);
    component = fixture.componentInstance;
    componentHtml = fixture.debugElement.nativeElement;


    spy.calls.mostRecent().returnValue.subscribe(() => {
      expect(component.currentSettings.displayName).toEqual("Ronald");
      expect(component.currentSettings.displayStatus).toEqual("Test");
      expect(component.currentSettings.distanceUnit).toEqual("feet");
      expect(component.currentSettings.temperatureUnit).toEqual("celcius");
    });
  });

  it('Update username', () => {
    let inputEl: HTMLInputElement = componentHtml.querySelector("#displayName");
    let statusEl: HTMLTextAreaElement = componentHtml.querySelector("#status");
    let buttonEl: HTMLButtonElement = componentHtml.querySelector("#personalSettingsForm button");

    component.personalSettings.controls.displayName.setValue("Bob")

    component.onSubmitPersonalSettings();

    fixture.detectChanges();

    expect(component.currentSettings.displayName).toBe("Bob");
  });

  it('Can\'t have empty display name', () => {

    fixture = TestBed.createComponent(SettingsComponent);
    component = fixture.componentInstance;

    component.personalSettings.controls.displayName.setValue("");
    component.personalSettings.controls.status.setValue("Hello world");

    expect(component.personalSettings.valid).toBe(false);
  });

  it('Can\'t have empty status', () => {

    fixture = TestBed.createComponent(SettingsComponent);
    component = fixture.componentInstance;

    component.personalSettings.controls.displayName.setValue("Bob");
    component.personalSettings.controls.status.setValue("");

    expect(component.personalSettings.valid).toBe(false);
  });

});
