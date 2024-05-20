package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AllergyCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AllergyDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.AllergyMapper;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.AllergyService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PermitAll
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new allergy")
    public AllergyDto create(@Valid @RequestBody AllergyCreateDto toCreate) {
        LOGGER.info("POST " + BASE_PATH);
        return allergyMapper.allergyToDto(this.allergyService.createAllergy(toCreate));
    }

    @PermitAll
    @GetMapping(value = "/{id}")
    public AllergyDto find(@PathVariable(name = "id") Long id) {
        LOGGER.info("GET " + BASE_PATH + "/{}", id);
        return allergyMapper.allergyToDto(this.allergyService.findById(id));
    }

    @PermitAll
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

    @PermitAll
    @PostMapping(value = "/{id}")
    @Operation(summary = "Update a new allergy")
    public ResponseEntity<AllergyDto> update(@PathVariable(name = "id") Long id, @RequestBody AllergyDto toUpdate) {
        LOGGER.info("POST " + BASE_PATH + "/{}", id);
        try {
            AllergyDto allergy = allergyMapper.allergyToDto(this.allergyService.updateAllergy(new AllergyDto(id, toUpdate.getName())));
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(allergy);

        } catch (NotFoundException e) {
            LOGGER.info("Could not find allergy with id {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
