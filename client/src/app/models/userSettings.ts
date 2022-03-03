export enum DistanceUnit {
  meters = 'meters',
  feet = 'feet'
}

export enum TemperatureUnit {
  celcius= 'celcius',
  fahrenheit = 'fahrenheit'
}

export class UserSettings {
  temperatureUnit: TemperatureUnit;
  distanceUnit: DistanceUnit;
  displayName: string;
  displayStatus: string;
  profilePicture: File;
  /**
   graphPreferences is a collection of booleans(bits) packed into a single int.
   it's mapped according to the following indices:
   + bit 0: co2-level
   + bit 1: air-temp
   + bit 2: soil-temp
   + bit 3: soil-humidity
   + bit 4: air-humidity
   + bit 5: water-ph
   */
  graphPreferences: Map<string, boolean>;

  public static possibleGraphPreferences = [
    "air-humidity",
    "air-temp",
    "co2-level",
    "soil-humidity",
    "soil-temp",
    "water-ph"
  ].sort();

  constructor(temperatureUnit: TemperatureUnit, distanceUnit: DistanceUnit, displayName: string, displayStatus: string, graphPreferences: Map<string,boolean>) {
    this.temperatureUnit = temperatureUnit;
    this.distanceUnit = distanceUnit;
    this.displayName = displayName;
    this.displayStatus = displayStatus;
    this.graphPreferences = graphPreferences
    this.profilePicture = new File([], 'empty.png', { type: 'image/png' });
  }
}

//Mirrors UserSettings, just minus the profilepicture
export class UserSettingsResponse {
  temperatureUnit: TemperatureUnit;
  distanceUnit: DistanceUnit;
  displayName: string;
  displayStatus: string;
  graphPreferences: number;

  constructor(temperatureUnit: TemperatureUnit, distanceUnit: DistanceUnit, displayName: string, displayStatus: string, graphPreferences: number) {
    this.temperatureUnit = temperatureUnit;
    this.distanceUnit = distanceUnit;
    this.displayName = displayName;
    this.displayStatus = displayStatus;
    this.graphPreferences = graphPreferences;
  }

  static fromUserSettings(s: UserSettings): UserSettingsResponse {

    let graphPreferences: string[] = [];
    s.graphPreferences.forEach(((value, key) => {
      // @ts-ignore
      console.log((value.value) ? '1' : '0');
      // @ts-ignore
      graphPreferences.push((value.value) ? '1' : '0');
    }));

    return new UserSettingsResponse(
      s.temperatureUnit,
      s.distanceUnit,
      s.displayName,
      s.displayStatus,
      parseInt(graphPreferences.join('').padEnd(7, '0'),2)
    );
  }
}
