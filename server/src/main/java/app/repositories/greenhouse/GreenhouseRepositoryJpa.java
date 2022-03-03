package app.repositories.greenhouse;

import app.models.greenhouse.Greenhouse;
import app.models.user.Team;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class GreenhouseRepositoryJpa implements GreenhouseRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Greenhouse> getAll() {
        return em.createQuery("SELECT g FROM Greenhouse g", Greenhouse.class).getResultList();
    }

    public List<Greenhouse> getAllFree() {
        return em.createQuery("SELECT g FROM Greenhouse g WHERE g.team IS null", Greenhouse.class).getResultList();
    }

    public Greenhouse save(Greenhouse greenhouse) {
        return em.merge(greenhouse);
    }
}
