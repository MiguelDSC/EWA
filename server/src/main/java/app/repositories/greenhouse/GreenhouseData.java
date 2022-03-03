package app.repositories.greenhouse;

import app.models.greenhouse.GreenhouseMeasurement;
import app.models.greenhouse.Greenhouse;
import app.models.greenhouse.GreenhouseSetting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface GreenhouseData {
    Greenhouse getGreenhouse(int id);
    Map<LocalDate, Double> getDailyAverage(int id, int days, Function<GreenhouseMeasurement, Double> mapper);
    Map<LocalDateTime, Double> getHourlyAverage(int id, int hours, Function<GreenhouseMeasurement, Double> mapper);
    List<GreenhouseMeasurement> getAllFull(int id, int hours, Function<GreenhouseMeasurement, Double> mapper);
    GreenhouseMeasurement getLast(int id);
    GreenhouseMeasurement getCurrentMeasurement(int greenhouse);
    GreenhouseSetting setSetting(int id, GreenhouseSetting greenhouseState);
    GreenhouseSetting getSetting(int id);
}
