package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepr.groupphase.backend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TreatmentEndpointTest {

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_PATH = "/api/v1/treatment";
    ObjectWriter ow;

    @Autowired
    TreatmentRepository treatmentRepository;

    @Autowired
    PatientRepository patientRepository;
    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    MedicationRepository medicationRepository;
    @Autowired
    OutpatientDepartmentRepository outpatientDepartmentRepository;

    @BeforeEach
    public void beforeEach() {
        treatmentRepository.deleteAll();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ow = objectMapper.writer().withDefaultPrettyPrinter();
    }

    @AfterEach
    public void cleanup() {
        treatmentRepository.deleteAll();
    }

    @Test
    void givenInvalidUrl_whenSendRequest_Returns404NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/invalid")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    private TreatmentDtoCreate createTreatmentDtoCreate() {
        return new TreatmentDtoCreate(
            "behandlung 1",
            new Date(),
            new Date(),
            new PatientDto(
                2L,
                "1234567890",
                "test2",
                "test1",
                "test@g.c",
                "om08slg7p96o8rvl7ea0fl514g",
                true

            ),
            new OutpatientDepartmentDto(
                1L,
                "departement2",
                "testtesttest",
                10,
                null
            ),
            "sefsfse fselfiuhsokefh osuhefos",
            List.of(new DoctorDto(
                3L,
                "Alice",
                "Smith",
                "alice@test.c",
                "cpde6mofjrqnbmc6t3d7cpmp6d",
                true)),
            List.of(new TreatmentMedicineDtoCreate(
                new MedicationDto(1L, "m2", true),
                    "mg",
                200, new Date())
                )
        );
    }

    // TODO: Add further tests as soon as test-data is available (Issue #41)
    @Test
    void givenValidTreatmentDto_whenCreateTreatment_thenReturnCreatedTreatment() throws Exception {
        Date date = new Date();
        TreatmentDtoCreate treatmentDtoCreate = createTreatmentDtoCreate();
        String json = ow.writeValueAsString(treatmentDtoCreate);
        /*byte[] body = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();
        List<PatientDto> treatment = objectMapper.readerFor(TreatmentDto.class).<PatientDto>readValues(body).readAll();
       assertNotNull(treatment);*/
    }
}
