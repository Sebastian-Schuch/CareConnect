package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Ambulance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(nullable = false, name = "capacity")
    private Long capacity;

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getCapacity() {
        return capacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ambulance)) {
            return false;
        }
        Ambulance ambulance = (Ambulance) o;
        return getId().equals(ambulance.getId()) && getName().equals(ambulance.getName()) && getCapacity().equals(ambulance.getCapacity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getCapacity());
    }

    @Override
    public String toString() {
        return "ambulance{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", capacity='" + capacity + '\''
            + '}';
    }

    public static final class AmbulanceBuilder {
        private Long id;
        private String name;
        private Long capacity;

        private AmbulanceBuilder() {
        }

        public static AmbulanceBuilder anAmbulance() {
            return new AmbulanceBuilder();
        }

        public AmbulanceBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public AmbulanceBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public AmbulanceBuilder withCapacity(Long capacity) {
            this.capacity = capacity;
            return this;
        }

        public Ambulance build() {
            Ambulance ambulance = new Ambulance();
            ambulance.setId(id);
            ambulance.setName(name);
            ambulance.setCapacity(capacity);
            return ambulance;
        }
    }


}
