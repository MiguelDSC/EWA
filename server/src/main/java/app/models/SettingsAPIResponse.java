package app.models;

//This model should be exactly the same as the Settings model, just minus the profile picture
public class SettingsAPIResponse {
    public Settings.TemperatureUnit temperatureUnit;
    public Settings.DistanceUnit distanceUnit;
    public String displayName;
    public String displayStatus;
    public int graphPreferences;

    public SettingsAPIResponse(Settings.TemperatureUnit temperatureUnit, Settings.DistanceUnit distanceUnit, String displayName, String displayStatus, int graphPreferences) {
        this.temperatureUnit = temperatureUnit;
        this.distanceUnit = distanceUnit;
        this.displayName = displayName;
        this.displayStatus = displayStatus;
        this.graphPreferences = graphPreferences;
    }

    public static SettingsAPIResponse fromSettingsModel(Settings s) {
        return new SettingsAPIResponse(
                s.temperatureUnit,
                s.distanceUnit,
                s.displayName,
                s.displayStatus,
                s.graphPreferences
        );
    }

    public static Settings toSettingsModel(SettingsAPIResponse s) {
        return new Settings(
                null,
                s.temperatureUnit,
                s.distanceUnit,
                s.displayName,
                s.displayStatus,
                s.graphPreferences
        );
    }
}
