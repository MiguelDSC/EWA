package app.rest.greenhouse;

import app.models.greenhouse.Greenhouse;
import app.models.greenhouse.GreenhouseMeasurement;
import app.models.greenhouse.GreenhouseSetting;
import app.repositories.greenhouse.GreenhouseData;
import app.repositories.greenhouse.GreenhouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@RestController
@RequestMapping("/api/greenhouse")
public class GreenhouseController {

    @Autowired
    @Qualifier("greenhouseDataJpa")
    private GreenhouseData greehouseData;


    @Autowired
    private GreenhouseRepository greenhouseRepository;


    /**
     * Retrieve the last measurement record.
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}/current")
    public GreenhouseMeasurement getCurrent(@PathVariable int id) {
        return this.greehouseData.getLast(id);
    }

    /**
     * Get daily averages.
     *
     * @param id
     * @param mapper
     * @param limit
     * @return
     */
    @GetMapping("/{id}/{mapper}/daily/average")
    public Map<LocalDate, Double> getDailyAverage(@PathVariable int id, @PathVariable String mapper,
                                                  @RequestParam Optional<Integer> limit) {
        return greehouseData.getDailyAverage(id, limit.orElse(10), this.getMapper(mapper));
    }

    /**
     * @param id
     * @param mapper
     * @param limit
     * @return
     */
    @GetMapping("/{id}/{mapper}/hourly/average")
    public Map<LocalDateTime, Double> getHourlyAverage(@PathVariable int id, @PathVariable String mapper,
                                                       @RequestParam Optional<Integer> limit) {
        return this.greehouseData.getHourlyAverage(id, limit.orElse(24), this.getMapper(mapper));
    }

    /**
     * Get full record of measurements.
     *
     * @param id
     * @param mapper
     * @param limit
     * @return
     */
    @GetMapping("/{id}/{mapper}/full")
    public List<GreenhouseMeasurement> getDataFull(@PathVariable int id, @PathVariable String mapper,
                                                   @RequestParam Optional<Integer> limit) {
        return greehouseData.getAllFull(id, limit.orElse(1), this.getMapper(mapper));
    }

    /**
     * Retrieve the configuration of the greenhouse.
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}/settings")
    public GreenhouseSetting getState(@PathVariable int id) {
        return greehouseData.getGreenhouse(id).getSetting();
    }

    /**
     * Update the greenhouse configuration.
     *
     * @param id
     * @param setting
     * @return
     */
    @PutMapping(value = "/{id}/settings")
    public ResponseEntity<GreenhouseSetting> postState(@PathVariable int id, @RequestBody GreenhouseSetting setting) {
        GreenhouseSetting newSetting = greehouseData.setSetting(id, setting);
        if (newSetting == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The form is invalid");
        return new ResponseEntity<>(newSetting, HttpStatus.OK);
    }


    @GetMapping(value = "/")
    public List<Greenhouse> getAllGreenhouses() {
        return greenhouseRepository.getAll();
    }

    @GetMapping(value = "/free")
    public List<Greenhouse> getAllFreeGreenhouses() {
        return greenhouseRepository.getAllFree();
    }


    /**
     * Helper method for converting a string into the selected
     * type.
     *
     * @param mapper
     * @return
     */
    private Function<GreenhouseMeasurement, Double> getMapper(String mapper) {
        return switch (mapper) {
            case "co2-level" -> GreenhouseMeasurement::getCo2Level;
            case "air-temp" -> GreenhouseMeasurement::getAirTemperature;
            case "soil-temp" -> GreenhouseMeasurement::getSoilTemperature;
            case "soil-humidity" -> GreenhouseMeasurement::getSoilHumidity;
            case "air-humidity" -> GreenhouseMeasurement::getAirHumidity;
            case "water-ph" -> GreenhouseMeasurement::getWaterPH;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Must contain a valid measurement value.");
        };
    }
}

