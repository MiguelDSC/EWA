package app.rest.team;

import app.models.UserTeam;
import app.models.greenhouse.Greenhouse;
import app.models.user.Team;
import app.models.user.User;
import app.repositories.greenhouse.GreenhouseData;
import app.repositories.greenhouse.GreenhouseDataJpa;
import app.repositories.greenhouse.GreenhouseRepository;
import app.repositories.team.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/team")
public class TeamController {
    @Autowired
    public TeamRepository repo;

    @Autowired
    @Qualifier("greenhouseDataJpa")
    public GreenhouseData ghRepo;

    @Autowired
    public GreenhouseRepository gRepo;

    @GetMapping("/")
    public List<Team> getAll() {
        return repo.getAll();
    }

    @GetMapping("/{id}")
    public Team getById(@PathVariable int id) {
        return repo.getTeam(id);
    }

    @PostMapping("/")
    public Team add(@RequestBody Team team) {
        return repo.save(team);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    private class notFound extends RuntimeException {
        private notFound(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    private class notAllowed extends RuntimeException {
        private notAllowed(String message) {
            super(message);
        }
    }

    @PutMapping("/{id}")
    public Team update(@PathVariable int id, @RequestBody Team team) {
        if (id != team.getId()) throw new idNotSame("The id in the url does not match the id in the team");
        Team oldTeam = repo.getTeam(team.getId());

        if (oldTeam == null) throw new notFound("The team is not found");
        oldTeam.setName(team.getName());

        if ((ghRepo.getGreenhouse(team.getGreenhouse().getGreenhouseId()).getTeam() != null) && (oldTeam.getGreenhouse().getGreenhouseId() != team.getGreenhouse().getGreenhouseId()))
            throw new notAllowed("This greenhouse already has a team");

        Greenhouse gh = ghRepo.getGreenhouse(team.getGreenhouse().getGreenhouseId());
        repo.editGreenhouse(oldTeam, gh);
        oldTeam.setGreenhouse(ghRepo.getGreenhouse(team.getGreenhouse().getGreenhouseId()));
        return repo.update(oldTeam);
    }

    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    private class idNotSame extends RuntimeException {
        public idNotSame(String message) {
            super(message);
        }
    }

    @GetMapping(value = "/{id}/users")
    public List<User> getListOfUsersPerTeam(@PathVariable int id) {
        Team team = repo.getTeam(id);
        if (team == null) throw new notFound("this team does not exist");
        List<User> returnValue = new ArrayList<>();
        for (UserTeam user : team.getUsers()) {
            returnValue.add(user.getUser());
        }
        return returnValue;
    }

    @DeleteMapping(value = "/{id}")
    public Team delete(@PathVariable int id){
        return repo.delete(id);
    }
}
