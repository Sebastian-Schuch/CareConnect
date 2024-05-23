package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.PdfCouldNotBeCreatedException;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<InputStreamResource> create(@Valid @RequestBody PatientCreateDto toCreate) {
        LOG.info("POST " + BASE_PATH);
        LOG.debug("Body of request:\n{}", toCreate);
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

    /**
     * The get endpoint for the patient.
     *
     * @param id the id of patient requested
     * @return the patient requested
     */
    @Secured({"SECRETARY", "PATIENT", "ADMIN", "DOCTOR"})
    @GetMapping({"/{id}"})
    public PatientDto get(@PathVariable("id") long id) {
        LOG.info("GET " + BASE_PATH + "/{}", id);
        return this.patientService.getPatientById(id);
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
}
