package app.Settings.controller;

import app.models.Settings;
import app.models.UserTeam;
import app.models.user.Role;
import app.models.user.Team;
import app.models.user.User;
import app.repositories.Settings.SettingsRepository;
import app.repositories.team.TeamRepository;
import app.repositories.user.UserRepository;
import app.repositories.user.UserRepositoryJPA;
import app.repositories.user.UserRepositoryMock;
import app.repositories.userteam.UserTeamRepository;
import app.rest.security.JWTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.AssertionsForClassTypes;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest()
@AutoConfigureMockMvc
public class TestSettingsController {

    private String token;
    private UserTeam userTeam;
    private Long userId;

    @Autowired
    private UserTeamRepository userTeamRepositoryJPA;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @Qualifier("userRepositoryJPA")
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private JWTokenUtil jwTokenUtil;

    @Autowired
    private SettingsRepository settingsRepository;

    @Before
    public void generateUser() {
        userRepository.save(new User("Rick", "De boer", "rick@hotmail.com"));
        Team team = teamRepository.save(new Team());

        List<User> users = userRepository.findAll();
        User last = users.get(users.size()-1);

        userTeamRepositoryJPA.save(new UserTeam(last, team, new Role(""), 0));

        userId = last.getId();
        System.out.println("UserID: "+ userId);
        this.generateToken();
    }

    public void generateToken() {
        User user = userRepository.findById(userId);

        this.userTeam = userTeamRepositoryJPA.findByUserId(userId);
        this.token = jwTokenUtil.encode(userTeam);
    }

    @Test
    public void getSettings() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get(String.format("/settings/%d", this.userId))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        MvcResult result = mockMvc.perform(request).andReturn();

        String content = result.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(content);

        assertThat(jsonObject.get("displayName")).isEqualTo("Rick");
    }


    @Test
    public void postSettings() throws Exception {
        Settings targetSettings = new Settings(userId, Settings.TemperatureUnit.fahrenheit, Settings.DistanceUnit.feet, "Thomas", "Status", 22);
        ObjectMapper objectMapper = new ObjectMapper();
        RequestBuilder request = MockMvcRequestBuilders.post(String.format("/settings/%d", this.userId))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .content(objectMapper.writeValueAsString(targetSettings));
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(settingsRepository.findById(userId).displayName).isEqualTo("Thomas");
        assertThat(settingsRepository.findById(userId).displayStatus).isEqualTo("Status");
        assertThat(settingsRepository.findById(userId).distanceUnit).isEqualTo(Settings.DistanceUnit.feet);
        assertThat(settingsRepository.findById(userId).temperatureUnit).isEqualTo(Settings.TemperatureUnit.fahrenheit);
        assertThat(settingsRepository.findById(userId).graphPreferences).isEqualTo(22);
    }
}
