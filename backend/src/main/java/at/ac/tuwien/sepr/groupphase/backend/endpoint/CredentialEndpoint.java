package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = CredentialEndpoint.BASE_PATH)
public class CredentialEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "/api/v1/credentials";

    private final PatientService patientService;
    private final CustomUserDetailService customUserDetailService;

    public CredentialEndpoint(PatientService patientService, CustomUserDetailService customUserDetailService) {
        this.patientService = patientService;
        this.customUserDetailService = customUserDetailService;
    }

    /**
     * Get the patient by the email of the currently logged-in user.
     *
     * @return the patientDto
     */
    @Secured({"ADMIN", "DOCTOR", "SECRETARY", "PATIENT"})
    @GetMapping
    public PatientDto getPatient() {
        LOG.trace("findPatientByEmail()");
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Credential credential = customUserDetailService.findApplicationUserByEmail(email);
        return patientService.findPatientByCredential(credential);
    }
}
