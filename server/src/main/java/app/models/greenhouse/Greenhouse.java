package app.models.greenhouse;

import app.models.user.Team;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.List;

/**
 * Greenhouse that holds greenhouse measurements.
 */
@Entity
@Table(name = "Greenhouse")
public class Greenhouse {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int greenhouseId;

    @OneToMany(mappedBy = "greenhouse")
    private List<GreenhouseMeasurement> measurements = new java.util.ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JsonBackReference
    private Team team;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id", referencedColumnName = "greenhouse_id")
    private GreenhouseSetting greenhouseSetting;

    public Greenhouse() {
    }

    public Greenhouse(int greenhouseId, List<GreenhouseMeasurement> measurements, Team team, GreenhouseSetting greenhouseSetting) {
        this.greenhouseId = greenhouseId;
        this.measurements = measurements;
        this.team = team;
        this.greenhouseSetting = greenhouseSetting;
    }

    public Greenhouse(int id) {
        this.greenhouseId = id;
    }

    public Greenhouse(List<GreenhouseMeasurement> measurements) {
        this.measurements = measurements;
    }

    public int getGreenhouseId() {
        return greenhouseId;
    }

    public List<GreenhouseMeasurement> getMeasurements() {
        return measurements;
    }

    /**
     * Add measuerment to the list. This methods also maintains
     * the invariant.
     *
     * @param measurement
     * @return boolean
     */
    public boolean addMeasurement(GreenhouseMeasurement measurement) {
        if (measurement != null) measurement.setGreenhouse(this);
        return this.measurements.add(measurement);
    }

    public boolean setSetting(GreenhouseSetting setting) {
        if (setting != null) {
            this.greenhouseSetting = setting;
            this.greenhouseSetting.setGreenhouse(this);
            return true;
        }

        return false;
    }

    public GreenhouseSetting getSetting() {
        return this.greenhouseSetting;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
