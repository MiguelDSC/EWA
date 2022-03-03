package app.rest.authentication;

import app.models.UserTeam;
import app.repositories.user.UserRepository;
import app.repositories.userteam.UserTeamRepository;
import app.rest.security.JWTokenInfo;
import app.rest.security.JWTokenUtil;
import app.rest.security.Password;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/auth/login")
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

    @Value("${fe.address}")
    private String url;

    /**
     * Login the users with their email and password.
     * @param in
     * @return UserTeam object with authorization token in header.
     */
    @PostMapping(value = "", produces = "application/json")
    public ResponseEntity<UserTeam> login(@RequestBody ObjectNode in) {
        String email = in.get("email").asText();        // Email
        String password = in.get("password").asText();  // Password

        // Get the list of user team relations.
        List<UserTeam> userTeamList = this.userTeamRepository.findByUser(this.userRepository.findByEmail(email));

        // Authenticate the user.
        if (userTeamList.size() == 0 || !this.password.passwordVerify(password, userTeamList.get(0).getUser().getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User login is invalid.");

        // Check if a user is part of more than one team.
        // Return token with some null values (partial token).
        if (userTeamList.size() > 1) {
            UserTeam userTeam = userTeamList.get(0);
            return ResponseEntity.accepted()
                    .location(URI.create(String.format("%s/team-selection", this.url)))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " +
                            tokenizer.encode(userTeam.getUser(), userTeam.getId()))
                                    .body(userTeam);
        }

        // If a user is part of one team, return dashboard url + full token.
        return ResponseEntity.accepted()
                .location(URI.create(String.format("%s/dashboard", this.url)))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenizer.encode(userTeamList.get(0)))
                .body(userTeamList.get(0));
    }

    @PostMapping(value = "/refresh", produces = "application/json")
    public ResponseEntity<Long> refresh(HttpServletRequest req, HttpServletResponse res) {
        String tokenString = req.getHeader("Authorization");
        tokenString = tokenString.replace("Bearer", "");
        JWTokenInfo token = tokenizer.getNonExpiredInfo(tokenString);
        UserTeam ut = userTeamRepository.findByUserId(token.getUserId());
        String newToken = tokenizer.refresh(token, ut);
        System.out.println(newToken);
        return ResponseEntity.accepted()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + newToken)
                .body(ut.getId());
    }
}
