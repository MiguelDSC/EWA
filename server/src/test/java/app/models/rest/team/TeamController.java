package app.models.rest.team;

import app.models.greenhouse.Greenhouse;
import app.models.user.Team;
import app.repositories.team.TeamRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TeamController {
    @Autowired
    private TeamRepository repo;

    @Before
    public void addData() {
        repo.save(new Team("team 2"));
        repo.save(new Team("team 3"));
        repo.save(new Team("team 4"));
        repo.save(new Team("team 5"));
    }

    @Test
    public void repoNotNull() {
        assertThat(repo).isNotNull();
    }

    @Test
    public void getAll() {
        List<Team> teams = repo.getAll();
        assertThat(teams).isNotNull();
    }

    @Test
    public void canGetSpecificTeam() {
        Team team = repo.getTeam(4);
        assertThat(team.getName()).isEqualTo("team 4");
    }

    @Test
    public void canAddGreenhouse() {
        Greenhouse greenhouse = new Greenhouse(6);
        Team oldTeam = repo.getTeam(2);
        Team team = repo.editGreenhouse(oldTeam, greenhouse);
        assertThat(team.getGreenhouse().getGreenhouseId()).isEqualTo(greenhouse.getGreenhouseId());
    }

    @Test
    public void canUpdateGreenhouse() {
        Greenhouse greenhouse = new Greenhouse(6);
        Team oldTeam = repo.getTeam(2);
        Team team = repo.editGreenhouse(oldTeam, greenhouse);
        assertThat(team.getGreenhouse().getGreenhouseId()).isEqualTo(greenhouse.getGreenhouseId());
        Greenhouse newGreenhouse = new Greenhouse(3);
        team = repo.editGreenhouse(team, newGreenhouse);
        assertThat(team.getGreenhouse().getGreenhouseId()).isEqualTo(newGreenhouse.getGreenhouseId());
    }

    @Test
    public void canUpdateTeam() {
        Team team = repo.getTeam(3);
        team.setName("new name");
        Team newTeam = repo.update(team);
        assertThat(newTeam.getName()).isEqualTo("new name");
    }

    @Test
    public void canDelete() {
        Team team = repo.getTeam(5);
        repo.delete(team.getId());
        Team oldTeam = repo.getTeam(5);
        assertThat(oldTeam).isNull();
    }
}
