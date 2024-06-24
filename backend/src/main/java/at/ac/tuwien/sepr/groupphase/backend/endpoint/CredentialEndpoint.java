package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdminDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChangePasswordDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.exception.PdfCouldNotBeCreatedException;
import at.ac.tuwien.sepr.groupphase.backend.service.AdminService;
import at.ac.tuwien.sepr.groupphase.backend.service.DoctorService;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import at.ac.tuwien.sepr.groupphase.backend.service.SecretaryService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

@RestController
@RequestMapping(value = CredentialEndpoint.BASE_PATH)
public class CredentialEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "/api/v1/credentials";

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final SecretaryService secretaryService;

    private final AdminService adminService;
    private final CustomUserDetailService customUserDetailService;

    public CredentialEndpoint(PatientService patientService, DoctorService doctorService, SecretaryService secretaryService, AdminService adminService, CustomUserDetailService customUserDetailService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.secretaryService = secretaryService;
        this.adminService = adminService;
        this.customUserDetailService = customUserDetailService;
    }

    /**
     * Get the patient by the email of the currently logged-in user.
     *
     * @return the patientDto
     */
    @Secured({"PATIENT"})
    @GetMapping({"/patients"})
    public PatientDtoSparse getPatientByToken() {
        LOG.trace("getPatientByToken()");
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Credential credential = customUserDetailService.findApplicationUserByEmail(email);
        return patientService.findPatientByCredential(credential);
    }

    /**
     * Get the doctor by the email of the currently logged-in user.
     *
     * @return the doctorDto
     */
    @Secured({"DOCTOR"})
    @GetMapping({"/doctors"})
    public DoctorDtoSparse getDoctorByToken() {
        LOG.trace("getDoctorByToken()");
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Credential credential = customUserDetailService.findApplicationUserByEmail(email);
        return doctorService.findDoctorByCredential(credential);
    }

    /**
     * Get the secretary by the email of the currently logged-in user.
     *
     * @return the secretaryDto
     */
    @Secured({"SECRETARY"})
    @GetMapping({"/secretaries"})
    public SecretaryDtoSparse getSecretaryByToken() {
        LOG.trace("getSecretaryByToken()");
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Credential credential = customUserDetailService.findApplicationUserByEmail(email);
        return secretaryService.findSecretaryByCredential(credential);
    }

    /**
     * Get the admin by the email of the currently logged-in user.
     *
     * @return the adminDto
     */
    @Secured({"ADMIN"})
    @GetMapping({"/admins"})
    public AdminDtoSparse getAdminByToken() {
        LOG.trace("getAdminByToken()");
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Credential credential = customUserDetailService.findApplicationUserByEmail(email);
        return adminService.findAdministratorByCredential(credential);
    }

    /**
     * Disable user endpoint.
     *
     * @param toDisable the user to disable
     * @return a Response Entity with the status and a message depending on the validity of the request
     */
    @Secured({"ADMIN", "SECRETARY"})
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping({"/disable"})
    public String disableUser(@RequestBody UserLoginDto toDisable) {
        LOG.trace("disableUser()");
        Credential credential = customUserDetailService.findApplicationUserByEmail(toDisable.getEmail());
        if (customUserDetailService.isValidRequestOfRole(Role.SECRETARY) && credential.getRole() == Role.PATIENT
            || (customUserDetailService.isValidRequestOfRole(Role.ADMIN) && (credential.getRole() == Role.DOCTOR || credential.getRole() == Role.SECRETARY || credential.getRole() == Role.ADMIN))) {
            customUserDetailService.disableUser(toDisable.getEmail());
            return "User disabled";
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to disable this user.");
        }
    }

    /**
     * Change password endpoint.
     *
     * @param passwords the new password
     * @return a Response Entity with the status and a message depending on the validity of the request
     */
    @Secured({"SECRETARY", "PATIENT", "ADMIN", "DOCTOR"})
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping
    public String changePassword(@RequestBody ChangePasswordDto passwords) {
        LOG.info("PUT " + BASE_PATH);
        LOG.debug("Body of request:\n{}", passwords);
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!principal.equals(passwords.email())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to change the password of another user.");
        }

        customUserDetailService.changePassword(passwords);
        return "Password changed successfully.";
    }

    /**
     * Reset password endpoint.
     *
     * @param toReset the user to reset the password for
     * @return the user login PDF stream
     */
    @Secured({"SECRETARY", "ADMIN"})
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/reset")
    public ResponseEntity<InputStreamResource> resetPassword(@RequestBody UserLoginDto toReset) {
        LOG.info("POST " + BASE_PATH);
        LOG.debug("Body of request:\n{}", toReset);
        Credential credential = customUserDetailService.findApplicationUserByEmail(toReset.getEmail());
        if (credential.getRole() == Role.PATIENT && customUserDetailService.isValidRequestOfRole(Role.SECRETARY)
            || (credential.getRole() == Role.DOCTOR || credential.getRole() == Role.SECRETARY || credential.getRole() == Role.ADMIN) && customUserDetailService.isValidRequestOfRole(Role.ADMIN)) {
            PDDocument accountDataSheet = customUserDetailService.resetPassword(toReset.getEmail());
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
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to reset the password of this user.");
        }
    }
}
