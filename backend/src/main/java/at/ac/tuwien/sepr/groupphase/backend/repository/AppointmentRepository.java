package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Appointment;
import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment>, PagingAndSortingRepository<Appointment, Long> {
    @Transactional
    @Query(value = "SELECT COUNT(*) FROM Appointment WHERE (OUTPATIENT_DEPARTMENT_ID = ?1 AND START_DATE = ?2)", nativeQuery = true)
    int getCountFromAllAppointmentsOnOutpatientDepartmentDuringSpecificTime(Long id, Date startDate);

    @Transactional
    @Query(value = "SELECT * FROM Appointment WHERE (OUTPATIENT_DEPARTMENT_ID = ?1 AND START_DATE >= ?2 AND END_DATE <= ?3)", nativeQuery = true)
    List<Appointment> getAllAppointmentsFromStartDateToEndDateWithOutpatientDepartmentId(Long id, Date startDate, Date endDate);

    @Transactional
    @Query(value = "SELECT * FROM Appointment WHERE PATIENT_PATIENT_ID = ?1", nativeQuery = true)
    List<Appointment> getAllAppointmentsFromPatientWithPatientId(Long id);

    @Transactional
    @Query(value = "SELECT * FROM Appointment", nativeQuery = true)
    List<Appointment> getAllAppointments();

    @Transactional
    @Query(value = "SELECT COUNT(*) FROM Appointment WHERE PATIENT_PATIENT_ID = ?1 AND OUTPATIENT_DEPARTMENT_ID = ?2 AND START_DATE = ?3 AND END_DATE = ?4", nativeQuery = true)
    int getAllAppointmentsMatchingPatientIdOutpatientDepartmentStartDateAndEndDate(Long patientId, Long outpatientDepartmentId, Date startDate, Date endDate);

    @Transactional
    List<Appointment> findByOutpatientDepartmentAndStartDateBetween(OutpatientDepartment department, Date startDate, Date endDate);
}
