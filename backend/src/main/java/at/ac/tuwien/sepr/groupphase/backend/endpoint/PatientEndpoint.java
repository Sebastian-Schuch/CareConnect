package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoUpdate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.PdfCouldNotBeCreatedException;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
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
@RequestMapping(value = PatientEndpoint.BASE_PATH)
public class PatientEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "/api/v1/patients";

    private final PatientService patientService;

    private final UserService userService;

    public PatientEndpoint(PatientService patientService, UserService userService) {
        this.patientService = patientService;
        this.userService = userService;
    }

    /**
     * The create endpoint for the patient.
     *
     * @param toCreate the data for the patient to create
     * @return the created patient
     */
    @Secured("SECRETARY")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<InputStreamResource> create(@Valid @RequestBody PatientDtoCreate toCreate) {
        LOG.info("POST " + BASE_PATH);
        LOG.debug("Body of request:\n{}", toCreate);
        try {
            this.userService.findApplicationUserByEmail(toCreate.email());
        } catch (NotFoundException ex) {
            // User was not found and therefor can be created
            PDDocument accountDataSheet = userService.createPatient(toCreate);
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
     * The get endpoint for the patient.
     *
     * @param id the id of patient requested
     * @return the patient requested
     */
    @Secured({"SECRETARY", "PATIENT", "DOCTOR"})
    @GetMapping({"/{id}"})
    public PatientDto get(@PathVariable("id") long id) {
        LOG.info("GET " + BASE_PATH + "/{}", id);
        if (patientService.isOwnRequest(id) || userService.isValidRequestOfRole(Role.SECRETARY) || userService.isValidRequestOfRole(Role.DOCTOR)) {
            return this.patientService.getPatientById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this resource");
        }
    }

    /**
     * Get all the patients from the repository.
     *
     * @return a list of all patients
     */
    @Secured({"SECRETARY", "DOCTOR"})
    @GetMapping
    public List<PatientDto> getAll() {
        LOG.info("GET " + BASE_PATH);
        return this.patientService.getAllPatients();
    }

    /**
     * Update the patient with the given id.
     *
     * @param id       the id of the patient to update
     * @param toUpdate the data to update the patient with
     * @return the updated patient
     */
    @Secured({"SECRETARY", "PATIENT"})
    @PutMapping({"/{id}"})
    public PatientDto update(@PathVariable("id") long id, @Valid @RequestBody PatientDtoUpdate toUpdate) {
        LOG.info("PUT " + BASE_PATH + "/{}", id);
        if (userService.isValidRequestOfRole(Role.SECRETARY) || patientService.isOwnRequest(id)) {
            return this.patientService.updatePatient(id, toUpdate);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this resource");
        }
    }

    /**
     * Search for patients.
     *
     * @param toSearch the search parameters
     * @return a list of patients matching the search parameters
     */
    @Secured({"SECRETARY"})
    @GetMapping({"/search"})
    public List<PatientDto> search(UserDtoSearch toSearch) {
        LOG.info("GET " + BASE_PATH + "/search");
        return this.patientService.searchPatients(toSearch);
    }
}
