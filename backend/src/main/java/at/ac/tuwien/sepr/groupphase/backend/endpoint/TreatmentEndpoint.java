package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.service.TreatmentService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.PatientServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
}
