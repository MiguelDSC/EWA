package app.repositories.team;

import app.models.greenhouse.Greenhouse;
import app.models.user.Team;
import app.models.user.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This method <Description of functionality
 *
 * @author R. Siepelinga
 */

@PersistenceContext
@Repository
@Transactional
public class TeamRepositoryJPA implements TeamRepository {

    @PersistenceContext
    private EntityManager em;

    public Team getTeam(int id) {
        Team t = null;
        try {
            t = (Team) this.em.createNativeQuery("SELECT team.id, team.name FROM `team` WHERE `team`.`id`=?", Team.class)
                    .setParameter(1, id)
                    .getSingleResult();
            try {
                Greenhouse g = (Greenhouse) this.em.createNativeQuery("SELECT * FROM `greenhouse` WHERE team_id=?", Greenhouse.class).setParameter(1, id).getSingleResult();
                t.setGreenhouse(g);
            } catch (Exception e) {
                t.setGreenhouse(null);
            }
        } catch (Exception ignored) {
        }
        return t;
    }

    public List<Team> getAll() {
        List<Team> returnValue = this.em.createNativeQuery("SELECT id, name FROM `team`", Team.class).getResultList();
        for (Team team : returnValue) {
            try {
                Greenhouse gh = (Greenhouse) this.em.createNativeQuery("SELECT * FROM `greenhouse` WHERE team_id=" + team.getId(), Greenhouse.class).getSingleResult();
                team.setGreenhouse(gh);
            } catch (Exception e) {
                team.setGreenhouse(null);
            }
        }
        return returnValue;
    }

    public Team save(Team team) {
        System.out.println("here");
        int id = team.getId();
        if (id == 0) {
            List<Team> teams = getAll();
            teams.sort(Comparator.comparing(Team::getId));
            id = teams.get(teams.size() - 1).getId() + 1;
        }

        this.em.createNativeQuery("INSERT INTO `team` (id, name) VALUES (?, ?)").setParameter(1, id).setParameter(2, team.getName()).executeUpdate();
        try {
            if (team.getGreenhouse().getGreenhouseId() != 0)
                this.em.createNativeQuery("UPDATE `greenhouse` SET team_id=? WHERE id=?").setParameter(1, id).setParameter(2, team.getGreenhouse().getGreenhouseId()).executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getTeam(id);
    }

    public Team update(Team team) {
        em.merge(team);
        return getTeam(team.getId());
    }

    public Team delete(int id) {
        try {
            Team team = getTeam(id);
            this.em.createNativeQuery("UPDATE `greenhouse` SET team_id=null WHERE team_id=?").setParameter(1, id).executeUpdate();
            this.em.createNativeQuery("DELETE FROM `user_team` WHERE team_id=?").setParameter(1, id).executeUpdate();
            this.em.createNativeQuery("DELETE FROM `team` WHERE id=?").setParameter(1, id).executeUpdate();
            return team;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Team editGreenhouse(Team team, Greenhouse greenhouse) {
        this.em.createNativeQuery("UPDATE `greenhouse` SET team_id=null WHERE team_id=?")
                .setParameter(1, team.getId()).executeUpdate();
        this.em.createNativeQuery("UPDATE `greenhouse` SET team_id=? WHERE id=?")
                .setParameter(1, team.getId())
                .setParameter(2, greenhouse.getGreenhouseId())
                .executeUpdate();
        return getTeam(team.getId());
    }

}

