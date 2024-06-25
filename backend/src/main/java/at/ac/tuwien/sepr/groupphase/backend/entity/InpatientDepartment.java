package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class InpatientDepartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int capacity;
    @Column(nullable = false)
    private boolean active;

    public InpatientDepartment setId(long id) {
        this.id = id;
        return this;
    }

    public InpatientDepartment setName(String name) {
        this.name = name;
        return this;
    }

    public InpatientDepartment setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }


    public boolean isActive() {
        return active;
    }

    public InpatientDepartment setActive(boolean active) {
        this.active = active;
        return this;
    }
}
