package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.util.Date;

@Entity
public class Stay {

    @GeneratedValue
    @Id
    private Long id;

    @ManyToOne
    private InpatientDepartment inpatientDepartment;

    public Patient getPatient() {
        return patient;
    }

    public Stay setPatient(Patient patient) {
        this.patient = patient;
        return this;
    }

    @ManyToOne
    private Patient patient;

    private Date arrival;
    private Date departure;

    public Long getId() {
        return id;
    }

    public Stay setId(Long id) {
        this.id = id;
        return this;
    }

    public InpatientDepartment getInpatientDepartment() {
        return inpatientDepartment;
    }

    public Stay setInpatientDepartment(InpatientDepartment inpatientDepartment) {
        this.inpatientDepartment = inpatientDepartment;
        return this;
    }

    public Date getArrival() {
        return arrival;
    }

    public Stay setArrival(Date arrival) {
        this.arrival = arrival;
        return this;
    }

    public Date getDeparture() {
        return departure;
    }

    public Stay setDeparture(Date departure) {
        this.departure = departure;
        return this;
    }
}
