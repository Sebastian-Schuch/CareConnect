package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDtoUpdate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.PdfCouldNotBeCreatedException;
import at.ac.tuwien.sepr.groupphase.backend.service.SecretaryService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import jakarta.validation.Valid;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = SecretaryEndpoint.BASE_PATH)
public class SecretaryEndpoint {
    static final String BASE_PATH = "/api/v1/secretaries";
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SecretaryService secretaryService;
    private final UserService userService;

    public SecretaryEndpoint(SecretaryService secretaryService, UserService userService) {
        this.secretaryService = secretaryService;
        this.userService = userService;
    }

    /**
     * The create endpoint for the secretary.
     *
     * @param toCreate the data for the secretary to create
     * @return the created secretary
     */

    @Secured("ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<InputStreamResource> create(@Valid @RequestBody SecretaryDtoCreate toCreate) {
        LOG.info("POST" + BASE_PATH);
        LOG.debug("Body of request:\n{}", toCreate);
        try {
            this.userService.findApplicationUserByEmail(toCreate.email());
        } catch (NotFoundException ex) {
            // User was not found and therefor can be created
            PDDocument accountDataSheet = userService.createSecretary(toCreate);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayInputStream inputStream = null;
            try {
                accountDataSheet.save(outputStream);
                accountDataSheet.close();
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Disposition", "attachment; filename=accountDataSheet.pdf");

                byte[] bytes = outputStream.toByteArray();

                inputStream = new ByteArrayInputStream(bytes);
                return ResponseEntity
                    .created(URI.create(""))
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(inputStream));
            } catch (IOException e) {
                throw new PdfCouldNotBeCreatedException("Could not create PDF document: " + e.getMessage());
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        LOG.error("Could not close input stream: {}", e.getMessage());
                    }
                }
            }
        }
        throw new ConflictException("User with email " + toCreate.email() + " already exists");
    }

    /**
     * The get endpoint for the secretary.
     *
     * @param id the id of secretary requested
     * @return the secretary requested
     */

    @GetMapping({"/{id}"})
    @Secured({"ADMIN", "SECRETARY", "DOCTOR"})
    public SecretaryDtoSparse getById(@PathVariable("id") long id) {
        LOG.info("GET" + BASE_PATH + "/{}", id);
        return secretaryService.getById(id);
    }

    /**
     * Get all secretaries from the repository.
     *
     * @return a list of all secretaries
     */
    @Secured({"ADMIN", "SECRETARY", "DOCTOR"})
    @GetMapping
    public List<SecretaryDtoSparse> getAll() {
        LOG.info("GET " + BASE_PATH);
        return this.secretaryService.getAllSecretaries();
    }

    /**
     * Update a secretary.
     *
     * @param id       the id of the secretary to update
     * @param toUpdate the data to update the secretary with
     * @return the updated secretary
     */
    @Secured({"ADMIN", "SECRETARY"})
    @PutMapping({"/{id}"})
    public SecretaryDtoSparse update(@PathVariable("id") long id, @Valid @RequestBody SecretaryDtoUpdate toUpdate) {
        LOG.info("PUT " + BASE_PATH + "/{}", id);
        if (secretaryService.isOwnRequest(id) || userService.isValidRequestOfRole(Role.ADMIN)) {
            return this.secretaryService.updateSecretary(id, toUpdate);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this resource");
        }
    }

    /**
     * Search for secretaries.
     *
     * @param toSearch the data to search for
     * @return a list of secretaries that match the search criteria
     */
    @Secured({"ADMIN"})
    @GetMapping({"/search"})
    public List<SecretaryDtoSparse> search(UserDtoSearch toSearch) {
        LOG.info("GET " + BASE_PATH + "/search");
        return this.secretaryService.searchSecretaries(toSearch);
    }
}
