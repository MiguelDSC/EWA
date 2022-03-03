package app.repositories.greenhouse;

import app.models.greenhouse.GreenhouseMeasurement;
import app.models.greenhouse.Greenhouse;
import app.models.greenhouse.GreenhouseSetting;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Greenhouse mock generates a bunch of fake data for testing.
 * This repository only contains one static greenhouse and greenhouse setting.
 */
@Repository
public class GreenhouseDataMock implements GreenhouseData {

    private final Greenhouse greenhouse;

    /**
     * Adding mock data in memory.
     */
    public GreenhouseDataMock() {
        this.greenhouse = new Greenhouse();
        this.greenhouse.setSetting(this.measurementToSetting());

        // Simulate every minute for one year.
        LocalDateTime start = LocalDateTime.now().minusDays(8);
        LocalDateTime end = LocalDateTime.now();

        // Mock measurement database.
        for (LocalDateTime i = start; i.isBefore(end); i = i.plusMinutes(1)) {
            greenhouse.addMeasurement(new GreenhouseMeasurement(i, numberGenerator(10, 10), numberGenerator(50, 10),
                    numberGenerator(10, 5), 10, numberGenerator(10, 5), 5, 7,
                    "#ffffff", 1200.0, 720));
        }
    }

    /**
     * For testing this always returns the one greenhouse.
     * @param id
     * @return
     */
    @Override
    public Greenhouse getGreenhouse(int id) {
        return this.greenhouse;
    }

    @Override
    public GreenhouseMeasurement getLast(int id) {
        return this.greenhouse.getMeasurements().get(this.greenhouse.getMeasurements().size()-1);
    }

    /**
     * Get all measurements of a greenhouse.
     * @param id greenhouse id (for mock always 1).
     * @param days amount of days.
     * @param mapper type of data.
     * @return daily averages.
     */
    @Override
    public Map<LocalDate, Double> getDailyAverage(int id, int days, Function<GreenhouseMeasurement, Double> mapper) {

        // Get list of measurement types and get daily average.
        return this.greenhouse.getMeasurements().stream()
                .filter(e -> e.getApiTime().isAfter(LocalDateTime.now().minusDays(days)))
                .collect(Collectors.groupingBy(
                        (e) -> LocalDate.from(e.getApiTime().toLocalDate()),
                        TreeMap::new,
                        Collectors.averagingDouble(mapper::apply)
                ));
    }

    /**
     * Get hourly average of greenhouse data.
     * @param id greenhouse id (for mock always 1).
     * @param hours amount of hours.
     * @param mapper type of data.
     * @return hourly averages.
     */
    @Override
    public Map<LocalDateTime, Double> getHourlyAverage(int id, int hours, Function<GreenhouseMeasurement, Double> mapper) {
        return this.getAllFull(id, hours, mapper).stream()
                .collect(Collectors.groupingBy(
                        (e) -> LocalDateTime.of(e.getApiTime().toLocalDate(), LocalTime.of(e.getApiTime().getHour(), 0)),
                        TreeMap::new,
                        Collectors.averagingDouble(mapper::apply)
                ));
    }

    /**
     * Get all minutes for an n-amount of hours.
     * @param id greenhouse id.
     * @param hours amount of hours.
     * @param mapper type of data.
     * @return return all minutes.
     */
    @Override
    public List<GreenhouseMeasurement> getAllFull(int id, int hours, Function<GreenhouseMeasurement, Double> mapper) {
        return this.greenhouse.getMeasurements().stream()
                .filter(e -> e.getApiTime().isAfter(LocalDateTime.now().minusHours(hours)))
                .collect(Collectors.toList());
    }

    /**
     * Get the current state of the greenhouse.
     * @param id greenhouse id.
     * @return last measurement of the greenhouse.
     */
    @Override
    public GreenhouseMeasurement getCurrentMeasurement(int id) {

        try {
            // Get the most recent measurement value.
            LinkedHashMap req = WebClient.builder().build()
                    .get()
                    .uri(String.format("http://sneltec.com/hva/v2.php?gh_id=%d&user_id=sys", id))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(LinkedHashMap.class)
                    .block();

            // API doesn't return any error codes, manual check of error array.
            if (((ArrayList) req.get("errorList")).size() > 0) return null;
            return new GreenhouseMeasurement(((LinkedHashMap) ((ArrayList) req.get("sensorInfoList")).get(0)), this.greenhouse);
        } catch (WebClientRequestException e) {
            return null;
        }
    }

    /**
     * Set a new greenhouse setting.
     * @param id greenhouse id.
     * @param setting greenhouse setting.
     * @return the greenhouse setting with new timestamp.
     */
    @Override
    public GreenhouseSetting setSetting(int id, GreenhouseSetting setting) {
        setting.setTimestamp(LocalDateTime.now());
        this.greenhouse.setSetting(setting);

        try {
            // Generate a post request while converting the setting to a form.
            LinkedHashMap res = WebClient.create()
                    .post()
                    .uri("http://sneltec.com/hva/v2.php?gh_id=1")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(setting.form(id)))
                    .exchangeToMono(e -> e.bodyToMono(LinkedHashMap.class))
                    .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(5)))
                    .block();

            // API doesn't return any error codes, manual check of error array.
            if (((ArrayList) res.get("errorList")).size() > 0) return null;
            return setting;
        } catch (WebClientRequestException e) {
            return new GreenhouseSetting();
        }
    }

    @Override
    public GreenhouseSetting getSetting(int id) {
        return this.getGreenhouse(id).getSetting();
    }

    /**
     * Generate a random numer (helper function for data generation).
     * @param min
     * @param max
     * @return random double of min max range.
     */
    private double numberGenerator(int min, int max) {
        double d = (Math.random() * max) + min;
        double scale = Math.pow(10, 1);
        return Math.round(d*scale)/scale;
    }

    /**
     * Helper method that converts the current measurement
     * to a greenhouse setting.
     * @return
     */
    private GreenhouseSetting measurementToSetting() {
        GreenhouseSetting setting = new GreenhouseSetting();
        GreenhouseMeasurement measurement = this.getCurrentMeasurement(1);

        setting.setTimestamp(measurement.getApiTime());
        setting.setAirHumidity(measurement.getAirHumidity());
        setting.setSoilTemperature(measurement.getSoilTemperature());
        setting.setSoilHumidity(measurement.getSoilHumidity());
        setting.setAirTemperature(measurement.getAirTemperature());
        setting.setSoilMixId(measurement.getSoilMixId());
        setting.setWaterMixId(measurement.getWaterMixId());
        setting.setWaterPH(measurement.getWaterPH());
        setting.setLightingHex(measurement.getLightingHex());
        setting.setExposure(measurement.getExposure());

        return setting;
    }
}
