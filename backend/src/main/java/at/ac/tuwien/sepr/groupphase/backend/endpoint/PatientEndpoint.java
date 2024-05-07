package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = PatientEndpoint.BASE_PATH)
public class PatientEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "/api/patients";

    private final PatientService patientService;

    public PatientEndpoint(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * The create endpoint for the patient.
     *
     * @param toCreate the data for the patient to create
     * @return the created patient
     */
    @PermitAll
    //@Secured("ROLE_SECRETARY")
    @PostMapping
    public PatientDto create(@RequestBody PatientCreateDto toCreate) {
        LOG.info("POST " + BASE_PATH);
        LOG.debug("Body of request:\n{}", toCreate);
        return this.patientService.createPatient(toCreate);
    }

    /**
     * The get endpoint for the patient.
     *
     * @param id the id of patient requested
     * @return the patient requested
     */
    @PermitAll
    //@Secured("ROLE_SECRETARY", "ROLE_PATIENT", "ROLE_ADMIN", "ROLE_DOCTOR")
    @GetMapping({"/{id}"})
    public PatientDto get(@PathVariable("id") long id) {
        LOG.info("GET " + BASE_PATH + "/{}", id);
        return this.patientService.getPatientById(id);
    }
}
