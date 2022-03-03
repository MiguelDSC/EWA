package app.rest.authentication;

import app.models.UserTeam;
import app.models.user.User;
import app.repositories.user.UserRepository;
import app.repositories.userteam.UserTeamRepository;
import app.rest.security.JWTokenInfo;
import app.rest.security.JWTokenUtil;
import app.rest.security.Password;
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
 * LoginController test.
 * @author Bart Salfischberger <bart.salfischberger@hva.nl>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class LoginController {

    @Autowired
    @Qualifier("userRepositoryJPA")
    private UserRepository userRepository;

    @Autowired
    @Qualifier("userTeamRepositoryJPA")
    private UserTeamRepository userTeamRepository;

    @Autowired
    private JWTokenUtil tokenizer;

    @Autowired
    private Password password;

    private final String EMAIL = "bart@hva.nl";
    private final String PASSWORD = "bart";

    /**
     * Find the correct user.
     */
    @Test
    public void findUser() {

        // Find user object.
        User user = this.userRepository.findByEmail(EMAIL);
        assertThat(user.getEmail()).isNotNull().isEqualTo(EMAIL);

        // Correct password.
        assertThat(this.password.passwordVerify(PASSWORD, user.getPassword())).isTrue();

        // Wrong password.
        assertThat(this.password.passwordVerify("test", user.getPassword())).isFalse();

        List<UserTeam> userTeams = this.userTeamRepository.findByUser(user);

        // Check if team 1 can be found from the user.
        assertThat(userTeams.get(0).getTeam().getId()).isEqualTo(1);
    }

    /**
     * Check for a non-existing user.
     * To test the try catch on NoResultException.
     */
    @Test
    public void handleNonUser() {
        String wrongEmail = "test@example.hva.nl";

        // Try to find user object.
        User user = this.userRepository.findByEmail(wrongEmail);

        // Should be null if no user is found.
        assertThat(user).isNull();

        // User is null, check null in userTeamRepository.
        List<UserTeam> userTeams = this.userTeamRepository.findByUser(null);

        // Zero userTeam objects should be found.
        assertThat(userTeams.size()).isEqualTo(0);
    }

    /**
     * Encode then decode a token.
     */
    @Test
    public void tokenDeEncoding() {
        UserTeam userTeam = this.userTeamRepository.findByUser(this.userRepository.findByEmail(EMAIL)).get(0);
        String token = this.tokenizer.encode(userTeam);

        // Decode token
        JWTokenInfo tokenInfo = this.tokenizer.getInfo(token);

        assertThat(tokenInfo.getTeam()).isEqualTo(1);
    }

    /**
     * Handle non token strings.
     */
    @Test
    public void handleNonToken() {
        JWTokenInfo tokenInfo = this.tokenizer.getInfo("h");
        assertThat(tokenInfo).isNull();

        tokenInfo = this.tokenizer.getInfo("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
        assertThat(tokenInfo).isNull();
    }
}
