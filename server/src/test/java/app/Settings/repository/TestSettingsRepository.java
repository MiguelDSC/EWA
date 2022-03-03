package app.Settings.repository;

import app.models.Settings;
import app.models.user.User;
import app.repositories.Settings.SettingsRepository;
import app.repositories.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class TestSettingsRepository {

    @Autowired
    @Qualifier("userRepositoryJPA")
    private UserRepository userRepository;

    @Autowired
    @Qualifier("settingsRepository")
    private SettingsRepository settingsRepository;

    @Before
    public void beforeEach() {
        userRepository.save(new User("Rick", "De boer", "rick@hotmail.com"));
        userRepository.save(new User("Joost", "De boer", "joost@hotmail.com"));
        userRepository.save(new User("Gerard", "De boer", "gerard@hotmail.com"));
    }

    @Test
    public void testDefaultData() {
        List<User> users = userRepository.findAll();
        User last = users.get(users.size()-1);

        Settings userSettings = settingsRepository.findById(last.getId());

        assertThat(userSettings.displayName).isEqualTo("Gerard");
        assertThat(userSettings.distanceUnit).isEqualTo(Settings.DistanceUnit.meters);
        assertThat(userSettings.temperatureUnit).isEqualTo(Settings.TemperatureUnit.celcius);
    }

    @Test
    public void updateDisplayName() {
        List<User> users = userRepository.findAll();
        User last = users.get(users.size()-1);

        Settings userSettings = settingsRepository.findById(last.getId());
        userSettings.displayName = "xX_Gerard_Xx";
        userSettings.id = last.getId();
        settingsRepository.save(userSettings);

        assertThat(settingsRepository.findById(last.getId()).displayName).isEqualTo("xX_Gerard_Xx");
    }

    @Test
    public void updateDisplayStatus() {
        List<User> users = userRepository.findAll();
        User last = users.get(users.size()-1);

        Settings userSettings = settingsRepository.findById(last.getId());
        userSettings.displayStatus = "enjoying life";
        userSettings.id = last.getId();
        settingsRepository.save(userSettings);

        assertThat(settingsRepository.findById(last.getId()).displayStatus).isEqualTo("enjoying life");
    }
}
