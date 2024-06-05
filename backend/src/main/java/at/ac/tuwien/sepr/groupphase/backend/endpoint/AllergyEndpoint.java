package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AllergyDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AllergyDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.AllergyMapper;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.AllergyService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/allergies")
public class AllergyEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
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
        LOGGER.info("POST " + BASE_PATH);
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
        LOGGER.info("GET " + BASE_PATH + "/{}", id);
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
        LOGGER.info("GET " + BASE_PATH);
        try {
            return allergyMapper.allergyToDto(allergyService.findAll());
        } catch (NotFoundException e) {
            LOGGER.info("No allergies found");
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
    @PostMapping(value = "/{id}")
    @Operation(summary = "Update a new allergy")
    public AllergyDto update(@PathVariable(name = "id") Long id, @RequestBody AllergyDto toUpdate) {
        LOGGER.info("POST " + BASE_PATH + "/{}", id);
        try {
            return allergyMapper.allergyToDto(this.allergyService.updateAllergy(new AllergyDto(id, toUpdate.getName())));
        } catch (NotFoundException e) {
            LOGGER.info("Could not find allergy with id {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find allergy");
        }
    }
}
