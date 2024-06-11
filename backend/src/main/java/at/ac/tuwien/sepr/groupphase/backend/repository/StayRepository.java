package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Stay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StayRepository extends JpaRepository<Stay, Long> {
    /**
     * Find the current stay of a patient.
     *
     * @param patientId the id of the patient
     * @return the current stay of the patient
     */
    List<Stay> findByPatient_PatientIdAndDepartureIsNull(long patientId);

    /**
     * Find all stays of a patient.
     *
     * @param patientId the id of the patient
     * @return all stays of the patient
     */
    Page<Stay> findByPatient_PatientIdAndDepartureIsNotNull(Long patientId, Pageable pageable);

}
