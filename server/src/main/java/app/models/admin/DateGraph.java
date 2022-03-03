package app.models.admin;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
public class DateGraph {
    @Id
    @GeneratedValue
    private int id;
    private LocalDate date;
    private double value;

    public DateGraph(LocalDate date, double value) {
        this.date = date;
        this.value = value;
    }

    public DateGraph() {
    }

    public LocalDate getDate() {
        return date;
    }

    public double getValue() {
        return value;
    }
}
