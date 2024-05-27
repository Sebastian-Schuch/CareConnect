package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    @Transactional
    @Query(value = "Select * FROM Patient WHERE ID = ?1", nativeQuery = true)
    Patient findByCredentialId(Long id);

    @Query("SELECT p.credential FROM Patient p")
    List<Credential> findAllPatientCredentials();

    Patient findByCredential(Credential credential);

    Patient findByCredential_Email(String email);

}
