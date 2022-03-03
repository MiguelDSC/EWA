package app.repositories.team;

import app.models.greenhouse.Greenhouse;
import app.models.user.Team;
import app.models.user.User;

import java.util.List;

public interface TeamRepository {
    Team getTeam(int id);

    List<Team> getAll();

    Team save(Team team);

    Team delete(int id);

    Team editGreenhouse(Team team, Greenhouse greenhouse);

    Team update(Team team);
}
