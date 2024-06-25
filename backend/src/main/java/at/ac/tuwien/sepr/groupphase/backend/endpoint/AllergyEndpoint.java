package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AllergyDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AllergyDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AllergyPageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.AllergyMapper;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.AllergyService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/allergies")
public class AllergyEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String BASE_PATH = "/api/v1/allergies";
    private final AllergyService allergyService;
    private final AllergyMapper allergyMapper;

    public AllergyEndpoint(AllergyService allergyService, AllergyMapper allergyMapper) {
        this.allergyService = allergyService;
        this.allergyMapper = allergyMapper;
    }

    /**
     * Create a new allergy.
     *
     * @param toCreate the data for the allergy to create
     * @return the created allergy
     */
    @Secured({"ADMIN"})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new allergy")
    public AllergyDto create(@Valid @RequestBody AllergyDtoCreate toCreate) {
        LOG.info("POST " + BASE_PATH);
        return allergyMapper.allergyToDto(this.allergyService.createAllergy(toCreate));
    }

    /**
     * Get a specific allergy.
     *
     * @param id the id of the allergy requested
     * @return the allergy requested
     */
    @Secured({"ADMIN", "SECRETARY", "DOCTOR", "PATIENT"})
    @GetMapping(value = "/{id}")
    public AllergyDto find(@PathVariable(name = "id") Long id) {
        LOG.info("GET " + BASE_PATH + "/{}", id);
        return allergyMapper.allergyToDto(this.allergyService.findById(id));
    }

    /**
     * Get all allergies.
     *
     * @return list of allergies
     */
    @Secured({"ADMIN", "SECRETARY", "DOCTOR", "PATIENT"})
    @GetMapping
    @Operation(summary = "Get list of allergies without details")
    public List<AllergyDto> findAll() {
        LOG.info("GET " + BASE_PATH);
        try {
            return allergyMapper.allergyToDto(allergyService.findAll());
        } catch (NotFoundException e) {
            LOG.info("No allergies found");
            return List.of();
        }
    }

    /**
     * Update an existing allergy.
     *
     * @param id       the id of the allergy to update
     * @param toUpdate the data to update the allergy with
     * @return the updated allergy
     */
    @Secured({"ADMIN"})
    @PutMapping(value = "/{id}")
    @Operation(summary = "Update a new allergy")
    public AllergyDto update(@PathVariable(name = "id") Long id, @RequestBody AllergyDto toUpdate) {
        LOG.info("POST " + BASE_PATH + "/{}", id);
        try {
            return allergyMapper.allergyToDto(this.allergyService.updateAllergy(new AllergyDto(id, toUpdate.name(), toUpdate.active())));
        } catch (NotFoundException e) {
            LOG.info("Could not find allergy with id {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find allergy");
        }
    }

    /**
     * Search for allergies with specified criteria.
     *
     * @param page       the page number
     * @param size       the size of the page
     * @param searchTerm the name of the allergy to search for
     * @return a page of all allergies that match the criteria
     */
    @Secured({"ADMIN", "DOCTOR", "SECRETARY", "PATIENT"})
    @GetMapping({"/search"})
    public AllergyPageDto searchAllergies(
        @RequestParam(name = "page", defaultValue = "0") Integer page,
        @RequestParam(name = "size", defaultValue = "20") Integer size,
        @RequestParam(name = "allergyName", defaultValue = "") String searchTerm
    ) {
        LOG.info("searchAllergies({}, {}, {})", page, size, searchTerm);
        return allergyService.searchAllergies(searchTerm, page, size);
    }

    /**
     * Set an allergy inactive by its id.
     *
     * @param id the id of the allergy
     * @return the inactive allergy
     */
    @Secured({"ADMIN"})
    @DeleteMapping({"/{id}"})
    public AllergyDto setAllergyInactiveById(@PathVariable("id") Long id) {
        LOG.info("setOutpatientDepartmentInactiveById({})", id);
        return allergyService.setAllergyInactive(id);
    }
}
