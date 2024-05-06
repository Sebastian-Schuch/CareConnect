package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PatientEndpoint {
    private final PatientService patientService;

    public PatientEndpoint(PatientService patientService) {
        this.patientService = patientService;
    }

    @Secured("ROLE_USER")
    @PostMapping
    public PatientDto create(@RequestBody PatientCreateDto toCreate) {
        return this.patientService.createPatient(toCreate);
    }
}
