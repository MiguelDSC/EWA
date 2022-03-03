package app.models.admin;

import app.models.user.Team;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;


@Entity
@IdClass(ApiCallId.class)
public class ApiCallPerTeam {
    @Id
    int id;

    @Id
    @ManyToOne
    Team team;

    LocalDate date;
    int value;

    public ApiCallPerTeam(Team team, int value) {
        this.team = team;
        this.value = value;
    }

    public ApiCallPerTeam() {
    }

    public Team getName() {
        return team;
    }

    public int getValue() {
        return value;
    }
}

class ApiCallId implements Serializable {
    private Team team;
    private LocalDate date;

    public ApiCallId(Team team, LocalDate date) {
        this.team = team;
        this.date = date;
    }

    public ApiCallId() {

    }
}
