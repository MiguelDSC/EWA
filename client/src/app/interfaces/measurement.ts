export interface MeasurementInterface {
    timestamp: Date,
    airTemperature: number,
    airHumidity: number,
    soilTemperature: number,
    soilHumidity: number,
    soilMixId: number,
    waterPH: number,
    waterMixId: number,
    lightingHex: string,
    exposure: number,
    co2Level: number
}