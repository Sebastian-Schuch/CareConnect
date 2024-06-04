package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AppointmentCalendarDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AppointmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AppointmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AppointmentSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.service.AppointmentService;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import at.ac.tuwien.sepr.groupphase.backend.service.SecretaryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = AppointmentEndpoint.BASE_PATH)
public class AppointmentEndpoint {

    static final String BASE_PATH = "/api/v1/appointments";
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final SecretaryService secretaryService;

    public AppointmentEndpoint(AppointmentService appointmentService, PatientService patientService, SecretaryService secretaryService) {
        this.appointmentService = appointmentService;
        this.patientService = patientService;
        this.secretaryService = secretaryService;
    }

    /**
     * The create endpoint for the appointment.
     *
     * @param toCreate the data for the appointment to create
     * @return the created appointment
     */
    @Secured({"SECRETARY", "PATIENT"})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public AppointmentDto create(@Valid @RequestBody AppointmentDtoCreate toCreate) throws ConflictException {
        LOG.info("POST" + BASE_PATH);
        LOG.debug("Body of request:\n{}", toCreate);
        return this.appointmentService.create(toCreate);
    }

    /**
     * The delete endpoint for the appointment.
     *
     * @param id the id of the appointment to delete
     */
    @Secured({"SECRETARY", "PATIENT"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") long id) {
        LOG.info("DELETE" + BASE_PATH + "/{}", id);
        long userId = appointmentService.getAppointmentById(id).patient().id();
        if (secretaryService.isValidSecretaryRequest() || patientService.isOwnRequest(userId)) {
            appointmentService.delete(id);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized to delete this appointment");
        }
    }

    /**
     * The get endpoint for the appointment.
     *
     * @param id the id of patient
     * @return all his booked appointments
     */
    @Secured({"SECRETARY", "PATIENT"})
    @GetMapping({"/patients/{id}"})
    public ResponseEntity<List<AppointmentDto>> getAllAppointmentsFromPatientWithPatientId(@PathVariable("id") long id) {
        LOG.info("GET" + BASE_PATH + "/patients/{}", id);
        if (secretaryService.isValidSecretaryRequest() || patientService.isOwnRequest(id)) {
            return ResponseEntity.status(200).body(appointmentService.getAllAppointmentsFromPatientWithPatientId(id));
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this resource");
        }
    }

    /**
     * Get all appointments booked on a OutpatientDepartment.
     *
     * @return the booked appointments
     */
    @Secured({"SECRETARY", "PATIENT"})
    @GetMapping
    public List<AppointmentCalendarDto> getAllAppointmentsFromOutpatientDepartmentWithOutpatientDepartmentId(@Valid AppointmentSearchDto searchParams) {
        LOG.info("GET" + BASE_PATH);
        LOG.debug("Body of request:\n{}", searchParams);
        return appointmentService.getAllAppointmentsFromStartDateToEndDateWithOutpatientDepartmentId(searchParams);
    }

    /**
     * Get all appointments.
     *
     * @return the list of all appointments.
     */
    @Secured({"SECRETARY"})
    @GetMapping({"/all"})
    public List<AppointmentDto> getAllAppointments() {
        LOG.info("GET" + BASE_PATH + "/all");
        return appointmentService.getAllAppointments();
    }

}
