package app.models.greenhouse;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

/**
 * This models stores measurement data.
 */
@Entity
@NamedQuery(
        name = "GreenhouseMeasurement.greaterThen",
        query = "SELECT m FROM GreenhouseMeasurement m WHERE m.serverTime >= ?1 AND m.greenhouse.greenhouseId = ?2"
)
public class GreenhouseMeasurement {

    // All data from a greenhouse measurement.
    @Id
    @GeneratedValue
    @JsonIgnore
    private long id;
    private LocalDateTime apiTime;      // API timezone (this can be a few hours difference)

    @JsonIgnore
    private LocalDateTime serverTime;   // Server time for filtering.

    private double airTemperature;
    private double airHumidity;
    private double soilTemperature;
    private double soilHumidity;
    private int soilMixId;
    private double waterPH;
    private int waterMixId;
    private String lightingHex;
    private double exposure;
    private double co2Level;

    @JsonBackReference
    @ManyToOne
    private Greenhouse greenhouse;

    public Greenhouse getGreenhouse() {
        return greenhouse;
    }

    public GreenhouseMeasurement() {}

    /**
     * LinkedHashMap and greenhouse to measurement.
     * @param in
     */
    public GreenhouseMeasurement(LinkedHashMap in, Greenhouse greenhouse) {
        this.airHumidity = doubler(in.get("air_humidity"));
        this.airTemperature = doubler(in.get("air_temp_c"));
        this.apiTime = LocalDateTime.parse((String) in.get("date_time"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.serverTime = LocalDateTime.now();
        this.soilTemperature = doubler(in.get("soil_temp_c"));
        this.soilMixId = (int) in.get("soil_mix_id");
        this.soilHumidity = doubler(in.get("soil_humidity"));
        this.waterPH = doubler(in.get("water_ph"));
        this.waterMixId = (int) in.get("water_mix_id");
        this.lightingHex = (String) in.get("lighting_rgb");
        this.exposure = doubler(in.get("daily_exposure"));
        this.co2Level = doubler(in.get("CO2_level"));
        this.greenhouse = greenhouse;
    }

    /**
     * Make measuerment.
     */
    public GreenhouseMeasurement(LocalDateTime time, double airTemperature, double airHumidity, double soilTemperature, int soilMixId,
                                 double soilHumidity, int waterPH, int waterMixId, String lightingHex, double exposure, int co2Level) {
        this.apiTime = time;
        this.serverTime = LocalDateTime.now();
        this.airTemperature = doubler(airTemperature);
        this.airHumidity = doubler(airHumidity);
        this.soilTemperature = doubler(soilTemperature);
        this.soilMixId = soilMixId;
        this.waterPH = waterPH;
        this.waterMixId = waterMixId;
        this.lightingHex = lightingHex;
        this.exposure = doubler(exposure);
        this.co2Level = doubler(co2Level);
        this.soilHumidity = doubler(soilHumidity);
    }

    /**
     * Helper method to round incoming measurement values.
     * @param o
     * @return
     */
    public static double doubler(Object o) {
        if (o instanceof Integer) o = ((double) (Integer) o);
        return Math.round((double) o * 100.0) / 100.0;
    }

    /**
     * Set greenhouse for measurement.
     * @param greenhouse
     */
    public void setGreenhouse(Greenhouse greenhouse) {
        this.greenhouse = greenhouse;
    }

    public double getAirTemperature() {
        return airTemperature;
    }

    public double getAirHumidity() {
        return airHumidity;
    }

    public double getSoilTemperature() {
        return soilTemperature;
    }

    public int getSoilMixId() {
        return soilMixId;
    }

    public double getWaterPH() {
        return waterPH;
    }

    public int getWaterMixId() {
        return waterMixId;
    }

    public String getLightingHex() {
        return lightingHex;
    }

    public double getExposure() {
        return exposure;
    }

    public double getCo2Level() {
        return co2Level;
    }

    public double getSoilHumidity() {
        return soilHumidity;
    }

    public LocalDateTime getApiTime() {
        return apiTime;
    }
}
