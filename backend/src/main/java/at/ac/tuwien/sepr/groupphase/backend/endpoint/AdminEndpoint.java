package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdminDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdminDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdminDtoUpdate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.PdfCouldNotBeCreatedException;
import at.ac.tuwien.sepr.groupphase.backend.service.AdminService;
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
@RequestMapping(value = AdminEndpoint.BASE_PATH)
public class AdminEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "/api/v1/admins";

    private final AdminService adminService;
    private final UserService userService;

    public AdminEndpoint(AdminService adminService, UserService userService) {
        this.adminService = adminService;
        this.userService = userService;
    }

    /**
     * The create endpoint for the administrator.
     *
     * @param toCreate the data for the administrator to create
     * @return the created administrator
     */
    @Secured({"ADMIN"})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<InputStreamResource> create(@Valid @RequestBody AdminDtoCreate toCreate) {
        LOG.info("POST " + BASE_PATH);
        LOG.debug("Body of request:\n{}", toCreate);
        try {
            this.userService.findApplicationUserByEmail(toCreate.email());
        } catch (NotFoundException ex) {
            // User was not found and therefor can be created
            PDDocument accountDataSheet = userService.createAdministrator(toCreate);
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
     * The get endpoint for the administrator.
     *
     * @param id the id of administrator requested
     * @return the administrator requested
     */
    @Secured({"ADMIN"})
    @GetMapping({"/{id}"})
    public AdminDtoSparse get(@PathVariable("id") long id) {
        LOG.info("GET " + BASE_PATH + "/{}", id);
        return this.adminService.getAdministratorById(id);
    }

    /**
     * Get all the administrators from the repository.
     *
     * @return a list of all administrators
     */
    @Secured({"ADMIN"})
    @GetMapping
    public List<AdminDtoSparse> getAll() {
        LOG.info("GET " + BASE_PATH);
        return this.adminService.getAllAdministrators();
    }

    /**
     * Update a administrator.
     *
     * @param id       the id of the administrator to update
     * @param toUpdate the data to update the administrator with
     * @return the updated administrator
     */
    @Secured({"ADMIN"})
    @PutMapping({"/{id}"})
    public AdminDtoSparse update(@PathVariable("id") long id, @Valid @RequestBody AdminDtoUpdate toUpdate) {
        LOG.info("PUT " + BASE_PATH + "/{}", id);
        if (adminService.isOwnRequest(id) || userService.isValidRequestOfRole(Role.ADMIN)) {
            return this.adminService.updateAdministrator(id, toUpdate);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this resource");
        }

    }

    /**
     * Search for administrators.
     *
     * @param toSearch the data to search for
     * @return a list of administrators matching the search criteria
     */
    @Secured({"ADMIN"})
    @GetMapping({"/search"})
    public List<AdminDtoSparse> search(UserDtoSearch toSearch) {
        LOG.info("GET " + BASE_PATH + "/search");
        return this.adminService.searchAdministrators(toSearch);
    }
}
