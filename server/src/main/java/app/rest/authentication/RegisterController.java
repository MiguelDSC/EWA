package app.rest.authentication;

import app.models.invite.Invite;
import app.models.user.User;
import app.repositories.invite.InviteRepository;
import app.repositories.user.UserRepository;
import app.rest.security.Password;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth/register")
public class RegisterController {

    @Autowired
    private Password password;

    @Autowired
    private InviteRepository inviteRepository;

    @Autowired
    @Qualifier("userRepositoryJPA")
    private UserRepository userRepository;

    // Update the user when clicked on registration
    @PutMapping("")
    public ResponseEntity<Boolean> updateUser(@RequestBody ObjectNode in, @RequestParam String hash) {
        String password = in.get("password").asText();
        Invite invite = inviteRepository.findByHash(hash);

        if (password.isEmpty() && invite == null) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Password or Hash incorrect");
        }

        User user = invite.getUserTeam().getUser();
        user.setPassword(this.password.passwordHash(password));
        userRepository.save(user);
        inviteRepository.deleteInviteByUser(invite.getUserTeam());

        return ResponseEntity.accepted().body(true);
    }
}
