package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;

import java.util.List;

@Entity
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private long patientId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id")
    private Credential credential;

    @Convert(converter = EncryptorConverter.class)
    @Column(nullable = false, unique = true)
    private String svnr;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Medication> medicines;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Allergy> allergies;

    public String getSvnr() {
        return svnr;
    }

    public void setSvnr(String svnr) {
        this.svnr = svnr;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public long getPatientId() {
        return patientId;
    }

    public Credential getCredential() {
        return credential;
    }

    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    public List<Medication> getMedicines() {
        return medicines;
    }

    public void setMedicines(List<Medication> medicines) {
        this.medicines = medicines;
    }

    public List<Allergy> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<Allergy> allergies) {
        this.allergies = allergies;
    }
}
