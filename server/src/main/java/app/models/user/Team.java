package app.models.user;

import app.models.UserTeam;
import app.models.greenhouse.Greenhouse;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.engine.internal.Cascade;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;


/**
 * This method <Description of functionality
 *
 * @author R. Siepelinga
 */
@Entity
@Table(name = "Team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "team_id")
    private Greenhouse greenhouse;

    @JsonIgnore
    @OneToMany(mappedBy = "team")
    private List<UserTeam> users = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }

    public Team() {
        this.name = "Amsterdam";
    }


    public Team(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%s/%s", id, name);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public Greenhouse getGreenhouse() {
        return greenhouse;
    }

    public List<UserTeam> getUsers() {
        return users;
    }

    public void setGreenhouse(Greenhouse greenhouse) {
        this.greenhouse = greenhouse;
    }

    public void setName(String name) {
        this.name = name;
    }




}
