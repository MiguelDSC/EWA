package app.repositories.Settings;

import app.models.Image;
import app.models.Settings;
import app.models.user.User;
import app.repositories.user.UserRepositoryJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;

@PersistenceContext
@Repository
@Transactional
public class SettingsRepository implements SettingsInterface {

    public static HashMap<Long, Image> images = new HashMap<>();

    @PersistenceContext
    public EntityManager em;

    @Autowired
    UserRepositoryJPA userRepository;

    @Override
    public Settings findById(long id) {
        Settings result = em.find(Settings.class, id);
        if (result == null) {
            User user = userRepository.findById(id);
            result = new Settings();
            result.displayName = user.getFirstname();
            result.distanceUnit = Settings.DistanceUnit.meters;
            result.temperatureUnit = Settings.TemperatureUnit.celcius;
            result.displayStatus = " ... ";
            result.graphPreferences = 63;
        }
        return result;
    }

    @Override
    public List<Settings> findAll(String[] category) {
        return em.createQuery("SELECT s FROM Settings s", Settings.class).getResultList();
    }

    @Override
    public Settings save(Settings settings) {

        if ( settings.id != null && settings.id.equals(0L)) {
            em.persist(settings);
            return settings;
        }
        return em.merge(settings);
    }

    @Override
    public Settings deleteById(long id) {
        Settings toDelete = this.findById(id);
        em.remove(toDelete);
        return toDelete;
    }
}
