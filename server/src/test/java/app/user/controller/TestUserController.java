package app.user.controller;

import app.models.UserTeam;
import app.models.user.Team;
import app.models.user.User;
import app.repositories.team.TeamRepository;
import app.repositories.user.UserRepository;
import app.repositories.userteam.UserTeamRepository;
import app.repositories.userteam.UserTeamRepositoryJPA;
import app.rest.security.JWTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Base64Util;
import org.h2.util.json.JSONString;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Base64;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * This method <Description of functionality
 *
 * @author R. Siepelinga
 */

@RunWith(SpringRunner.class)
@SpringBootTest()
@AutoConfigureMockMvc
public class TestUserController {
    private String token;
    private UserTeam userTeam;
    private final int USER_ID = 1;

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

    @Before
    public void generateToken() {
        User user = userRepository.findById(1);
        Team team = teamRepository.getTeam(1);

        this.userTeam = userTeamRepositoryJPA.findByUserAndTeam(user, team);
        this.token = jwTokenUtil.encode(userTeam);
    }

    @Test
    public void getUserById() throws Exception {
        // Arrange a fake request
        RequestBuilder request = MockMvcRequestBuilders.get("/user/" + this.USER_ID)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        MvcResult result = mockMvc.perform(request).andReturn();

        // Act, set the content as a new json object.
        String content = result.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(content);

        // Assert, check if the id of the first user is equal to the number we want
        assertThat(jsonObject.get("id")).isEqualTo(this.USER_ID);
    }

    @Test
    public void checkIfEmailExist() throws Exception {
        // Arrange, make a fake json object for an email
        Object emailObject = new Object() {
            public final String email = "gerardjoling@hotmail.com";
        };

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(emailObject);

        // Arrange a fake request
        RequestBuilder request = MockMvcRequestBuilders.post("/user/email/check/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                .content(json).characterEncoding("utf-8");
        MvcResult result = mockMvc.perform(request).andReturn();

        // Act, set the content as a new json object.
        String content = result.getResponse().getContentAsString();

        // Assert, the email shouldn't exist.
        assertThat(Integer.parseInt(content)).isEqualTo(0);
    }

}