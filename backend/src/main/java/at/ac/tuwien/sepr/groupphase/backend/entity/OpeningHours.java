package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class OpeningHours {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String monday;
    private String tuesday;
    private String wednesday;
    private String thursday;
    private String friday;
    private String saturday;
    private String sunday;

    public Long getId() {
        return id;
    }

    public OpeningHours setId(Long id) {
        this.id = id;
        return this;
    }

    public String getMonday() {
        return monday;
    }

    public OpeningHours setMonday(String monday) {
        this.monday = monday;
        return this;
    }

    public String getTuesday() {
        return tuesday;
    }

    public OpeningHours setTuesday(String tuesday) {
        this.tuesday = tuesday;
        return this;
    }

    public String getWednesday() {
        return wednesday;
    }

    public OpeningHours setWednesday(String wednesday) {
        this.wednesday = wednesday;
        return this;
    }

    public String getThursday() {
        return thursday;
    }

    public OpeningHours setThursday(String thursday) {
        this.thursday = thursday;
        return this;
    }

    public String getFriday() {
        return friday;
    }

    public OpeningHours setFriday(String friday) {
        this.friday = friday;
        return this;
    }

    public String getSaturday() {
        return saturday;
    }

    public OpeningHours setSaturday(String saturday) {
        this.saturday = saturday;
        return this;
    }

    public String getSunday() {
        return sunday;
    }

    public OpeningHours setSunday(String sunday) {
        this.sunday = sunday;
        return this;
    }
}