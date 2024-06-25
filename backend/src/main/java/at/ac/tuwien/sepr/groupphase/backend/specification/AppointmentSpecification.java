package at.ac.tuwien.sepr.groupphase.backend.specification;

import at.ac.tuwien.sepr.groupphase.backend.entity.Appointment;
import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class AppointmentSpecification {

    public static Specification<Appointment> hasPatient(Patient patient) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("patient"), patient);
    }

    public static Specification<Appointment> hasOutpatientDepartment(OutpatientDepartment outpatientDepartment) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("outpatientDepartment"), outpatientDepartment);
    }

    public static Specification<Appointment> hasStartDateAfter(Date startDate) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), startDate);
    }

    public static Specification<Appointment> hasEndDateBefore(Date endDate) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), endDate);
    }

    public static Specification<Appointment> filterByPatientWithOptionalCriteria(Patient patient, OutpatientDepartment outpatientDepartment, Date startDate, Date endDate) {
        Specification<Appointment> spec = Specification.where(null);

        if (patient != null) {
            spec = spec.and(hasPatient(patient));
        }

        if (outpatientDepartment != null) {
            spec = spec.and(hasOutpatientDepartment(outpatientDepartment));
        }

        if (startDate != null) {
            spec = spec.and(hasStartDateAfter(startDate));
        }

        if (endDate != null) {
            spec = spec.and(hasEndDateBefore(endDate));
        }

        return spec;
    }

    public static Specification<Appointment> filterAllAppointmentsWithOptionalCriteria(Patient patient, OutpatientDepartment outpatientDepartment, Date startDate, Date endDate) {
        Specification<Appointment> spec = Specification.where(null);

        if (patient != null) {
            spec = spec.and(hasPatient(patient));
        }

        if (outpatientDepartment != null) {
            spec = spec.and(hasOutpatientDepartment(outpatientDepartment));
        }

        if (startDate != null) {
            spec = spec.and(hasStartDateAfter(startDate));
        }

        if (endDate != null) {
            spec = spec.and(hasEndDateBefore(endDate));
        }

        return spec;
    }
}
