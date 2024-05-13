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
    private Long uid;

    public String getName() {
        return name;
    }

    @Convert(converter = EncryptorConverter.class)
    @Column(nullable = false, name = "name")
    private String name;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Allergy allergies)) {
            return false;
        }
        return getUid().equals(allergies.getUid()) && getName().equals(allergies.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUid(), getName());
    }

    @Override
    public String toString() {
        return "allergies{"
            + "uid=" + uid
            + ", name='" + name + '\''
            + '}';
    }

    public static final class AllergieBuilder {
        private Long uid;
        private String name;

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

        public Allergy build() {
            Allergy allergies = new Allergy();
            allergies.setUid(uid);
            allergies.setName(name);
            return allergies;
        }
    }

}
