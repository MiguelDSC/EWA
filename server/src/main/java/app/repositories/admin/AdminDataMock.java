package app.repositories.admin;

import app.models.user.Team;
import app.models.admin.*;
import app.models.user.User;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Primary
@Component
public class AdminDataMock implements AdminData{

    private List<DateGraph> logins = new ArrayList<>();
    private List<DateGraph> changes = new ArrayList<>();
    private List<ApiCallPerTeam> apiCallPerTeam = new ArrayList<>();
    private List<User> members = new ArrayList<>();

    public AdminDataMock() {
        /*members.add(new Member(1, memberRole.agronomist, "Norman", "Borlaug", "borlaug@hva.nl",
                "/assets/img/member/avatar.png", memberPermission.team_leader, LocalDate.now()));
        members.add(new Member(2, memberRole.climate_scientist, "John", "Deere", "borlaug@hva.nl",
                "/assets/img/member/avatar.png", memberPermission.team_leader, LocalDate.now()));
        members.add(new Member(3, memberRole.botanist, "Carl", "Linnaeus", "linnaeus@hva.nl",
                "/assets/img/member/avatar.png", memberPermission.team_leader, LocalDate.now()));

        LocalDate start = LocalDate.now().minusYears(1);
        LocalDate end = LocalDate.now().plusDays(1);

        for (LocalDate i = start; i.isBefore(end); i = i.plusDays(1)) {
            logins.add(new DateGraph(i, (int) (Math.random() * 30) + 10));
            changes.add(new DateGraph(i, (int) (Math.random() * 2) + 10));
        }

        for (int i = 0; i < memberRole.values().length; i++) {
            apiCallPerTeam.add(new ApiCallPerTeam(new Team(memberRole.values()[i].toString()), (int) (Math.random() * 2) + 10));
        }*/
    }

    @Override
    public List<DateGraph> getLogins() {
        return this.logins;
    }

    @Override
    public List<DateGraph> getChanges() {
        return this.changes;
    }

    @Override
    public List<ApiCallPerTeam> apiCallPerRoles() {
        return this.apiCallPerTeam;
    }

    @Override
    public List<User> getTeamCaptains() {
        return this.members;
    }
}
