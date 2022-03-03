package app.repositories.user;

import app.models.UserTeam;
import app.models.user.Role;
import app.models.user.Team;
import app.models.user.User;
import app.repositories.team.TeamRepositoryJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

/**
 * This method <Description of functionality
 *
 * @author R. Siepelinga
 */
@PersistenceContext
@Repository
@Transactional
public class UserRepositoryJPA implements UserRepository {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private TeamRepositoryJPA teamRepository;

    @Override
    public List<User> findAll() {
        List<User> users = em.createQuery("SELECT u FROM User u").getResultList();

        return users;
    }

    @Override
    public User findById(long id) {
        return this.em.find(User.class, id);
    }

    @Override
    public User findByEmail(String email) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.email = ?1", User.class)
                    .setParameter(1, email).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public User save(User user) {
        User result = em.merge(user);

        return result;
    }

    @Override
    public boolean deleteById(long id) {
        User trashModel = em.find(User.class,id);

        if (trashModel != null) {
            em.remove(trashModel);
            return true;
        }

        return false;
    }

    @Override
    public int getUserLevel(long id, int team_id) {
        UserTeam user = em.createQuery("SELECT u FROM UserTeam u JOIN Team t ON t = u.team WHERE u.user = ?1 AND t.id = ?2", UserTeam.class)
                .setParameter(1, this.findById(id)).setParameter(2, team_id).getSingleResult();
        return user.getLevel();
    }

    @Override
    public List<User> getUsersByTeam(int team) {
        return em.createNativeQuery("SELECT u.* FROM user u LEFT JOIN invite i ON i.email = u.email INNER JOIN " +
                        "user_team t ON t.user_id = u.id WHERE i.email IS NULL AND t.team_id = ?1", User.class)
                .setParameter(1, team).getResultList();
    }

    @Override
    public int checkIfUserExistsByEmail(String email) {
        Long count = (Long) em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = ?1")
                .setParameter(1, email).getSingleResult();
        return count.intValue();
    }

    public List<UserTeam> getUsersAndRoleByTeam(int team) {
        List<UserTeam> users = em.createQuery("SELECT ut.user, ut.role FROM UserTeam ut INNER JOIN User u ON u = ut.user LEFT JOIN Invite i ON i.email = u.email WHERE i.email IS NULL AND ut.team = ?1")
                .setParameter(1, teamRepository.getTeam(team)).getResultList();
        
        return users;
    }
}