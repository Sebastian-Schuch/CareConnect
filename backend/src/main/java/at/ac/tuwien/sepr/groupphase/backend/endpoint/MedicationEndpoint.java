package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationPageDto;
import at.ac.tuwien.sepr.groupphase.backend.service.MedicationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = MedicationEndpoint.BASE_PATH)
public class MedicationEndpoint {
    static final String BASE_PATH = "/api/v1/medications";
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MedicationService medicationService;

    public MedicationEndpoint(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    /**
     * The create endpoint for the medication.
     *
     * @param toCreate the data for the medication to create
     *
     * @return the created medication
     */
    @Secured("ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public MedicationDto create(@Valid @RequestBody MedicationDtoCreate toCreate) {
        LOG.info("POST" + BASE_PATH);
        LOG.debug("Body of request:\n{}", toCreate);
        return this.medicationService.create(toCreate);
    }

    /**
     * The get endpoint for the medication.
     *
     * @param id the id of medication requested
     * @return the medication requested
     */

    @GetMapping({"/{id}"})
    @Secured({"ADMIN", "PATIENT", "SECRETARY", "DOCTOR"})
    public MedicationDto getById(@PathVariable("id") long id) {
        LOG.info("GET" + BASE_PATH + "/{}", id);
        return medicationService.getById(id);
    }

    /**
     * Get all medications from the repository.
     *
     * @return a list of all medications
     */
    @Secured({"ADMIN", "PATIENT", "SECRETARY", "DOCTOR"})
    @GetMapping
    public List<MedicationDto> getAll() {
        LOG.info("GET " + BASE_PATH);
        return this.medicationService.getAllMedications();
    }

    /**
     * The delete endpoint for the medication.
     *
     * @param id the id of medication to delete
     * @return the disabled medication
     */
    @Secured({"ADMIN"})
    @DeleteMapping({"/{id}"})
    public MedicationDto deleteById(@PathVariable("id") long id) {
        LOG.info("DELETE" + BASE_PATH + "/{}", id);
        return medicationService.disableById(id);
    }

    /**
     * Search for medications with specified criteria.
     *
     * @param page       the page number
     * @param size       the size of the page
     * @param searchTerm the name of the medication to search for
     * @return a page of all medications that match the criteria
     */
    @Secured({"ADMIN", "DOCTOR", "SECRETARY", "PATIENT"})
    @GetMapping({"/search"})
    public MedicationPageDto searchMedications(
        @RequestParam(name = "page", defaultValue = "0") Integer page,
        @RequestParam(name = "size", defaultValue = "20") Integer size,
        @RequestParam(name = "medicationName", defaultValue = "") String searchTerm
    ) {
        LOG.info("searchMedications({}, {}, {})", page, size, searchTerm);
        return medicationService.searchMedications(searchTerm, page, size);
    }

    /**
     * The update endpoint for the medication.
     *
     * @param id       the id of the medication to update
     * @param toUpdate the data to update the medication with
     * @return the updated medication
     */
    @Secured({"ADMIN"})
    @PutMapping(value = "/{id}")
    public MedicationDto update(@PathVariable(name = "id") Long id, @RequestBody MedicationDto toUpdate) {
        LOG.info("PUT" + BASE_PATH + "/{}", id);
        return medicationService.update(new MedicationDto(id, toUpdate.name(), toUpdate.active(), toUpdate.unitOfMeasurement()));
    }

    @Secured({"ADMIN", "PATIENT", "SECRETARY", "DOCTOR"})
    @GetMapping("/count")
    public int getMedicationCount() {
        LOG.info("GET " + BASE_PATH + "/count");
        return this.medicationService.getMedicationCount();
    }
}
