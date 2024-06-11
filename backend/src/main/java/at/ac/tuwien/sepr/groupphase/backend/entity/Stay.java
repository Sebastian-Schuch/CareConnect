package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class Stay {

    @GeneratedValue
    @Id
    private Long id;

    @ManyToOne
    private Station station;

    public Patient getPatient() {
        return patient;
    }

    public Stay setPatient(Patient patient) {
        this.patient = patient;
        return this;
    }

    @ManyToOne
    private Patient patient;

    private LocalDateTime arrival;
    private LocalDateTime departure;

    public Long getId() {
        return id;
    }

    public Stay setId(Long id) {
        this.id = id;
        return this;
    }

    public Station getStation() {
        return station;
    }

    public Stay setStation(Station station) {
        this.station = station;
        return this;
    }

    public LocalDateTime getArrival() {
        return arrival;
    }

    public Stay setArrival(LocalDateTime arrival) {
        this.arrival = arrival;
        return this;
    }

    public LocalDateTime getDeparture() {
        return departure;
    }

    public Stay setDeparture(LocalDateTime departure) {
        this.departure = departure;
        return this;
    }
}
