package app.rest.invite;

import app.models.UserTeam;
import app.models.user.Role;
import app.models.user.User;
import app.models.invite.Invite;
import app.repositories.EmailSenderRepository;
import app.repositories.role.RoleRepository;
import app.repositories.user.UserRepositoryJPA;
import app.repositories.invite.InviteRepository;
import app.repositories.team.TeamRepositoryJPA;
import app.repositories.userteam.UserTeamRepositoryJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * This method <Description of functionality
 *
 * @author R. Siepelinga
 */

@RestController
@RequestMapping(value = "/invite")
public class InviteController {

    @Autowired
    private InviteRepository inviteRepository;
    @Autowired
    private UserTeamRepositoryJPA userTeamRepositoryJPA;
    @Autowired
    private UserRepositoryJPA userRepository;
    @Autowired
    private TeamRepositoryJPA teamRepository;
    @Autowired
    private EmailSenderRepository emailSenderRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private Environment environment;

    @Value("${email.link}")
    private String emailLink;

    @GetMapping("")
    public ResponseEntity<List<Invite>> getInvites() {
        return ResponseEntity.ok(inviteRepository.findAll());
    }

    /**
     * Get the invites for a team
     * @param id
     * @return
     */
    @GetMapping("/team/{id}")
    public ResponseEntity<List<String>> getInvitesByTeam(@PathVariable int id) {
        return ResponseEntity.ok(inviteRepository.findInvitesByTeam(id));
    }

    /**
     * Getting an invited user
     */
    @GetMapping("/user/{hash}")
    public ResponseEntity<HashMap> getUserByHash(@PathVariable String hash) {
        Invite invite = inviteRepository.findByHash(hash);
        if (invite == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with hash not found");
        }

        User userToGet = userRepository.findById(invite.getUserTeam().getUser().getId());
        Map map = new HashMap();
        map.put("user", userToGet);
        map.put("role", invite.getUserTeam().getRole().getName());
        map.put("team", invite.getUserTeam().getTeam().getName());

        Map result = new HashMap();
        result.put("result", map);
        return ResponseEntity.ok((HashMap) result);
    }

    /**
     * Invite an user that doesn't have an account yet
     * @param values
     * @return
     * @throws NoSuchAlgorithmException
     */
    @PostMapping("/new")
    public ResponseEntity<User> inviteUser(@RequestBody Map<String, String> values) throws NoSuchAlgorithmException {
        User user = new User(values.get("firstname"), values.get("surname"), values.get("email"));
        user = this.userRepository.save(user);

        Role role;
        try {
            role = roleRepository.getRoleByName(values.get("role"));
        } catch (Exception e) {
            role = null;
        }

        int level = Integer.parseInt(values.get("level"));
        UserTeam userTeam = new UserTeam(user, teamRepository.getTeam(Integer.parseInt(values.get("team"))),
                role, level);
        userTeam = this.userTeamRepositoryJPA.save(userTeam);

        Invite invite = new Invite(userTeam, false);
        inviteRepository.save(invite);

        String link = String.format("<a href=\"%s/register/%s\" target=\"_blank\">Click here</a>",
                this.emailLink, invite.getUrl());
        emailSenderRepository.sendEmail(invite.getEmail(),
                "Dear " + user.getFirstname() + ", \nHopefully you're doing well," +
                        " you've been invited to collaborate on this project. Please click the link down here to join "+
                        "the team. \n" + link,
                "Invited to join the EWA team!");

        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/inviteTeamLeader/")
    public ResponseEntity<User> inviteTeamLeader( @RequestBody Map<String, String> values, UriComponentsBuilder build) throws NoSuchAlgorithmException {
        return this.invite(values, build, true);
    }

    private ResponseEntity<User> invite(Map<String, String> values, UriComponentsBuilder build, boolean teamLeader) throws NoSuchAlgorithmException {
        User user = new User(values.get("firstname"), values.get("surname"), values.get("email"));
        user = this.userRepository.save(user);

        Role role;
        try {
            role = roleRepository.getRoleByName(values.get("role"));
        } catch (Exception e) {
            role = null;
        }
        UserTeam userTeam = new UserTeam(user, teamRepository.getTeam(Integer.parseInt(values.get("team"))),
                role, Integer.parseInt(values.get("level")));
        userTeam.setLevel(teamLeader ? 1 : 0);
        userTeam = this.userTeamRepositoryJPA.save(userTeam);


        Invite invite = new Invite(userTeam, false);
        inviteRepository.save(invite);

        String link = String.format("<a href=\"%s/register/%s\" target=\"_blank\">Click here</a>",
                this.emailLink, invite.getUrl());
        emailSenderRepository.sendEmail(invite.getEmail(),
                "Dear " + user.getFirstname() + ", \nHopefully you're doing well," +
                        " you've been invited to collaborate on this project. Please click the link down here to join " +
                        "the team. \n" + link,
                "Invited to join the EWA team!");

        return ResponseEntity.ok().body(user);
    }

    /**
     * Invite an existing user to the team
     * @param values
     * @return
     * @throws NoSuchAlgorithmException
     */
    @PostMapping("/existing")
    public ResponseEntity<User> inviteExistingUser(@RequestBody Map<String, String> values)
            throws NoSuchAlgorithmException {
        User user = this.userRepository.findByEmail(values.get("email"));
        Role role;
        try {
            role = roleRepository.getRoleByName(values.get("role"));
        } catch (Exception e) {
            role = null;
        }

        UserTeam userTeam = new UserTeam(user, teamRepository.getTeam(Integer.parseInt(values.get("team"))),
                role, Integer.parseInt(values.get("level")));
        userTeam = this.userTeamRepositoryJPA.save(userTeam);

        Invite invite = new Invite(userTeam, true);
        inviteRepository.save(invite);

        String link = String.format("<a href=\"%s/register/%s\" target=\"_blank\">Click here</a>",
                this.emailLink, invite.getUrl());
        emailSenderRepository.sendEmail(invite.getEmail(),
                "Dear " + user.getFirstname() + ", \nHopefully you're doing well," +
                        " you've been invited to collaborate on this project in another team." +
                        " Please click the link down here to join the team. \n" + link,
                "Invited to join the EWA team!");

        return ResponseEntity.ok().body(user);
    }

    /**
     * Get only the invite
     * @param hash
     * @return
     */
    @GetMapping("/{hash}")
    public ResponseEntity<Invite> getInviteByHash(@PathVariable String hash) {
        Invite invite = inviteRepository.findByHash(hash);
        if (invite == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with hash not found");
        }

        return ResponseEntity.ok(invite);
    }

    @DeleteMapping("/{hash}")
    public ResponseEntity<Invite> deleteInvitedUser(@PathVariable String hash) {
        Invite invite = inviteRepository.findByHash(hash);
        if (invite != null) {
            inviteRepository.deleteByHash(hash);
            return ResponseEntity.ok().body(invite);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User: " + hash + " not found");
        }
    }

    /**
     * Deletes an invite, and that's it
     * @param hash
     * @return
     */
    @DeleteMapping(value = "/accepted/{hash}")
    public ResponseEntity<Invite> deleteInvite(@PathVariable String hash) {
        Invite invite = inviteRepository.findByHash(hash);
        if (invite != null) {
            inviteRepository.deleteInvite(invite);
            return ResponseEntity.ok().body(invite);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User: " + hash + " not found");
        }
    }

    /**
     *
     * @param hash
     * @return
     */
    @DeleteMapping(value = "/declined/{hash}")
    public ResponseEntity<Invite> deleteInviteAndUserTeam(@PathVariable String hash) {
        Invite invite = inviteRepository.findByHash(hash);
        if (invite != null) {
            inviteRepository.deleteInvite(invite);
            userTeamRepositoryJPA.removeUserTeam(invite.getUserTeam());
            return ResponseEntity.ok().body(invite);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User: " + hash + " not found");
        }
    }

    @PutMapping("")
    public ResponseEntity<User> updateUser(@RequestBody User umsUser, UriComponentsBuilder build) {
        User user = userRepository.save(umsUser);
//        inviteRepository.deleteInviteByUser(user);

        UriComponents uriComponents = build.path("/aevents/{id}").buildAndExpand(user.getId());

        return ResponseEntity.created(uriComponents.toUri()).body(user);
    }
}
