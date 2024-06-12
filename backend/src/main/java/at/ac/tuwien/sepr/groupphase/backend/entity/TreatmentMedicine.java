package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TreatmentMedicine {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Medication medicine;

    private Long amount;

    @Convert(converter = EncryptorConverter.class)
    private String unitOfMeasurement;

    private java.util.Date timeOfAdministration;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Medication getMedicine() {
        return medicine;
    }

    public void setMedicine(Medication medicine) {
        this.medicine = medicine;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public void setUnitOfMeasurement(String unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
    }

    public java.util.Date getTimeOfAdministration() {
        return timeOfAdministration;
    }

    public void setTimeOfAdministration(Date timeOfAdministration) {
        this.timeOfAdministration = timeOfAdministration;
    }
}
