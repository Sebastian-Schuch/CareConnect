package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Allergy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public String getName() {
        return name;
    }

    @Column(nullable = false, name = "name", unique = true, length = 510)
    private String name;

    @Column(nullable = false)
    boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long uid) {
        this.id = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Allergy allergies)) {
            return false;
        }
        return getId().equals(allergies.getId()) && getName().equals(allergies.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }

    @Override
    public String toString() {
        return "allergies{"
            + "uid=" + id
            + ", name='" + name + '\''
            + '}';
    }

    public static final class AllergieBuilder {
        private Long uid;
        private String name;
        private boolean active;

        private AllergieBuilder() {
        }

        public static AllergieBuilder anAllergies() {
            return new AllergieBuilder();
        }

        public AllergieBuilder withUid(Long uid) {
            this.uid = uid;
            return this;
        }

        public AllergieBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public AllergieBuilder withActive(boolean active) {
            this.active = active;
            return this;
        }

        public Allergy build() {
            Allergy allergies = new Allergy();
            allergies.setId(uid);
            allergies.setName(name);
            allergies.setActive(active);
            return allergies;
        }
    }

}
