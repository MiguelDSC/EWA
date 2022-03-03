package app.rest.greenhouse;

import app.models.greenhouse.GreenhouseMeasurement;
import app.repositories.greenhouse.GreenhouseData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * GreenhouseController test.
 * @author Bart Salfischberger <bart.salfischberger@hva.nl>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class GreenhouseController {

    @Qualifier("greenhouseDataJpa")
    @Autowired
    private GreenhouseData greenhouseData;

    @Test
    public void getHourData() {
        List<GreenhouseMeasurement> measurements = greenhouseData.getAllFull(1, 1,
                GreenhouseMeasurement::getCo2Level);
        assertThat(measurements).isNotEmpty();
    }
}
