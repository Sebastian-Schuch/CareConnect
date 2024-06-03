package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.TestBase;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "datagen"})
@AutoConfigureMockMvc
public class SecurityTest extends TestBase {

    private static final List<Class<?>> mappingAnnotations = Lists.list(
        RequestMapping.class,
        GetMapping.class,
        PostMapping.class,
        PutMapping.class,
        PatchMapping.class,
        DeleteMapping.class
    );

    private static final List<Class<?>> securityAnnotations = Lists.list(
        Secured.class,
        PreAuthorize.class,
        RolesAllowed.class,
        PermitAll.class,
        DenyAll.class,
        DeclareRoles.class
    );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private List<Object> components;

    ObjectWriter ow;

    static final String MEDICATION_BASE_PATH = "/api/v1/medications";

    static final String PATIENT_BASE_PATH = "/api/v1/patients";

    static final String ADMIN_TOKEN =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJzZWN1cmUtYmFja2VuZCIsImF1ZCI6InNlY3VyZS1hcHAiLCJzdWIiOiJhZG1pbiIsImV4cC" +
            "I6MTcxNTUyMjkyMywicm9sIjpbIkFETUlOIl19.3Srm1hYPbIsSQLOEv92yRERxSmPpsECw72Juna-WHDy7Y8yM1SMnDGe-vuRaFuAfLjRylvER7H6f5MBQFBwDPw";

    public SecurityTest() {
        super("security");
    }


    @BeforeEach
    public void beforeEach() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
        ow = objectMapper.writer().withDefaultPrettyPrinter();
    }

    /**
     * This ensures every Rest Method is secured with Method Security.
     * It is very easy to forget securing one method causing a security vulnerability.
     * Feel free to remove / disable / adapt if you do not use Method Security (e.g. if you prefer Web Security to define who may perform which actions) or want to use Method Security on the service layer.
     */

    @Test
    @WithMockUser(username = "patient", authorities = {"PATIENT"})
    public void givenPatientAuthorities_whenGetResourceSecuredForPatients_thenReturn200OkStatus() throws Exception {
        mockMvc
            .perform(MockMvcRequestBuilders.get(MEDICATION_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "doctor", authorities = {"DOCTOR"})
    public void givenDoctorAuthorities_whenGetResourceSecuredForDoctors_thenReturn200OkStatus() throws Exception {
        mockMvc
            .perform(MockMvcRequestBuilders.get(PATIENT_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    public void givenSecretaryAuthorities_whenGetResourceSecuredForSecretaries_thenReturn200OkStatus() throws Exception {
        mockMvc
            .perform(MockMvcRequestBuilders.get(PATIENT_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenAdminAuthorities_whenGetResourceSecuredForAdmins_thenReturn200OkStatus() throws Exception {
        mockMvc
            .perform(MockMvcRequestBuilders.get(MEDICATION_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

}
