package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.TestBase;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.repository.DoctorRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.MedicationRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.OutpatientDepartmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PatientRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TreatmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "datagen"})
@AutoConfigureMockMvc
class TreatmentEndpointTest extends TestBase {

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_PATH = "/api/v1/treatments";
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

    public TreatmentEndpointTest() {
        super("treatment");
    }

    @BeforeEach
    public void beforeEach() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ow = objectMapper.writer().withDefaultPrettyPrinter();
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
                null,
                null,
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

    //TODO: Add further tests as soon as test-data is available (Issue #41)
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
