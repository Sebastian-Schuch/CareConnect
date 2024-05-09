package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.service.SecretaryService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
@RequestMapping(value = SecretaryEndpoint.BASE_PATH)
public class SecretaryEndpoint {
    static final String BASE_PATH = "/api/secretaries";
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SecretaryService secretaryService;

    public SecretaryEndpoint(SecretaryService secretaryService) {
        this.secretaryService = secretaryService;
    }

    /**
     * The create endpoint for the secretary.
     *
     * @param toCreate the data for the secretary to create
     * @return the created secretary
     */

    //@Secured("ADMIN")
    @PermitAll
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public SecretaryDetailDto create(@Valid @RequestBody SecretaryCreateDto toCreate) {
        LOG.info("POST" + BASE_PATH);
        LOG.debug("Body of request:\n{}", toCreate);
        return this.secretaryService.create(toCreate);
    }

    /**
     * The get endpoint for the secretary.
     *
     * @param id the id of secretary requested
     * @return the secretary requested
     */

    @PermitAll
    @GetMapping({"/{id}"})
    //@Secured({"ADMIN", "PATIENT", "SECRETARY", "DOCTOR"})
    public SecretaryDetailDto getById(@PathVariable("id") long id) {
        LOG.info("GET" + BASE_PATH + "/{}", id);
        return secretaryService.getById(id);
    }

    /**
     * Get all secretaries from the repository.
     *
     * @return a list of all secretaries
     */
    @PermitAll
    //@Secured({"ADMIN", "PATIENT", "SECRETARY", "DOCTOR"})
    @GetMapping
    public List<SecretaryDetailDto> getAll() {
        LOG.info("GET " + BASE_PATH);
        return this.secretaryService.getAllSecretaries();
    }


}
