package at.ac.tuwien.sepr.groupphase.backend.integrationtest.treatment.treatmentMedicine;

import at.ac.tuwien.sepr.groupphase.backend.TestBase;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.entity.TreatmentMedicine;
import at.ac.tuwien.sepr.groupphase.backend.exception.DtoValidationException;
import at.ac.tuwien.sepr.groupphase.backend.integrationtest.treatment.util.TreatmentTestUtils;
import at.ac.tuwien.sepr.groupphase.backend.repository.TreatmentMedicineRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.MedicationServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.TreatmentMedicineServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.util.DtoValidationUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;
import org.springframework.web.context.WebApplicationContext;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "datagen"})
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class TreatmentMedicineEndpointIntegrationTest extends TestBase {

    private static final String BASE_PATH = "/api/v1/treatmentMedicines";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext webAppContext;
    @Autowired
    private Validator validator;
    @Autowired
    private MedicationServiceImpl medicationService;
    @Autowired
    private TreatmentMedicineServiceImpl treatmentMedicineService;
    @Autowired
    private TreatmentMedicineRepository treatmentMedicineRepository;
    @Autowired
    private TreatmentTestUtils treatmentTestUtils;

    private DtoValidationUtils dtoValidator;
    private ObjectWriter ow;

    private TreatmentMedicineDtoCreate TREATMENT_MEDICINE_DTO_CREATE_VALID;
    private TreatmentMedicineDtoCreate TREATMENT_MEDICINE_DTO_CREATE_INVALID;

    public TreatmentMedicineEndpointIntegrationTest() {
        super("treatment");
    }

    @BeforeEach
    void initialize() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(webAppContext)
            .apply(springSecurity())
            .build();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ow = objectMapper.writer().withDefaultPrettyPrinter();
        dtoValidator = new DtoValidationUtils(validator);

        MedicationDto MEDICATION1 = medicationService.getAllMedications().get(1);
        Date TREATMENT_MEDICATION_DATE1 = treatmentTestUtils.createDate(2022, Calendar.JANUARY, 1, 10, 10);

        TREATMENT_MEDICINE_DTO_CREATE_VALID = new TreatmentMedicineDtoCreate(
            MEDICATION1,
            "mg",
            100,
            TREATMENT_MEDICATION_DATE1
        );

        TREATMENT_MEDICINE_DTO_CREATE_INVALID = new TreatmentMedicineDtoCreate(
            null,
            "l",
            200,
            TREATMENT_MEDICATION_DATE1
        );
    }

    // *** createTreatmentMedicine: valid create ***

    @Test
    @DisplayName("createTreatmentMedicine: valid TreatmentMedicineDtoCreate - expect success")
    @WithMockUser(username = "doctor1@email.com", authorities = {"DOCTOR"})
    @Transactional
    void testCreateTreatmentMedicine_givenValidTreatmentMedicineDtoCreate_expectCreated() throws Exception {
        dtoValidator.validate(TREATMENT_MEDICINE_DTO_CREATE_VALID, "treatmentMedicineDtoCreate");
        String json = ow.writeValueAsString(TREATMENT_MEDICINE_DTO_CREATE_VALID);
        MvcResult result = mockMvc.perform(post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(responseContent);
        long validId = rootNode.path("id").asLong();

        TreatmentMedicine treatmentMedicine = treatmentMedicineRepository.findById(validId).orElseThrow();
        assertNotNull(treatmentMedicine);
        assertAll("Verify treatment medicine properties",
            () -> assertEquals(TREATMENT_MEDICINE_DTO_CREATE_VALID.medication().id(), treatmentMedicine.getMedicine().getId()),
            () -> assertEquals(TREATMENT_MEDICINE_DTO_CREATE_VALID.unitOfMeasurement(), treatmentMedicine.getUnitOfMeasurement()),
            () -> assertEquals(TREATMENT_MEDICINE_DTO_CREATE_VALID.amount(), treatmentMedicine.getAmount()),
            () -> assertEquals(TREATMENT_MEDICINE_DTO_CREATE_VALID.medicineAdministrationDate(), treatmentMedicine.getTimeOfAdministration())
        );
    }

    // *** createTreatmentMedicine: invalid create ***
    @Test
    @DisplayName("createTreatmentMedicine: invalid TreatmentMedicineDtoCreate - expect validation error")
    @WithMockUser(username = "doctor1@email.com", authorities = {"DOCTOR"})
    @Transactional
    void testCreateTreatmentMedicine_givenInvalidTreatmentMedicineDtoCreate_expectValidationError() throws Exception {
        Assertions.assertThrows(DtoValidationException.class,
            () -> dtoValidator.validate(TREATMENT_MEDICINE_DTO_CREATE_INVALID, "treatmentMedicineDtoCreate"));
        tryToCreateTreatmentMedicineExpectGivenStatus(TREATMENT_MEDICINE_DTO_CREATE_INVALID, status().isUnprocessableEntity());
    }

    // *** createTreatmentMedicine: check authorization ***

    @Test
    @DisplayName("createTreatmentMedicine: no authorization for secretary - expect forbidden")
    @WithMockUser(username = "secretary1@email.com", authorities = {"SECRETARY"})
    @Transactional
    void testCreateTreatmentMedicine_givenNoAuthorizationForSecretary_expectForbidden() throws Exception {
        tryToCreateTreatmentMedicineExpectGivenStatus(TREATMENT_MEDICINE_DTO_CREATE_VALID, status().isForbidden());
    }

    @Test
    @DisplayName("createTreatmentMedicine: no authorization for admin - expect forbidden")
    @WithMockUser(username = "admin@email.com", authorities = {"ADMIN"})
    @Transactional
    void testCreateTreatmentMedicine_givenNoAuthorizationForAdmin_expectForbidden() throws Exception {
        tryToCreateTreatmentMedicineExpectGivenStatus(TREATMENT_MEDICINE_DTO_CREATE_VALID, status().isForbidden());
    }

    @Test
    @DisplayName("createTreatmentMedicine: no authorization for patient - expect forbidden")
    @WithMockUser(username = "chris.anger@email.com", authorities = {"PATIENT"})
    @Transactional
    void testCreateTreatmentMedicine_givenNoAuthorizationForPatient_expectForbidden() throws Exception {
        tryToCreateTreatmentMedicineExpectGivenStatus(TREATMENT_MEDICINE_DTO_CREATE_VALID, status().isForbidden());
    }

    /**
     * Try to create a treatment medicine with the given TreatmentMedicineDtoCreate and expect the given status.
     *
     * @param TREATMENT_MEDICINE_DTO_CREATE_VALID the treatment medicine to create
     * @param expectedResponseStatus              the expected status
     * @throws Exception if the request fails
     */
    private void tryToCreateTreatmentMedicineExpectGivenStatus(TreatmentMedicineDtoCreate TREATMENT_MEDICINE_DTO_CREATE_VALID, ResultMatcher expectedResponseStatus) throws Exception {
        String json = ow.writeValueAsString(TREATMENT_MEDICINE_DTO_CREATE_VALID);
        mockMvc.perform(post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(expectedResponseStatus);
    }


    // *** deleteTreatmentMedicine: valid delete ***

    @Test
    @DisplayName("deleteTreatmentMedicine: valid id - expect success")
    @WithMockUser(username = "doctor1@email.com", authorities = {"DOCTOR"})
    @Transactional
    void testDeleteTreatmentMedicine_givenValidId_expectSuccessfulDeletion() throws Exception {
        TreatmentMedicineDto treatmentMedicineDto = treatmentMedicineService.createTreatmentMedicine(TREATMENT_MEDICINE_DTO_CREATE_VALID);
        mockMvc.perform(delete(BASE_PATH + "/" + treatmentMedicineDto.id())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        assertFalse(treatmentMedicineRepository.existsById(treatmentMedicineDto.id()));
    }

    // *** deleteTreatmentMedicine: invalid delete ***

    @Test
    @DisplayName("deleteTreatmentMedicine: invalid id - expect not found")
    @WithMockUser(username = "doctor1@email.com", authorities = {"DOCTOR"})
    @Transactional
    void testDeleteTreatmentMedicine_givenInvalidId_expectNotFound() throws Exception {
        tryToDeleteTreatmentMedicineExpectGivenStatus(999, status().isNotFound());
    }

    // *** deleteTreatmentMedicine: check Authorization ***

    @Test
    @DisplayName("deleteTreatmentMedicine: no authorization - expect forbidden")
    @WithMockUser(username = "patient1@email.com", authorities = {"PATIENT"})
    @Transactional
    void testDeleteTreatmentMedicine_givenNoAuthorization_expectForbidden() throws Exception {
        tryToDeleteTreatmentMedicineExpectGivenStatus(1, status().isForbidden());
    }

    @Test
    @DisplayName("deleteTreatmentMedicine: no authorization for secretary - expect forbidden")
    @WithMockUser(username = "secretary1@email.com", authorities = {"SECRETARY"})
    @Transactional
    void testDeleteTreatmentMedicine_givenNoAuthorizationForSecretary_expectForbidden() throws Exception {
        tryToDeleteTreatmentMedicineExpectGivenStatus(1, status().isForbidden());
    }

    @Test
    @DisplayName("deleteTreatmentMedicine: no authorization for admin - expect forbidden")
    @WithMockUser(username = "admin@email.com", authorities = {"ADMIN"})
    @Transactional
    void testDeleteTreatmentMedicine_givenNoAuthorizationForAdmin_expectForbidden() throws Exception {
        tryToDeleteTreatmentMedicineExpectGivenStatus(1, status().isForbidden());
    }

    /**
     * Try to delete a treatment medicine and expect the given status.
     *
     * @throws Exception if the request fails
     */
    private void tryToDeleteTreatmentMedicineExpectGivenStatus(long id, ResultMatcher responseStatus) throws Exception {
        mockMvc.perform(delete(BASE_PATH + "/" + id)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(responseStatus);
    }
}


