package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentPageDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Doctor;
import at.ac.tuwien.sepr.groupphase.backend.entity.Medication;
import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Treatment;
import at.ac.tuwien.sepr.groupphase.backend.entity.TreatmentMedicine;
import at.ac.tuwien.sepr.groupphase.backend.service.TreatmentService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.PatientServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Endpoint for treatment related operations.
 */


@RestController
@RequestMapping(value = "/api/v1/treatments")
public class TreatmentEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final TreatmentService treatmentService;
    private final PatientServiceImpl patientServiceImpl;
    private final UserService userService;

    @Autowired
    public TreatmentEndpoint(TreatmentService treatmentService, PatientServiceImpl patientServiceImpl, UserService userService) {
        this.treatmentService = treatmentService;
        this.patientServiceImpl = patientServiceImpl;
        this.userService = userService;
    }


    /**
     * Create a new treatment.
     *
     * @param treatmentDtoCreate the dto for treatment to create
     * @return the created treatment as dto
     */

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Secured("DOCTOR")
    public TreatmentDto createTreatment(@Valid @RequestBody TreatmentDtoCreate treatmentDtoCreate) {
        LOGGER.info("createTreatment({})", treatmentDtoCreate);
        return treatmentService.createTreatment(treatmentDtoCreate);
    }

    /**
     * Update an existing treatment.
     *
     * @param id                       the id of the treatment to update
     * @param treatmentDtoCreateUpdate the dto with updated treatment details
     * @return the updated treatment as dto
     */
    @Secured({"DOCTOR"})
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TreatmentDto updateTreatment(@PathVariable("id") Long id, @Valid @RequestBody TreatmentDtoCreate treatmentDtoCreateUpdate) {
        LOGGER.info("updateTreatment({}, {})", id, treatmentDtoCreateUpdate);
        return treatmentService.updateTreatment(id, treatmentDtoCreateUpdate);
    }


    /**
     * Get a treatment by id.
     *
     * @param id the id of the treatment
     * @return the treatment as dto
     */
    @Secured({"DOCTOR", "SECRETARY", "PATIENT"})
    @GetMapping({"/{id}"})
    public TreatmentDto getTreatmentById(@PathVariable("id") Long id) {
        LOGGER.info("getTreatmentById({})", id);
        TreatmentDto treatmentDto = treatmentService.getTreatmentById(id);
        if (userService.isValidRequestOfRole(Role.SECRETARY)
            || userService.isValidRequestOfRole(Role.DOCTOR)
            || patientServiceImpl.isOwnRequest(treatmentDto.patient().id())) {
            return treatmentDto;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this resource");
        }
    }

    /**
     * Search for treatments with specified criteria.
     *
     * @param searchParams the search criteria
     * @return a page of all treatments that match the criteria
     */
    @Secured({"DOCTOR", "SECRETARY", "PATIENT"})
    @GetMapping({"/search"})
    public TreatmentPageDto searchTreatments(TreatmentDtoSearch searchParams) {
        LOGGER.info("getAllTreatmentsFromTimePeriod()");
        if (userService.isValidRequestOfRole(Role.SECRETARY) || userService.isValidRequestOfRole(Role.DOCTOR) || patientServiceImpl.isOwnRequest(searchParams.patientId())) {
            Pageable pageable = PageRequest.of(searchParams.page(), searchParams.size(), Sort.Direction.fromString("DESC"), "treatmentStart");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Specification<Treatment> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (searchParams.startDate() != null && searchParams.endDate() != null) {
                    try {
                        Date start = sdf.parse(searchParams.startDate().split("T")[0]);
                        Date end = sdf.parse(searchParams.endDate().split("T")[0]);
                        predicates.add(cb.between(root.get("treatmentStart"), start, end));
                    } catch (ParseException e) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format");
                    }
                }
                if (searchParams.doctorName() != null) {
                    Join<Treatment, Doctor> doctorJoin = root.join("doctors");
                    Join<Doctor, Credential> credentialsJoin = doctorJoin.join("credential");
                    Predicate firstName = cb.like(cb.lower(credentialsJoin.get("firstName")), "%" + searchParams.doctorName().toLowerCase() + "%");
                    Predicate lastName = cb.like(cb.lower(credentialsJoin.get("lastName")), "%" + searchParams.doctorName().toLowerCase() + "%");
                    predicates.add(cb.or(firstName, lastName));
                }
                if (searchParams.patientName() != null) {
                    Join<Treatment, Patient> patientJoin = root.join("patient");
                    Join<Patient, Credential> credentialsJoin = patientJoin.join("credential");
                    Predicate firstName = cb.like(cb.lower(credentialsJoin.get("firstName")), "%" + searchParams.patientName().toLowerCase() + "%");
                    Predicate lastName = cb.like(cb.lower(credentialsJoin.get("lastName")), "%" + searchParams.patientName().toLowerCase() + "%");
                    predicates.add(cb.or(firstName, lastName));
                }
                if (searchParams.patientId() != null && searchParams.patientId() != -1) {
                    predicates.add(cb.equal(root.get("patient").get("id"), searchParams.patientId()));
                }
                if (searchParams.treatmentTitle() != null) {
                    predicates.add(cb.like(cb.lower(root.get("treatmentTitle")), "%" + searchParams.treatmentTitle().toLowerCase() + "%"));
                }
                if (searchParams.departmentName() != null) {
                    Join<Treatment, OutpatientDepartment> outpatientJoin = root.join("outpatientDepartment");
                    predicates.add(cb.like(cb.lower(outpatientJoin.get("name")), "%" + searchParams.departmentName().toLowerCase() + "%"));
                }
                if (searchParams.medicationName() != null) {
                    Join<Treatment, TreatmentMedicine> treatmentMedicineJoin = root.join("medicines");
                    Join<TreatmentMedicine, Medication> medicineJoin = treatmentMedicineJoin.join("medicine");
                    predicates.add(cb.like(cb.lower(medicineJoin.get("name")), "%" + searchParams.medicationName().toLowerCase() + "%"));
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            };
            return treatmentService.searchTreatments(spec, pageable);

        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this resource");
        }
    }
}
