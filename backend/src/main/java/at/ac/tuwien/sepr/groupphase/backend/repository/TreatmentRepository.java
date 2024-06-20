package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Treatment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Long> {
    @Query("SELECT t FROM Treatment t WHERE t.patient.credential.id = ?1 AND t.id IN (SELECT m.treatment.id FROM Message m)")
    List<Treatment> findTreatmentsWithExistingMessagesForPatientCredentials(Long credentialId);

    List<Treatment> findByPatient_PatientId(Long patientId);

    List<Treatment> findByDoctors_DoctorId(Long doctorId);

    @Query("SELECT t FROM Treatment t WHERE t.patient.credential.id = ?1 AND t.id NOT IN (SELECT m.treatment.id FROM Message m)")
    List<Treatment> findTreatmentsWithoutExistingMessagesForPatientCredentials(Long credentialId);

    @Query("SELECT t FROM Treatment t JOIN t.doctors d WHERE d.credential.id = ?1 AND t.id IN (SELECT m.treatment.id FROM Message m)")
    List<Treatment> findTreatmentsByDoctorsCredentialsWithMessages(Long credentialId);

    /**
     * Find all treatments by the specification.
     *
     * @param specification the specification
     * @param pageable      the pageable
     * @return the treatments as a page
     */
    Page<Treatment> findAll(Specification<Treatment> specification, Pageable pageable);
}
