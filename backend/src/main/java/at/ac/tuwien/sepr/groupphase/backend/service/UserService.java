package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {

    /**
     * Find a user in the context of Spring Security based on the email address
     * <br>
     * For more information have a look at this tutorial:
     * https://www.baeldung.com/spring-security-authentication-with-a-database
     *
     * @param email the email address
     * @return a Spring Security user
     * @throws UsernameNotFoundException is thrown if the specified user does not exists
     */
    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    /**
     * Find an application user based on the email address.
     *
     * @param email the email address
     * @return the credentials of a application user
     */
    Credential findApplicationUserByEmail(String email);

    /**
     * Log in a user.
     *
     * @param userLoginDto login credentials
     * @return the JWT, if successful
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are bad
     */
    String login(UserLoginDto userLoginDto);

    /**
     * Creates the Credential Entity with the data given.
     *
     * @param toCreate the data of the doctor for the credential entity
     * @return the users login data
     */
    PDDocument createDoctor(DoctorCreateDto toCreate);

    /**
     * Creates the Credential Entity with the data given.
     *
     * @param toCreate the data of the secretary for the credential entity
     * @return the users login data
     */
    PDDocument createSecretary(SecretaryCreateDto toCreate);

    /**
     * Creates the Credential Entity with the data given.
     *
     * @param toCreate the data of the patient for the credential entity
     * @return the users login data
     */
    PDDocument createPatient(PatientCreateDto toCreate);

    /**
     * Changes the password of the user who sent the request.
     *
     * @param newLogin the new user login data
     */
    void changePassword(UserLoginDto newLogin);
}
