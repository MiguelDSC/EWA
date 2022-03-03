package app.rest.admin;

import app.models.admin.ApiCallPerTeam;
import app.models.admin.DateGraph;
import app.models.user.User;
import app.repositories.admin.AdminData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminDataController {

    @Autowired
    private AdminData adminData;

    @GetMapping("/login")
    public List<DateGraph> getLogins() {
        return adminData.getLogins();
    }

    @GetMapping("/changes")
    public List<DateGraph> getChanges() {
        return adminData.getChanges();
    }

    @GetMapping("/changes/roles")
    public List<ApiCallPerTeam> getApiPerTeam() {
        return adminData.apiCallPerRoles();
    }

    @GetMapping("/team-captains")
    public List<User> getTeamCaptains() {
        return adminData.getTeamCaptains();
    }
}
