package app.rest.authentication;

import app.models.UserTeam;
import app.models.user.Team;
import app.models.user.User;
import app.repositories.team.TeamRepository;
import app.repositories.user.UserRepository;
import app.repositories.userteam.UserTeamRepository;
import app.rest.security.JWTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/auth/team")
public class TeamselectController {

    @Qualifier("userRepositoryJPA")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTeamRepository userTeamRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private JWTokenUtil tokenizer;

    @Value("${fe.address}")
    private String url;

    /**
     * Get all teams that a user is part of.
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public List<UserTeam> getAllTeams(@PathVariable Long id) {
        User user = this.userRepository.findById(id);
        return this.userTeamRepository.findByUser(user);
    }

    /**
     * Log a user in for the selected team.
     * @param teamId
     * @param req
     * @return UserTeam object with new authorization token.
     */
    @PostMapping("/{teamId}")
    public ResponseEntity<UserTeam> authWithTeam(@PathVariable int teamId, HttpServletRequest req) {
        String token = req.getHeader(HttpHeaders.AUTHORIZATION).replace("Bearer", "");
        User user = this.userRepository.findById(tokenizer.getInfo(token).getId());
        Team team = this.teamRepository.getTeam(teamId);

        // If no user nor team is found.
        if (user == null || team == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid input");

        // Get the UserTeam, by the user and team objects.
        UserTeam userTeam = this.userTeamRepository.findByUserAndTeam(user, team);

        // If no UserTeam is found.
        if (userTeam == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user or team");

        // If a user is part of one team, return dashboard url + full token.
        return ResponseEntity.accepted()
                .location(URI.create(String.format("%s/dashboard", this.url)))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenizer.encode(userTeam))
                .body(userTeam);
    }
}
