package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.Date;
import java.util.List;

@Entity
public class Treatment {
    @Id
    @GeneratedValue
    private Long id;

    @Convert(converter = EncryptorConverter.class)
    private String treatmentTitle;

    private Date treatmentStart;

    private Date treatmentEnd;

    @ManyToOne
    private Patient patient;

    @ManyToOne
    private OutpatientDepartment outpatientDepartment;

    @Convert(converter = EncryptorConverter.class)
    private String treatmentText;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Doctor> doctors;

    @OneToMany(fetch = FetchType.EAGER)
    private List<TreatmentMedicine> medicines;

    public void setId(Long id) {
        this.id = id;
    }

    public void setTreatmentTitle(String treatmentTitle) {
        this.treatmentTitle = treatmentTitle;
    }

    public void setTreatmentStart(Date treatmentStart) {
        this.treatmentStart = treatmentStart;
    }

    public void setTreatmentEnd(Date treatmentEnd) {
        this.treatmentEnd = treatmentEnd;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setOutpatientDepartment(OutpatientDepartment outpatientDepartment) {
        this.outpatientDepartment = outpatientDepartment;
    }

    public void setTreatmentText(String treatmentText) {
        this.treatmentText = treatmentText;
    }

    public void setDoctors(List<Doctor> doctors) {
        this.doctors = doctors;
    }

    public void setMedicines(List<TreatmentMedicine> medicines) {
        this.medicines = medicines;
    }

    public Long getId() {
        return id;
    }

    public String getTreatmentTitle() {
        return treatmentTitle;
    }

    public Date getTreatmentStart() {
        return treatmentStart;
    }

    public Date getTreatmentEnd() {
        return treatmentEnd;
    }

    public Patient getPatient() {
        return patient;
    }

    public OutpatientDepartment getOutpatientDepartment() {
        return outpatientDepartment;
    }

    public String getTreatmentText() {
        return treatmentText;
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }

    public List<TreatmentMedicine> getMedicines() {
        return medicines;
    }
}
