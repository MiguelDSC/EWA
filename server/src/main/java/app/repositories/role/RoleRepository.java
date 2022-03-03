package app.repositories.role;

import app.models.UserTeam;
import app.models.user.Role;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
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
public class RoleRepository {
    @PersistenceContext
    private EntityManager em;

    public Role getRoleByName(String name) {
        return em.createQuery("SELECT r FROM Role r WHERE r.name = ?1", Role.class)
                .setParameter(1, name)
                .getSingleResult();
    }

    public List<Role> getAllRoles() {
        return em.createQuery("FROM Role", Role.class).getResultList();
    }
}
