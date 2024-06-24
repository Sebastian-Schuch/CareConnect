package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.TestBase;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorWorkingHoursDto;
import at.ac.tuwien.sepr.groupphase.backend.repository.ApiKeyRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.DoctorRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "datagen"})
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class DataInterfaceEndpointTest extends TestBase {

    private static final String BASE_PATH = "/api/v1/data";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    private String validApiKey;

    public DataInterfaceEndpointTest() {
        super("dataInterface");
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(webAppContext)
            .apply(springSecurity())
            .build();

        validApiKey = apiKeyRepository.findAll().getFirst().getApiKey();
    }

    @Test
    public void testGetDoctorWorkingHours() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/doctor-working-hours")
                .header("Authorization", "Basic " + validApiKey)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        String content = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        List<DoctorWorkingHoursDto> doctorWorkingHours = mapper.readValue(content, new TypeReference<List<DoctorWorkingHoursDto>>() {
        });

        assertEquals(doctorRepository.countDistinctByTreatmentDoctors(), doctorWorkingHours.size());

    }

    @Test
    public void testGetDoctorWorkingHours_WithInvalidAPIKey_Expect403() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/doctor-working-hours")
                .header("Authorization", "Basic ")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andReturn();
    }

    @Test
    public void testGetDoctorWorkingHours_WithFilterThatIsInTheFuture_ExpectEmptyResponse() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/doctor-working-hours?startDate=2024-01-01T00:00:00Z&endDate=2024-01-01T00:00:00Z")
                .header("Authorization", "Basic " + validApiKey)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        String content = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        List<DoctorWorkingHoursDto> doctorWorkingHours = mapper.readValue(content, new TypeReference<List<DoctorWorkingHoursDto>>() {
        });

        assertTrue(doctorWorkingHours.isEmpty());

    }
}
