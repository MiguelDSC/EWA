package app.models.greenhouse;

import app.repositories.greenhouse.GreenhouseData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * A greenhouse configuration.
 */
@Entity
public class GreenhouseSetting {
    @Id
    @GeneratedValue
    @JsonIgnore
    private int id;
    private LocalDateTime timestamp;
    private double airTemperature;
    private double airHumidity;
    private double soilTemperature;
    private double soilHumidity;
    private int soilMixId;
    private double waterPH;
    private int waterMixId;
    private String lightingHex;
    private double exposure;

    @OneToOne
    private Greenhouse greenhouse;

    public GreenhouseSetting() {}

    /**
     * Convert the setting to a multiValueMap for
     * the greenhouse api.
     * @param id
     * @return
     */
    public MultiValueMap<String, String> form(long id) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("user_id", "system");
        form.add("gh_id", String.valueOf(id));
        form.add("air_temp_c", String.valueOf(this.airTemperature));
        form.add("air_humidity", String.valueOf(this.airHumidity));
        form.add("soil_temp_c", String.valueOf(this.soilTemperature));
        form.add("soil_humidity", String.valueOf(this.soilHumidity));
        form.add("soil_mix_id", String.valueOf(this.soilMixId));
        form.add("water_ph", String.valueOf(this.waterPH));
        form.add("water_mix_id", String.valueOf(this.waterMixId));
        form.add("lighting_rgb", String.valueOf(this.lightingHex));
        form.add("daily_exposure", String.valueOf(this.exposure));
        return form;
    }

    public void setGreenhouse(Greenhouse greenhouse) {
        this.greenhouse = greenhouse;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public double getAirTemperature() {
        return airTemperature;
    }

    public void setAirTemperature(double airTemperature) {
        this.airTemperature = airTemperature;
    }

    public double getAirHumidity() {
        return airHumidity;
    }

    public void setAirHumidity(double airHumidity) {
        this.airHumidity = airHumidity;
    }

    public double getSoilTemperature() {
        return soilTemperature;
    }

    public void setSoilTemperature(double soilTemperature) {
        this.soilTemperature = soilTemperature;
    }

    public double getSoilHumidity() {
        return soilHumidity;
    }

    public void setSoilHumidity(double soilHumidity) {
        this.soilHumidity = soilHumidity;
    }

    public int getSoilMixId() {
        return soilMixId;
    }

    public void setSoilMixId(int soilMixId) {
        this.soilMixId = soilMixId;
    }

    public double getWaterPH() {
        return waterPH;
    }

    public void setWaterPH(double waterPH) {
        this.waterPH = waterPH;
    }

    public int getWaterMixId() {
        return waterMixId;
    }

    public void setWaterMixId(int waterMixId) {
        this.waterMixId = waterMixId;
    }

    public String getLightingHex() {
        return lightingHex;
    }

    public void setLightingHex(String lightingHex) {
        this.lightingHex = lightingHex;
    }

    public double getExposure() {
        return exposure;
    }

    public void setExposure(double exposure) {
        this.exposure = exposure;
    }
}
