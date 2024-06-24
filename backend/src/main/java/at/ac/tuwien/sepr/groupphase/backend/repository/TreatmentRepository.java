package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
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

    @Query(value =
        "SELECT d.doctor_id, c.first_name, c.last_name, c.email, c.is_initial_password, SUM(TIMESTAMPDIFF(MINUTE, t.treatment_start, t.treatment_end)) "
            + "FROM treatment_doctors AS td "
            + "JOIN treatment AS t ON td.treatment_id = t.id "
            + "JOIN doctor AS d ON td.doctors_doctor_id = d.doctor_id "
            + "JOIN credential AS c ON d.id = c.id "
            + "WHERE (t.treatment_start >= ?1 OR ?1 IS NULL) AND (t.treatment_end <= ?2 OR ?2 IS NULL)"
            + "GROUP BY d.doctor_id ", nativeQuery = true)
    List<Object[]> findTreatmentAndSumHoursGroupedByDoctors(Date startDate, Date endDate);

    @Query(value =
        "SELECT m.id, m.name, SUM(tm.amount), m.unitOfMeasurement FROM Treatment t JOIN t.medicines tm JOIN tm.medicine m WHERE (tm.timeOfAdministration >= ?1 OR ?1 IS NULL) AND (tm.timeOfAdministration <= ?2 OR ?2 IS NULL) GROUP BY m.id")
    List<Object[]> findTreatmentAndSumAmountGroupedByMedication(Date startDate, Date endDate);

}
