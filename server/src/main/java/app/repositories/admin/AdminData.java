package app.repositories.admin;

import app.models.admin.ApiCallPerTeam;
import app.models.admin.DateGraph;
import app.models.user.User;

import java.util.List;

public interface AdminData {
    List<DateGraph> getLogins();
    List<DateGraph> getChanges();
    List<ApiCallPerTeam> apiCallPerRoles();
    List<User> getTeamCaptains();
}
