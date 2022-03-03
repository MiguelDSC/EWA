export interface GreenhouseSettingInterface {
    timestamp: Date | any,
    airTemperature: number,
    airHumidity: number,
    soilTemperature: number,
    soilHumidity: number,
    soilMixId: number,
    waterPH: number,
    waterMixId: number,
    lightingHex: string,
    exposure: number
}
