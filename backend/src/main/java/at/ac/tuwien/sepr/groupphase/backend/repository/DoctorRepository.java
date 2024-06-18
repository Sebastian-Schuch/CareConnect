package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Doctor findByCredential_EmailAndCredential_ActiveTrue(String email);

    @Query("SELECT d.credential FROM Doctor d WHERE d.credential.active = TRUE")
    List<Credential> findAllDoctorCredentials();

    Doctor findByCredentialAndCredential_ActiveTrue(Credential credential);

    @Query("SELECT d FROM Doctor d WHERE (?1 IS NULL OR UPPER(d.credential.email) LIKE %?1% ) AND ( ?2 IS NULL OR UPPER(d.credential.firstName) LIKE %?2% ) AND "
        + "( ?3 IS NULL OR UPPER(d.credential.lastName) LIKE %?3% ) AND d.credential.active ORDER BY d.credential.lastName ASC")
    List<Doctor> searchDoctor(String email, String firstName, String lastName);

    List<Doctor> findByCredential_ActiveTrue();

    Doctor findByDoctorIdAndCredential_ActiveTrue(Long id);
}
