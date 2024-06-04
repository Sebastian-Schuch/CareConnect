package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    @Transactional
    @Query(value = "SELECT * FROM Doctor WHERE id= ?1", nativeQuery = true)
    Doctor findDoctorById(Long id);

    Doctor findByCredential_Email(String email);

    @Query("SELECT d.credential FROM Doctor d")
    List<Credential> findAllDoctorCredentials();

    Doctor findByCredential(Credential credential);

    @Query("SELECT d FROM Doctor d WHERE (?1 IS NULL OR UPPER(d.credential.email) LIKE %?1% ) AND ( ?2 IS NULL OR UPPER(d.credential.firstName) LIKE %?2% ) AND "
        + "( ?3 IS NULL OR UPPER(d.credential.lastName) LIKE %?3% ) AND d.credential.active ORDER BY d.credential.lastName ASC")
    List<Doctor> searchDoctor(String email, String firstName, String lastName);
}
