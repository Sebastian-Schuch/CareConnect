package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDto;
import at.ac.tuwien.sepr.groupphase.backend.service.DoctorService;
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
@RequestMapping(value = DoctorEndpoint.BASE_PATH)
public class DoctorEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "/api/v1/doctors";

    private final DoctorService doctorService;

    public DoctorEndpoint(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    /**
     * The create endpoint for the doctor.
     *
     * @param toCreate the data for the doctor to create
     * @return the created doctor
     */
    @PermitAll
    //TODO @Secured("SECRETARY")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public DoctorDto create(@Valid @RequestBody DoctorCreateDto toCreate) {
        LOG.info("POST " + BASE_PATH);
        LOG.debug("Body of request:\n{}", toCreate);
        return this.doctorService.createDoctor(toCreate);
    }

    /**
     * The get endpoint for the doctor.
     *
     * @param id the id of doctor requested
     * @return the doctor requested
     */
    @PermitAll
    //TODO @Secured({"SECRETARY", "ADMIN", "DOCTOR"})
    @GetMapping({"/{id}"})
    public DoctorDto get(@PathVariable("id") long id) {
        LOG.info("GET " + BASE_PATH + "/{}", id);
        return this.doctorService.getDoctorById(id);
    }

    /**
     * Get all the doctors from the repository.
     *
     * @return a list of all doctors
     */
    //TODO @Secured({"SECRETARY", "DOCTOR", "ADMIN"})
    @PermitAll
    @GetMapping
    public List<DoctorDto> getAll() {
        LOG.info("GET " + BASE_PATH);
        return this.doctorService.getAllDoctors();
    }
}
