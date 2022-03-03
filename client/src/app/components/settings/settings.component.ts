import {Component, OnInit} from '@angular/core';
import {FormArray, FormBuilder, Validators} from '@angular/forms';
import {DomSanitizer, SafeUrl} from '@angular/platform-browser';
import {SettingsService} from 'src/app/service/settings/settings.service';
import {DistanceUnit, TemperatureUnit, UserSettings} from "../../models/userSettings";
import {PasswordValidator} from "../../Validators";
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {
  possibleGraphPreferences = UserSettings.possibleGraphPreferences;
  log = console.log;

  //Temp variable
  userId: Number;

  tu = TemperatureUnit;
  du = DistanceUnit;

  image: SafeUrl;

  profilepicPreview: File | undefined;
  profilepicPreviewName: string | undefined;

  imageUploaded: boolean = false;

  // Default UserSettings for testing purposes
  // Later on the UserSettings object will get loaded in from the backend
  currentSettings: UserSettings = new UserSettings(
    TemperatureUnit.celcius,
    DistanceUnit.feet,
    '',
    '',
     new Map<string, boolean>(this.possibleGraphPreferences.map(k => [k, true]))
  );


  personalSettings = this.fb.group({
    displayName: ['', Validators.required],
    status: ['', Validators.required],
    graphPreferences: this.fb.array([])
  });

  passwordSettings = this.fb.group({
    oldPass: ['', Validators.required],
    newPass1: ['', Validators.required],
    newPass2: ['', Validators.required]
  }, { validator: PasswordValidator('newPass1', 'newPass2')});



  unitSettings = this.fb.group({
    temperature: [''],
    distance: ['']
  })

  constructor(
    private fb: FormBuilder,
    public service: SettingsService,
    public sanitizer: DomSanitizer) {
  }

  ngOnInit(): void {
    this.userId = parseInt(localStorage.getItem("user"));

    this.service.getSettings(this.userId).subscribe((data: UserSettings) => {
      this.currentSettings = data;
      let graphPreferenceControls = this.personalSettings.controls.graphPreferences as FormArray;
      this.personalSettings.patchValue({
        displayName: this.currentSettings.displayName,
        status: this.currentSettings.displayStatus
      });

      this.currentSettings.graphPreferences = new Map([...this.currentSettings.graphPreferences.entries()].sort())
      for (const entry of this.currentSettings.graphPreferences) {
        graphPreferenceControls.push(
          this.fb.control({"key": entry[0], "value": entry[1]})
        );
      }
      this.unitSettings.patchValue({
        temperature: this.currentSettings.temperatureUnit,
        distance: this.currentSettings.distanceUnit
      });
    });

    // this.service.getProfilePicture(this.userId).subscribe((file: File) => {
    //   this.currentSettings.profilePicture = file;
    // });
    this.fetchProfilePicture();
  }

  updateCheckbox(event: any): void {
    const target = event.target
    const targetId = target.id
    let fa = this.personalSettings.controls.graphPreferences as FormArray;

    fa.at(targetId).value.value = target.checked;
  }

  onSubmitPersonalSettings(): void {
    this.currentSettings.displayName = this.personalSettings.value.displayName;
    this.currentSettings.displayStatus = this.personalSettings.value.status;
    this.currentSettings.graphPreferences = this.personalSettings.value.graphPreferences

    this.service.postSettings(this.userId, this.currentSettings);

    console.log("Updated personal settings: ", this.currentSettings);
    alert("Updated")
  }

  onConfirmImageUpload(): void {
    this.currentSettings.profilePicture = this.profilepicPreview;
    this.service.postProfilePicture(this.userId, this.currentSettings.profilePicture)
    //alert("Updated")
  }

  onSubmitPasswordSettings(): void {
    alert("Can't update password") // TODO update password in database
  }

  onSubmitUnitSettings(): void {
    this.currentSettings.temperatureUnit = this.unitSettings.value.temperature;
    this.currentSettings.distanceUnit = this.unitSettings.value.distance;

    this.service.postSettings(this.userId, this.currentSettings);

    console.log("Updated personal settings: ", this.currentSettings);
    alert("Updated")
  }

  onImageUpload(e: any) {
    const file: File = e.target.files[0];
    if (file) {
      console.log('image uploaded');
      console.log(e)

      this.profilepicPreviewName = file.name;
      this.profilepicPreview = file;

      const reader = new FileReader();
      reader.readAsDataURL(file);

      reader.onload = event => {
        if (reader.result != null) {
          this.image = reader.result;
          this.imageUploaded = true;
        }
      };
    }
  }

  fetchProfilePicture(): void {
    this.service.getProfilePicture(this.userId).subscribe(x => {
      this.image = this.sanitizer.bypassSecurityTrustUrl(URL.createObjectURL(x));
      console.log(x);
      console.log(this.image);
      // this.image = `${environment.api}/settings/${this.userId}/picture`;
    });
  }

}
