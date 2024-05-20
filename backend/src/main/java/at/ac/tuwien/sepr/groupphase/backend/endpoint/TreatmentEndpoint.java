package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.service.TreatmentService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

/**
 * Endpoint for treatment related operations.
 */

@RestController
@RequestMapping(value = "/api/v1/treatments")
public class TreatmentEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final TreatmentService treatmentService;

    @Autowired
    public TreatmentEndpoint(TreatmentService treatmentService) {
        this.treatmentService = treatmentService;
    }


    /**
     * Create a new treatment.
     *
     * @param treatmentDtoCreate the dto for treatment to create
     * @return the created treatment as dto
     * @throws MethodArgumentNotValidException if the dto is not valid
     */
    @Secured({"DOCTOR"})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TreatmentDto createTreatment(@Valid @RequestBody TreatmentDtoCreate treatmentDtoCreate) throws MethodArgumentNotValidException {
        LOGGER.info("createOutpatientDepartment({})", treatmentDtoCreate);
        return treatmentService.createTreatment(treatmentDtoCreate);
    }


    /**
     * Get a treatment by id.
     *
     * @param id the id of the treatment
     * @return the treatment as dto
     */
    @Secured({"ADMIN", "DOCTOR", "SECRETARY", "PATIENT"})
    @GetMapping({"/{id}"})
    public TreatmentDto getTreatmentById(@PathVariable("id") Long id) {
        LOGGER.info("getTreatmentById({})", id);
        return treatmentService.getTreatmentById(id);
    }
}
