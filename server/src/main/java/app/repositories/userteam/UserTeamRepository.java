package app.repositories.userteam;

import app.models.UserTeam;
import app.models.user.Team;
import app.models.user.User;

import java.util.List;

public interface UserTeamRepository {
    List<UserTeam> findByUser(User user);
    List<UserTeam> findByTeamId(Team id);
    UserTeam save(UserTeam userTeam);
    UserTeam delete(UserTeam userTeam);
    UserTeam findByUserId(long id);
    UserTeam findByUserAndTeam(User user, Team team);
    List<UserTeam> getUsersAndRoleByTeam(int team);
}
