package app.rest;

import app.repositories.greenhouse.GreenhouseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

    @Autowired
    @Qualifier("greenhouseDataJpa")
    private GreenhouseData greenhouseData;

    /**
     * Retrieve greenhouse data from the greenhouse API.
     * This method is ran every one minute.
     */
    @Scheduled(fixedRate = 60000)
    public void getGreenHouseData() {
        // Get the API data, store it in the database.
        greenhouseData.getCurrentMeasurement(1);
    }
}
