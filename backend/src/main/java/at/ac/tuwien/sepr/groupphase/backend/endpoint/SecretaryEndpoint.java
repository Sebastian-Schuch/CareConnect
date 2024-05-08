package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDtoDetail;
import at.ac.tuwien.sepr.groupphase.backend.service.SecretaryService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = SecretaryEndpoint.BASE_PATH)
public class SecretaryEndpoint {
    static final String BASE_PATH = "/api/secretary";
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
    @PermitAll
    //@Secured("ROLE_ADMIN");
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public SecretaryDtoDetail create(@Valid @RequestBody SecretaryDtoCreate toCreate) {
        LOG.info("POST" + BASE_PATH);
        LOG.debug("Body of request:\n{}", toCreate);
        return this.secretaryService.create(toCreate);
    }


}
