package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {
    Patient findByCredentialAndCredential_ActiveTrue(Credential credential);

    @Query("SELECT p.credential FROM Patient p WHERE p.credential.active = TRUE")
    List<Credential> findAllPatientCredentials();

    Patient findByCredential_EmailAndCredential_ActiveTrue(String email);

    @Query("SELECT p FROM Patient p WHERE (?1 IS NULL OR UPPER(p.credential.email) LIKE %?1% ) AND ( ?2 IS NULL OR UPPER(p.credential.firstName) LIKE %?2% ) AND "
        + "( ?3 IS NULL OR UPPER(p.credential.lastName) LIKE %?3% ) AND p.credential.active ORDER BY p.credential.lastName ASC")
    List<Patient> searchPatient(String email, String firstName, String lastName);

    Patient findByPatientIdAndCredential_ActiveTrue(Long id);

    List<Patient> findByCredential_ActiveTrue();

    Patient findBySvnr(String svnr);
}
