package app.repositories.greenhouse;

import app.models.greenhouse.Greenhouse;
import app.models.user.Team;

import java.util.List;

public interface GreenhouseRepository {
    List<Greenhouse> getAll();

    List<Greenhouse> getAllFree();

    Greenhouse save(Greenhouse greenhouse);
}
