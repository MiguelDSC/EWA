package app.rest.userTeam;
import app.models.UserTeam;
import app.models.user.Role;
import app.models.user.Team;
import app.models.user.User;
import app.repositories.role.RoleRepository;
import app.repositories.team.TeamRepositoryJPA;
import app.repositories.user.UserRepositoryJPA;
import app.repositories.userteam.UserTeamRepositoryJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
public class userTeamController {

    @Autowired
    private UserTeamRepositoryJPA userTeamRepository;

    @Autowired
    private UserRepositoryJPA userRepositoryJPA;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TeamRepositoryJPA teamRepositoryJPA;

    @GetMapping("/userTeam/{userId}")
    public ResponseEntity<UserTeam> getUserTeamByUserId(@PathVariable long userId, @RequestParam("currentTeamId") String currentTeamIdString) {
        User currentUser = this.userRepositoryJPA.findById(userId);
        Team currentTeam = this.teamRepositoryJPA.getTeam(Integer.parseInt(currentTeamIdString));

        UserTeam userTeam = this.userTeamRepository.findByUserAndTeam(currentUser, currentTeam);

        if (userTeam == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Id: " + userId + " not found");
        }

        return ResponseEntity.ok().body(userTeam);
    }

    /**
     * Updates the user role in a specific team
     */
    @PutMapping("/userTeam/update/{user_id}/{team_id}")
    public ResponseEntity<UserTeam> updateUserRole(@PathVariable int user_id, @PathVariable int team_id,
                                                   @RequestBody String role_name) {
        UserTeam userTeam = userTeamRepository.findByUserAndTeam(userRepositoryJPA.findById(user_id),
                teamRepositoryJPA.getTeam(team_id));

        Role role = roleRepository.getRoleByName(role_name);
        userTeam.setRole(role);
        userTeamRepository.save(userTeam);

        return ResponseEntity.ok(userTeam);
    }

    @DeleteMapping("/userTeam/delete/{user_id}/{team_id}")
    public ResponseEntity<UserTeam> deleteUserFromTeam(@PathVariable int user_id, @PathVariable int team_id) {
        UserTeam userTeam = userTeamRepository.findByUserAndTeam(userRepositoryJPA.findById(user_id),
                teamRepositoryJPA.getTeam(team_id));

        userTeamRepository.delete(userTeam);
        return ResponseEntity.ok(userTeam);
    }
}
