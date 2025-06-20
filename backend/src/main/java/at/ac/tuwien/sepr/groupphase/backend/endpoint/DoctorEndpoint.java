package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoUpdate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.PdfCouldNotBeCreatedException;
import at.ac.tuwien.sepr.groupphase.backend.service.DoctorService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import jakarta.validation.Valid;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping(value = DoctorEndpoint.BASE_PATH)
public class DoctorEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "/api/v1/doctors";

    private final DoctorService doctorService;
    private final UserService userService;

    public DoctorEndpoint(DoctorService doctorService, UserService userService) {
        this.doctorService = doctorService;
        this.userService = userService;
    }

    /**
     * The create endpoint for the doctor.
     *
     * @param toCreate the data for the doctor to create
     * @return the created doctor
     */
    @Secured("ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<InputStreamResource> create(@Valid @RequestBody DoctorDtoCreate toCreate) {
        LOG.info("POST " + BASE_PATH);
        LOG.debug("Body of request:\n{}", toCreate);
        try {
            this.userService.findApplicationUserByEmail(toCreate.email());
        } catch (NotFoundException ex) {
            // User was not found and therefor can be created
            PDDocument accountDataSheet = userService.createDoctor(toCreate);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayInputStream inputStream = null;
            try {
                accountDataSheet.save(outputStream);
                accountDataSheet.close();
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Disposition", "attachment; filename=accountDataSheet.pdf");

                byte[] bytes = outputStream.toByteArray();

                inputStream = new ByteArrayInputStream(bytes);
                return ResponseEntity.created(URI.create("")).headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(inputStream));
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
     * The get endpoint for the doctor.
     *
     * @param id the id of doctor requested
     * @return the doctor requested
     */
    @Secured({"SECRETARY", "ADMIN", "DOCTOR"})
    @GetMapping({"/{id}"})
    public DoctorDtoSparse get(@PathVariable("id") long id) {
        LOG.info("GET " + BASE_PATH + "/{}", id);
        return this.doctorService.getDoctorById(id);
    }

    /**
     * Update a doctor.
     *
     * @param id       the id of the doctor to update
     * @param toUpdate the data to update the doctor with
     * @return the updated doctor
     */
    @Secured({"ADMIN", "DOCTOR"})
    @PutMapping({"/{id}"})
    public DoctorDtoSparse update(@PathVariable("id") long id, @Valid @RequestBody DoctorDtoUpdate toUpdate) {
        LOG.info("PUT " + BASE_PATH + "/{}", id);
        if (doctorService.isOwnRequest(id) || userService.isValidRequestOfRole(Role.ADMIN)) {
            return this.doctorService.updateDoctor(id, toUpdate);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this resource");
        }

    }

    /**
     * Search for doctors.
     *
     * @param toSearch the data to search for
     * @return a list of doctors matching the search criteria
     */
    @Secured({"ADMIN"})
    @GetMapping({"/search"})
    public List<DoctorDtoSparse> search(UserDtoSearch toSearch) {
        LOG.info("GET " + BASE_PATH + "/search");
        return this.doctorService.searchDoctors(toSearch);
    }

    /**
     * Get the doctor by email.
     *
     * @param page       the page number
     * @param size       the size of the page
     * @param searchTerm the email of the doctor
     * @return the doctor with the email given
     */
    @Secured({"SECRETARY", "DOCTOR", "ADMIN"})
    @GetMapping
    public Page<DoctorDtoSparse> getDoctors(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "50") int size, @RequestParam(name = "searchTerm", defaultValue = "") String searchTerm) {
        LOG.info("GET " + BASE_PATH);
        Pageable pageable = PageRequest.of(page, size);
        return doctorService.getDoctors(searchTerm, pageable);
    }
}
