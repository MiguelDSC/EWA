package app.repositories.Settings;

import app.models.Settings;

import java.util.List;

public interface SettingsInterface {
    List<Settings> findAll(String[] category);
    Settings findById(long id);
    Settings save(Settings settings);
    Settings deleteById(long id);
}
