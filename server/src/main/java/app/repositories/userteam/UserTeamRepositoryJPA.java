package app.repositories.userteam;

import app.models.UserTeam;
import app.models.user.Team;
import app.models.user.User;
import app.repositories.team.TeamRepositoryJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@PersistenceContext
@Repository
@Transactional
public class UserTeamRepositoryJPA implements UserTeamRepository {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private TeamRepositoryJPA teamRepositoryJPA;

    @Override
    public List<UserTeam> findByUser(User user) {
        return em.createQuery("SELECT u FROM UserTeam u WHERE u.user = ?1", UserTeam.class)
                .setParameter(1, user)
                .getResultList();
    }

    @Override
    public UserTeam findByUserId(long id) {
        return em.createQuery("SELECT ut FROM UserTeam ut WHERE ut.user.id = :userId", UserTeam.class)
                .setParameter("userId", id)
                .getSingleResult();
    }

    @Override
    public UserTeam save(UserTeam userTeam) {
        UserTeam result = em.merge(userTeam);

        return result;
    }

    @Override
    public UserTeam delete(UserTeam userTeam) {
        em.remove(userTeam);
        return userTeam;
    }

    @Override
    public UserTeam findByUserAndTeam(User user, Team team) {
        return em.createQuery("SELECT u FROM UserTeam u WHERE u.user = ?1 AND u.team = ?2", UserTeam.class)
                .setParameter(1, user)
                .setParameter(2, team)
                .getSingleResult();
    }

    @Override
    public List<UserTeam> getUsersAndRoleByTeam(int team) {
        List<UserTeam> users = em.createQuery("SELECT ut.user, ut.role FROM UserTeam ut INNER JOIN User u ON u = ut.user LEFT JOIN Invite i ON i.email = u.email WHERE i.email IS NULL AND ut.team = ?1")
                .setParameter(1, teamRepositoryJPA.getTeam(team)).getResultList();

        return users;
    }

    public UserTeam findById(long id) {
        return em.createQuery("SELECT ut FROM UserTeam ut WHERE ut.id = :id", UserTeam.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    @Override
    public List<UserTeam> findByTeamId(Team team) {
        return em.createQuery("SELECT ut FROM UserTeam ut WHERE ut.team.id = :id", UserTeam.class)
                .setParameter("id", team.getId())
                .getResultList();
    }

    public UserTeam removeUserTeam(UserTeam userTeam) {
        em.remove(userTeam);
        return userTeam;
    }
}