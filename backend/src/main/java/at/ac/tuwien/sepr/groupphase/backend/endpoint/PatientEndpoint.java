package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = PatientEndpoint.BASE_PATH)
public class PatientEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "/api/v1/patients";

    private final PatientService patientService;

    private final UserService userService;

    public PatientEndpoint(PatientService patientService, UserService userService) {
        this.patientService = patientService;
        this.userService = userService;
    }

    /**
     * The create endpoint for the patient.
     *
     * @param toCreate the data for the patient to create
     * @return the created patient
     */
    @Secured("SECRETARY")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserLoginDto create(@Valid @RequestBody PatientCreateDto toCreate) {
        LOG.info("POST " + BASE_PATH);
        LOG.debug("Body of request:\n{}", toCreate);
        return userService.createPatient(toCreate);
    }

    /**
     * The get endpoint for the patient.
     *
     * @param id the id of patient requested
     * @return the patient requested
     */
    @Secured({"SECRETARY", "PATIENT", "ADMIN", "DOCTOR"})
    @GetMapping({"/{id}"})
    public PatientDto get(@PathVariable("id") long id) {
        LOG.info("GET " + BASE_PATH + "/{}", id);
        return this.patientService.getPatientById(id);
    }

    /**
     * Get all the patients from the repository.
     *
     * @return a list of all patients
     */
    @Secured({"SECRETARY", "DOCTOR"})
    @GetMapping
    public List<PatientDto> getAll() {
        LOG.info("GET " + BASE_PATH);
        return this.patientService.getAllPatients();
    }
}
