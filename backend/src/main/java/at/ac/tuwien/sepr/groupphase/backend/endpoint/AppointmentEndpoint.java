package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AppointmentCalendarDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AppointmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AppointmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AppointmentPageDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.service.AppointmentService;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = AppointmentEndpoint.BASE_PATH)
public class AppointmentEndpoint {

    static final String BASE_PATH = "/api/v1/appointments";
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final UserService userService;

    public AppointmentEndpoint(AppointmentService appointmentService, PatientService patientService, UserService userService) {
        this.appointmentService = appointmentService;
        this.patientService = patientService;
        this.userService = userService;
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
        if (userService.isValidRequestOfRole(Role.SECRETARY) || patientService.isOwnRequest(toCreate.patient().id())) {
            return this.appointmentService.create(toCreate);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this resource");
        }
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
        if (userService.isValidRequestOfRole(Role.SECRETARY) || patientService.isOwnRequest(userId)) {
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
    public List<AppointmentDto> getAllAppointmentsFromPatientWithPatientId(@PathVariable("id") long id) {
        LOG.info("GET" + BASE_PATH + "/patients/{}", id);
        if (userService.isValidRequestOfRole(Role.SECRETARY) || patientService.isOwnRequest(id)) {
            return appointmentService.getAllAppointmentsFromPatientWithPatientId(id);
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
    public List<AppointmentCalendarDto> getAllAppointmentsFromOutpatientDepartmentWithOutpatientDepartmentId(@RequestParam(name = "outpatientDepartmentId") long outpatientDepartmentId,
                                                                                                             @RequestParam(name = "startDate") Date startDate,
                                                                                                             @RequestParam(name = "endDate") Date endDate) {
        LOG.info("GET" + BASE_PATH);
        LOG.debug("Params of request:\n{},{},{}", outpatientDepartmentId, startDate, endDate);
        return appointmentService.getAllAppointmentsFromStartDateToEndDateWithOutpatientDepartmentId(outpatientDepartmentId, startDate, endDate);
    }

    @Secured("PATIENT")
    @GetMapping("/patient")
    public AppointmentPageDto getAppointmentsByPatient(
        @RequestParam(name = "page", defaultValue = "0") Integer page,
        @RequestParam(name = "size", defaultValue = "3") Integer size,
        @RequestParam Long patientId,
        @RequestParam(required = false) Long outpatientDepartmentId,
        @RequestParam(required = false) Date startDate,
        @RequestParam(required = false) Date endDate) {
        LOG.info("GET" + BASE_PATH + "/patient");
        return appointmentService.getAppointmentsByPatient(patientId, outpatientDepartmentId, startDate, endDate, page, size);
    }

    @Secured("SECRETARY")
    @GetMapping("/all")
    public AppointmentPageDto getAllAppointments(
        @RequestParam(name = "page", defaultValue = "0") Integer page,
        @RequestParam(name = "size", defaultValue = "3") Integer size,
        @RequestParam(required = false) Long patientId,
        @RequestParam(required = false) Long outpatientDepartmentId,
        @RequestParam(required = false) Date startDate,
        @RequestParam(required = false) Date endDate) {
        LOG.info("GET" + BASE_PATH + "/all");
        return appointmentService.getAllFilteredAppointments(patientId, outpatientDepartmentId, startDate, endDate, page, size);
    }
}
