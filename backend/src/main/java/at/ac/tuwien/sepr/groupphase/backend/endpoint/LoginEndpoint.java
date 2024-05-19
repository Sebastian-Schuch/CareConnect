package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

/**
 * REST controller for authentication.
 */

@RestController
@RequestMapping(value = LoginEndpoint.BASE_PATH)
public class LoginEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static final String BASE_PATH = "/api/v1/authentication";
    private final UserService userService;

    public LoginEndpoint(UserService userService) {
        this.userService = userService;
    }

    /**
     * Login endpoint.
     *
     * @param userLoginDto the user login
     * @return the jwt as a string
     */
    @PostMapping
    public String login(@RequestBody UserLoginDto userLoginDto) {
        LOG.info("POST " + BASE_PATH);
        LOG.debug("Body of request:\n{}", userLoginDto);
        return userService.login(userLoginDto);
    }

    /**
     * Change password endpoint.
     *
     * @param newLogin the new login
     * @return a Response Entity with the status and a message depending on the validity of the request
     */
    @PutMapping
    public ResponseEntity<String> changePassword(@RequestBody UserLoginDto newLogin) {
        LOG.info("PUT " + BASE_PATH);
        LOG.debug("Body of request:\n{}", newLogin);
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!principal.equals(newLogin.getEmail())) {
            return ResponseEntity.status(403).body("You can only change your own password.");
        }

        userService.changePassword(newLogin);
        return ResponseEntity.ok().body("Password changed successfully.");
    }

}
