package app.rest;

import app.models.UserTeam;
import app.models.invite.Invite;
import app.models.user.User;
import app.repositories.user.UserRepository;
import app.repositories.user.UserRepositoryJPA;
import app.repositories.invite.InviteRepository;
import app.repositories.userteam.UserTeamRepositoryJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

/**
 * This method <Description of functionality
 *
 * @author R. Siepelinga
 */

@RestController
@RequestMapping("/user")
public class UMSController {

    @Autowired
    @Qualifier("userRepositoryJPA")
    private UserRepository umsRepository;

    @Autowired
    private InviteRepository inviteRepository;

    @Autowired
    private UserTeamRepositoryJPA userTeamRepository;

    @RequestMapping("")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok().body(umsRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        User user = umsRepository.findById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Id: " + id + " not found");
        }
        return ResponseEntity.ok().body(user);
    }

    @PostMapping("")
    public ResponseEntity<User> createUser(@RequestBody User umsUser, UriComponentsBuilder build) {
        User user = umsRepository.save(umsUser);

        UriComponents uriComponents = build.path("/aevents/{id}").buildAndExpand(user.getId());

        return ResponseEntity.created(uriComponents.toUri()).body(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable int id) {
        User user = umsRepository.findById(id);
        if (user != null) {
            umsRepository.deleteById(id);
            return ResponseEntity.ok().body(user);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Id: " + id + " not found");
        }
    }

    @PutMapping("/activate")
    public ResponseEntity<User> activateUser(@RequestBody User user, UriComponentsBuilder build) throws NoSuchAlgorithmException {
        // Hash password
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        // Generate a SHA512 Hash, not the best but it'll function for now
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(salt);

        String namesToHash = user.getPassword();
        byte[] hashedPassword = md.digest(namesToHash.getBytes(StandardCharsets.UTF_8));

        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        String hashed = encoder.encodeToString(hashedPassword);

        user.setPassword(hashed);
        User activatedUser = umsRepository.save(user);

        UriComponents uriComponents = build.path("/aevents/{id}").buildAndExpand(activatedUser.getId());

        return ResponseEntity.created(uriComponents.toUri()).body(activatedUser);
    }

    /**
     * Get user level in team
     * @param id of user
     * @param team_id of team
     * @return ResponseEntity with the level
     */
    @GetMapping("/level/{id}/{team_id}")
    public ResponseEntity<Integer> getUserLevel(@PathVariable int id, @PathVariable int team_id) {
        return ResponseEntity.ok(umsRepository.getUserLevel(id, team_id));
    }

    /**
     * Get users in a team
     * @param team
     * @return
     */
    @GetMapping("/team/{team}")
    public ResponseEntity<List<UserTeam>> getUsersByTeam(@PathVariable int team) {
        return ResponseEntity.ok(userTeamRepository.getUsersAndRoleByTeam(team));
    }

    /**
     * Get all users, doesnt matter which team
     * @return
     */
    @GetMapping("/all/")
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(umsRepository.findAll());
    }

    /**
     * Check if an user exists through email
     * @param user
     * @return
     */
    @PostMapping("/email/check")
    public ResponseEntity<Integer> checkIfEmailExists(@RequestBody User user) {
        return ResponseEntity.ok(umsRepository.checkIfUserExistsByEmail(user.getEmail()));
    }
}
