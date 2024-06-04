package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.service.DoctorService;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import at.ac.tuwien.sepr.groupphase.backend.service.SecretaryService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = CredentialEndpoint.BASE_PATH)
public class CredentialEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "/api/v1/credentials";

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final SecretaryService secretaryService;
    private final CustomUserDetailService customUserDetailService;

    public CredentialEndpoint(PatientService patientService, DoctorService doctorService, SecretaryService secretaryService, CustomUserDetailService customUserDetailService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.secretaryService = secretaryService;
        this.customUserDetailService = customUserDetailService;
    }

    /**
     * Get the patient by the email of the currently logged-in user.
     *
     * @return the patientDto
     */
    @Secured({"ADMIN", "DOCTOR", "SECRETARY", "PATIENT"})
    @GetMapping({"/patients"})
    public PatientDto getPatientByToken() {
        LOG.trace("getPatientByToken()");
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Credential credential = customUserDetailService.findApplicationUserByEmail(email);
        return patientService.findPatientByCredential(credential);
    }

    /**
     * Get the doctor by the email of the currently logged-in user.
     *
     * @return the patientDto
     */
    @Secured({"ADMIN", "DOCTOR", "SECRETARY"})
    @GetMapping({"/doctors"})
    public DoctorDto getDoctorByToken() {
        LOG.trace("getDoctorByToken()");
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Credential credential = customUserDetailService.findApplicationUserByEmail(email);
        return doctorService.findDoctorByCredential(credential);
    }

    /**
     * Get the secretary by the email of the currently logged-in user.
     *
     * @return the patientDto
     */
    @Secured({"ADMIN", "DOCTOR", "SECRETARY"})
    @GetMapping({"/secretaries"})
    public SecretaryDto getSecretaryByToken() {
        LOG.trace("getSecretaryByToken()");
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Credential credential = customUserDetailService.findApplicationUserByEmail(email);
        return secretaryService.findSecretaryByCredential(credential);
    }

    @Secured({"ADMIN", "SECRETARY"})
    @PatchMapping({"/disable"})
    public ResponseEntity<String> disableUser(@RequestBody UserLoginDto toDisable) {
        LOG.trace("disableUser()");
        Credential credential = customUserDetailService.findApplicationUserByEmail(toDisable.getEmail());
        if (secretaryService.isValidSecretaryRequest() && credential.getRole() == Role.PATIENT) {
            customUserDetailService.disableUser(toDisable.getEmail());
            return ResponseEntity.ok("User disabled");
        } else {
            //if(adminService.isValidAdminRequest() && (credential.getRole() == Role.DOCTOR || credential.getRole() == Role.SECRETARY || credential.getRole() == Role.ADMIN)) {
            //customUserDetailService.disableUser(email);
            //} else {
            return ResponseEntity.status(403).body("You are not allowed to disable this user.");
        }
    }

    /**
     * Change password endpoint.
     *
     * @param newLogin the new login
     * @return a Response Entity with the status and a message depending on the validity of the request
     */
    @Secured({"SECRETARY", "PATIENT", "ADMIN", "DOCTOR"})
    @PatchMapping
    public ResponseEntity<String> changePassword(@RequestBody UserLoginDto newLogin) {
        LOG.info("PUT " + BASE_PATH);
        LOG.debug("Body of request:\n{}", newLogin);
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!principal.equals(newLogin.getEmail())) {
            return ResponseEntity.status(403).body("You can only change your own password.");
        }

        customUserDetailService.changePassword(newLogin);
        return ResponseEntity.ok().body("Password changed successfully.");
    }
}
