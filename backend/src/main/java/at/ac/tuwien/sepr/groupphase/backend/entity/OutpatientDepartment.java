package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;

@Entity

public class OutpatientDepartment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 510)
    private String name;

    @Column(length = 2000)
    private String description;

    private int capacity;
    @Column(nullable = false)
    private boolean active;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id")
    private OpeningHours openingHours;

    public OutpatientDepartment setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getId() {
        return id;
    }

    public OutpatientDepartment setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public OutpatientDepartment setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public OutpatientDepartment setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public int getCapacity() {
        return capacity;
    }

    public OutpatientDepartment setOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
        return this;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public boolean isActive() {
        return active;
    }

    public OutpatientDepartment setActive(boolean active) {
        this.active = active;
        return this;
    }
}
