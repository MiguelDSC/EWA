package app.models;


 import app.models.user.User;

 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.OneToOne;

@Entity
public class Settings {
    public Settings() {
        this.temperatureUnit = TemperatureUnit.celcius;
        this.distanceUnit = DistanceUnit.meters;
        this.displayName = "";
        this.displayStatus = "";
    }

    public enum DistanceUnit {
        meters,
        feet
    }

    public enum TemperatureUnit {
        celcius,
        fahrenheit
    }
    
    @OneToOne
    public User user;

    @Id
    public Long id;
    public TemperatureUnit temperatureUnit = TemperatureUnit.celcius;
    public DistanceUnit distanceUnit = DistanceUnit.meters;
    public String displayName = "";
    public String displayStatus = "";

    /**
     * graphPreferences is a collection of booleans(bits) packed into a single int.
     * it's mapped according to the following indices:
     * bit 0: co2-level
     * bit 1: air-temp
     * bit 2: soil-temp
     * bit 3: soil-humidity
     * bit 4: air-humidity
     * bit 5: water-ph
     */
    public int graphPreferences = 120; //Default enable everything

    //Completely necessary, constructs an empty Settings object
    public Settings(Long userId) {}

    public Settings(Long userId, TemperatureUnit temperatureUnit, DistanceUnit distanceUnit, String displayName, String displayStatus, int graphPreferences) {
        this.temperatureUnit = temperatureUnit;
        this.distanceUnit = distanceUnit;
        this.displayName = displayName;
        this.displayStatus = displayStatus;
        this.graphPreferences = graphPreferences;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "temperatureUnit=" + temperatureUnit +
                ", distanceUnit=" + distanceUnit +
                ", displayName='" + displayName + '\'' +
                ", displayStatus='" + displayStatus + '\'' +
                '}';
    }
}
