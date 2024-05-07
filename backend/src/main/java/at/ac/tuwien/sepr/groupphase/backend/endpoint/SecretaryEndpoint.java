package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.service.SecretaryService;
import jakarta.annotation.security.PermitAll;
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

    @PermitAll
    //@Secured("ROLE_ADMIN");
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public SecretaryDetailDto create(@RequestBody SecretaryCreateDto toCreate) {
        LOG.info("POST" + BASE_PATH);
        LOG.debug("Body of request:\n{}", toCreate);
        return this.secretaryService.create(toCreate);
    }


}
