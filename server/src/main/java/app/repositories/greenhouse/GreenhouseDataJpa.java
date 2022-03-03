package app.repositories.greenhouse;

import app.models.greenhouse.Greenhouse;
import app.models.greenhouse.GreenhouseMeasurement;
import app.models.greenhouse.GreenhouseSetting;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This is the greenhouse repository that connects to the
 * database with the use of JPA.
 */
@Repository
@Transactional
public class GreenhouseDataJpa implements GreenhouseData {

    @PersistenceContext
    private EntityManager em;

    /**
     * Rertieve greenhouse by greenhouse id.
     * @param id
     * @return
     */
    @Override
    public Greenhouse getGreenhouse(int id) {
        return em.find(Greenhouse.class, id);
    }

    /**
     * Retrieve all results greater then a cetrain amount of days.
     * @param id greenhouse id.
     * @param days the amount of days to get the measurement of.
     * @return
     */
    @Override
    public Map<LocalDate, Double> getDailyAverage(int id, int days, Function<GreenhouseMeasurement, Double> mapper) {
        return this.em.createNamedQuery("GreenhouseMeasurement.greaterThen", GreenhouseMeasurement.class)
                .setParameter(1, LocalDateTime.now().minusDays(days))
                .setParameter(2, id)
                .getResultList().stream().collect(Collectors.groupingBy(
                    (e) -> LocalDate.from(e.getApiTime().toLocalDate()),
                    TreeMap::new,
                    Collectors.averagingDouble(mapper::apply)
                ));
    }

    /**
     * Get the average for every hour.
     * @param id greenhouse
     * @param hours amount of hours.
     * @param mapper selected data (i.e. co2 level).
     * @return the averages by hour.
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
     * Returns a full list of a select amount of time from now.
     * @param hours how many hours back from now.
     * @param mapper datatype that should be returned.
     * @return list of measurements.
     */
    @Override
    public List<GreenhouseMeasurement> getAllFull(int id, int hours, Function<GreenhouseMeasurement, Double> mapper) {
        return this.em.createNamedQuery("GreenhouseMeasurement.greaterThen", GreenhouseMeasurement.class)
            .setParameter(1, LocalDateTime.now().minusHours(hours))
            .setParameter(2, id)
            .getResultList();
    }

    /**
     * Get the most recent greenhouse measurement.
     * @param id greenhouse id.
     * @return
     */
    @Override
    public GreenhouseMeasurement getLast(int id) {
        return em.createQuery("SELECT m FROM GreenhouseMeasurement m WHERE m.greenhouse.greenhouseId = ?1 ORDER BY m.serverTime DESC", GreenhouseMeasurement.class)
                .setParameter(1, id)
                .setMaxResults(1)
                .getSingleResult();
    }

    /**
     * Retrieve the current greenhouse measurement.
     * @param id get the last measurement from the api.
     * @return api results as greenhouse measurement.
     */
    @Override
    public GreenhouseMeasurement getCurrentMeasurement(int id) {
        try {
            LinkedHashMap req = WebClient.builder().build()
                    .get()
                    .uri(String.format("http://sneltec.com/hva/v2.php?gh_id=%d&user_id=sys", id))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(LinkedHashMap.class)
                    .block();

            if (((ArrayList) req.get("errorList")).size() > 0) return null;
            GreenhouseMeasurement measurement = new GreenhouseMeasurement(((LinkedHashMap)
                    ((ArrayList) req.get("sensorInfoList")).get(0)), this.getGreenhouse(id));
            this.em.merge(measurement);
            return measurement;
        } catch (Exception e) {
            return new GreenhouseMeasurement();
        }
    }

    /**
     * Update the greenhouse setting. Greenhouse values will be set according to this.
     * @param id greenhouse id.
     * @param greenhouseSetting new greenhouse settings.
     * @return
     */
    @Override
    public GreenhouseSetting setSetting(int id, GreenhouseSetting greenhouseSetting) {

        greenhouseSetting.setTimestamp(LocalDateTime.now());
        this.getGreenhouse(id).setSetting(greenhouseSetting);

        try {
            // Generate a post request while converting the setting to a form.
            LinkedHashMap res = WebClient.create()
                    .post()
                    .uri("http://sneltec.com/hva/v2.php?gh_id=1")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(greenhouseSetting.form(id)))
                    .exchangeToMono(e -> e.bodyToMono(LinkedHashMap.class))
                    .block();

            // API doesn't return any error codes, manual check of error array.
            if (((ArrayList) res.get("errorList")).size() > 0) return null;
            return greenhouseSetting;
        } catch (WebClientRequestException e) {
            return null;
        }
    }

    /**
     * Retrieve the current greenhouse setting.
     * @param id greenhouse id.
     * @return
     */
    @Override
    public GreenhouseSetting getSetting(int id) {
        Greenhouse greenhouse = this.em.find(Greenhouse.class, id);
        return this.em.createQuery("SELECT s FROM GreenhouseSetting s WHERE s.greenhouse = ?1", GreenhouseSetting.class)
                .setParameter(1, greenhouse).getSingleResult();
    }
}
