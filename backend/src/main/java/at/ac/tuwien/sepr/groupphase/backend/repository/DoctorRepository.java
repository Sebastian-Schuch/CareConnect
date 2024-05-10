package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Doctor;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    @Transactional
    @Query(value = "SELECT * FROM Doctor WHERE id= ?1", nativeQuery = true)
    Doctor findDoctorById(Long id);

}
