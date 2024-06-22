package at.ac.tuwien.sepr.groupphase.backend.integrationtest.treatment;

import at.ac.tuwien.sepr.groupphase.backend.TestBase;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Treatment;
import at.ac.tuwien.sepr.groupphase.backend.exception.DtoValidationException;
import at.ac.tuwien.sepr.groupphase.backend.integrationtest.treatment.util.TreatmentTestUtils;
import at.ac.tuwien.sepr.groupphase.backend.repository.TreatmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.DoctorServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.MedicationServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.OutpatientDepartmentServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.PatientServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.util.DtoValidationUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;
import org.springframework.web.context.WebApplicationContext;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "datagen"})
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class TreatmentEndpointIntegrationTest extends TestBase {

    private static final String BASE_PATH = "/api/v1/treatments";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext webAppContext;
    @Autowired
    private Validator validator;
    @Autowired
    private PatientServiceImpl patientServiceImpl;
    @Autowired
    private DoctorServiceImpl doctorService;
    @Autowired
    private OutpatientDepartmentServiceImpl outpatientDepartmentService;
    @Autowired
    private MedicationServiceImpl medicationService;
    @Autowired
    private TreatmentRepository treatmentRepository;
    @Autowired
    private TreatmentTestUtils treatmentTestUtils;

    private DtoValidationUtils dtoValidator;
    private ObjectWriter ow;

    private PatientDtoSparse PATIENT1;
    private PatientDtoSparse PATIENT2;
    private DoctorDtoSparse DOCTOR1;
    private DoctorDtoSparse DOCTOR2;
    private OutpatientDepartmentDto OUTPATIENT_DEPARTMENT1;
    private OutpatientDepartmentDto OUTPATIENT_DEPARTMENT2;
    private MedicationDto MEDICATION1;
    private MedicationDto MEDICATION2;
    private Date TREATMENT_MEDICATION_DATE1;
    private Date TREATMENT_MEDICATION_DATE2;
    private TreatmentMedicineDto TREATMENT_MEDICINE1_DTO;
    private TreatmentMedicineDto TREATMENT_MEDICINE2_DTO;

    private Date TREATMENT_START_DATE1;
    private Date TREATMENT_START_DATE2;
    private Date TREATMENT_END_DATE1;
    private Date TREATMENT_END_DATE2;
    private String TREATMENT_TEXT1;
    private String TREATMENT_TITLE1;

    private TreatmentDtoCreate TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED;

    public TreatmentEndpointIntegrationTest() {
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

        PATIENT1 = patientServiceImpl.getAllPatients().get(0);
        PATIENT2 = patientServiceImpl.getAllPatients().get(2);
        DOCTOR1 = doctorService.getAllDoctors().get(0);
        DOCTOR2 = doctorService.getAllDoctors().get(2);
        OUTPATIENT_DEPARTMENT1 = outpatientDepartmentService.getAllOutpatientDepartments().get(0);
        OUTPATIENT_DEPARTMENT2 = outpatientDepartmentService.getAllOutpatientDepartments().get(2);
        MEDICATION1 = medicationService.getAllMedications().get(1);
        MEDICATION2 = medicationService.getAllMedications().get(2);

        TREATMENT_MEDICATION_DATE1 = treatmentTestUtils.createDate(2022, Calendar.JANUARY, 1, 10, 10);
        TREATMENT_MEDICATION_DATE2 = treatmentTestUtils.createDate(2022, Calendar.FEBRUARY, 1, 10, 10);
        TREATMENT_MEDICINE1_DTO = treatmentTestUtils.createTreatmentMedicineDto(MEDICATION1, TREATMENT_MEDICATION_DATE1);
        TREATMENT_MEDICINE2_DTO = treatmentTestUtils.createTreatmentMedicineDto(MEDICATION2, TREATMENT_MEDICATION_DATE1);
        TREATMENT_START_DATE1 = treatmentTestUtils.createDate(2022, Calendar.JANUARY, 1, 4, 0);
        TREATMENT_START_DATE2 = treatmentTestUtils.createDate(2022, Calendar.FEBRUARY, 1, 4, 0);
        TREATMENT_END_DATE1 = treatmentTestUtils.createDate(2023, Calendar.JANUARY, 1, 3, 30);
        TREATMENT_END_DATE2 = treatmentTestUtils.createDate(2022, Calendar.FEBRUARY, 4, 4, 30);
        TREATMENT_TEXT1 = "Treatment Text";
        TREATMENT_TITLE1 = "Treatment Title";
    }

    @Test
    @DisplayName("Treatment Endpoint: invalid get request - returns 404 not found")
    @Transactional
    void givenInvalidUrl_whenSendRequest_Returns404NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/invalid")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    // **** Tests for createTreatment ****

    @WithMockUser(username = "doctor1@email.com", authorities = {"DOCTOR"})
    @TestFactory
    @DisplayName("createTreatment: valid TreatmentDtoCreates - check validation and successful request")
    @Transactional
    Stream<DynamicTest> testCreateTreatment_givenValidTreatmentDtoCreate_expectSuccessfulCreate() {
        return provideValidTreatmentDtoCreate()
            .map(arguments -> {
                String testName = (String) arguments.get()[0];
                TreatmentDtoCreate treatmentDtoCreate = (TreatmentDtoCreate) arguments.get()[1];
                TreatmentDto expectedTreatmentDto = (TreatmentDto) arguments.get()[2];

                return DynamicTest.dynamicTest(testName, () -> {

                    dtoValidator.validate(treatmentDtoCreate, "treatmentDtoCreate");
                    String json = ow.writeValueAsString(treatmentDtoCreate);
                    MvcResult result = mockMvc.perform(post(BASE_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.treatmentTitle").value(expectedTreatmentDto.treatmentTitle()))
                        .andExpect(jsonPath("$.outpatientDepartment.id").value(expectedTreatmentDto.outpatientDepartment().id()))
                        .andExpect(jsonPath("$.treatmentText").value(expectedTreatmentDto.treatmentText()))
                        .andExpect(jsonPath("$.patient.email").value(expectedTreatmentDto.patient().email()))
                        .andExpect(jsonPath("$.doctors.length()").value(expectedTreatmentDto.doctors().size()))
                        .andExpect(jsonPath("$.medicines.length()").value(expectedTreatmentDto.medicines().size()))
                        .andReturn();

                    String responseJson = result.getResponse().getContentAsString();
                    createOrUpdateTreatmentCheckAllDoctors(expectedTreatmentDto, responseJson);
                    createOrUpdateTreatmentCheckAllTreatmentMedicine(expectedTreatmentDto, responseJson);

                });
            });
    }

    @WithMockUser(username = "doctor1@email.com", authorities = {"DOCTOR"})
    @TestFactory
    @Transactional
    @DisplayName("createTreatment: invalid TreatmentDtoCreates - check validation throws exception and returns 422 unprocessable entity")
    Stream<DynamicTest> testCreateTreatment_givenInvalidTreatmentDtoCreate_throwsValidationException() {
        return provideInvalidTreatmentDtoCreate()
            .map(arguments -> {

                String testName = (String) arguments.get()[0];
                TreatmentDtoCreate treatmentDtoCreate = (TreatmentDtoCreate) arguments.get()[1];

                return DynamicTest.dynamicTest(testName, () -> {
                    Assertions.assertThrows(DtoValidationException.class,
                        () -> dtoValidator.validate(treatmentDtoCreate, "treatmentDtoCreate"));

                    String json = ow.writeValueAsString(treatmentDtoCreate);
                    mockMvc.perform(post(BASE_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isUnprocessableEntity());
                });
            });
    }

    // **** Tests for updateTreatment ****

    @WithMockUser(username = "doctor1@email.com", authorities = {"DOCTOR"})
    @Transactional
    @DisplayName("updateTreatment: valid update request - expect successful update")
    @Test
    void testUpdateExistingTreatment_givenValidDto_expectSuccessfulUpdate() throws Exception {
        TreatmentMedicineDto treatmentMedicineDto = treatmentTestUtils.createTreatmentMedicineDto(MEDICATION2, TREATMENT_MEDICATION_DATE2);
        Long id = treatmentRepository.findAll().get(2).getId();
        TreatmentDtoCreate updateDto =
            new TreatmentDtoCreate("Updated Title", TREATMENT_START_DATE2, TREATMENT_END_DATE2, PATIENT2, OUTPATIENT_DEPARTMENT2, "Updated Treatment Text",
                List.of(DOCTOR2), List.of(treatmentMedicineDto));
        TreatmentDto updatedTreatment =
            new TreatmentDto(id, "Updated Title", TREATMENT_START_DATE2, TREATMENT_END_DATE2, PATIENT2, OUTPATIENT_DEPARTMENT2, "Updated Treatment Text",
                List.of(DOCTOR2), List.of(treatmentMedicineDto));

        String json = ow.writeValueAsString(updateDto);
        MvcResult result = mockMvc.perform(put(BASE_PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(updatedTreatment.id()))
            .andExpect(jsonPath("$.treatmentTitle").value(updatedTreatment.treatmentTitle()))
            .andExpect(jsonPath("$.treatmentText").value(updatedTreatment.treatmentText()))
            .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        createOrUpdateTreatmentCheckAllDoctors(updatedTreatment, responseJson);
        createOrUpdateTreatmentCheckAllTreatmentMedicine(updatedTreatment, responseJson);
    }

    @WithMockUser(username = "doctor1@email.com", authorities = {"DOCTOR"})
    @Transactional
    @DisplayName("updateTreatment: valid update request - expect successful update")
    @Test
    void testCreateTreatmentThenUpdateTreatment_givenValidDto_expectSuccessfulUpdate() throws Exception {

        TreatmentDtoCreate validTreatmentDtoCreate = new TreatmentDtoCreate(
            TREATMENT_TITLE1,
            TREATMENT_START_DATE1,
            TREATMENT_END_DATE1,
            PATIENT1,
            OUTPATIENT_DEPARTMENT1,
            TREATMENT_TEXT1,
            List.of(DOCTOR1),
            List.of(treatmentTestUtils.createTreatmentMedicineDto(MEDICATION1, TREATMENT_MEDICATION_DATE1)));

        dtoValidator.validate(validTreatmentDtoCreate, "treatmentDtoCreate");
        String json = ow.writeValueAsString(validTreatmentDtoCreate);
        MvcResult result = mockMvc.perform(post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();

        // retrieve the id of the created treatment
        String responseContent = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(responseContent);
        int validId = rootNode.path("id").asInt();

        TreatmentMedicineDto treatmentMedicineDto = treatmentTestUtils.createTreatmentMedicineDto(MEDICATION2, TREATMENT_MEDICATION_DATE2);
        TreatmentDtoCreate updateDto = new TreatmentDtoCreate(
            "Updated Title",
            TREATMENT_START_DATE2,
            TREATMENT_END_DATE2,
            PATIENT2,
            OUTPATIENT_DEPARTMENT2,
            "Updated Treatment Text",
            List.of(DOCTOR2),
            List.of(treatmentMedicineDto)
        );

        TreatmentDto updatedTreatment = new TreatmentDto(
            validId,
            "Updated Title",
            TREATMENT_START_DATE2,
            TREATMENT_END_DATE2,
            PATIENT2,
            OUTPATIENT_DEPARTMENT2,
            "Updated Treatment Text",
            List.of(DOCTOR2),
            List.of(treatmentMedicineDto)
        );
        json = ow.writeValueAsString(updateDto);
        MvcResult resultUpdate = mockMvc.perform(put(BASE_PATH + "/" + validId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(updatedTreatment.id()))
            .andExpect(jsonPath("$.treatmentTitle").value(updatedTreatment.treatmentTitle()))
            .andExpect(jsonPath("$.treatmentText").value(updatedTreatment.treatmentText()))
            .andExpect(jsonPath("$.patient.email").value(updatedTreatment.patient().email()))
            .andExpect(jsonPath("$.doctors.length()").value(updatedTreatment.doctors().size()))
            .andExpect(jsonPath("$.medicines.length()").value(updatedTreatment.medicines().size()))
            .andReturn();

        String responseJson = resultUpdate.getResponse().getContentAsString();
        createOrUpdateTreatmentCheckAllDoctors(updatedTreatment, responseJson);
        createOrUpdateTreatmentCheckAllTreatmentMedicine(updatedTreatment, responseJson);
    }

    // **** Tests for getTreatmentById ****
    @WithMockUser(username = "doctor1@email.com", authorities = {"DOCTOR"})
    @Transactional
    @DisplayName("getTreatmentById: valid get request - expect treatment data")
    @Test
    void testGetTreatmentById_givenValidId_expectTreatmentData() throws Exception {
        // create a valid treatment to get
        TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED =
            new TreatmentDtoCreate(TREATMENT_TITLE1, TREATMENT_START_DATE1, TREATMENT_END_DATE1, PATIENT1, OUTPATIENT_DEPARTMENT1, TREATMENT_TEXT1,
                List.of(DOCTOR1), List.of(TREATMENT_MEDICINE1_DTO));
        dtoValidator.validate(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED, "treatmentDtoCreate");
        String json = ow.writeValueAsString(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED);
        MvcResult result = mockMvc.perform(post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();

        // retrieve the id of the created treatment
        String responseContent = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(responseContent);
        int validId = rootNode.path("id").asInt();

        // test the get request
        mockMvc.perform(get(BASE_PATH + "/" + validId)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.treatmentTitle").value(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED.treatmentTitle()))
            .andExpect(jsonPath("$.outpatientDepartment.id").value(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED.outpatientDepartment().id()))
            .andExpect(jsonPath("$.treatmentText").value(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED.treatmentText()))
            .andExpect(jsonPath("$.patient.email").value(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED.patient().email()))
            .andExpect(jsonPath("$.doctors.length()").value(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED.doctors().size()))
            .andExpect(jsonPath("$.medicines.length()").value(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED.medicines().size()));
    }

    // **** Authorization Tests for getTreatmentById() ****

    @WithMockUser(username = "doctor@example.com", authorities = {"DOCTOR"})
    @Transactional
    @DisplayName("getTreatmentById: treatment not found - expect 404 Not Found")
    @Test
    void testGetTreatmentById_givenInvalidId_expectNotFound() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/999")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "secretary1@email.com", authorities = {"SECRETARY"})
    @Transactional
    @DisplayName("getTreatmentById: authorized access (secretary) - expect Status OK")
    @Test
    void testGetTreatmentById_givenAuthorizedSecretary_expectStatusOk() throws Exception {
        tryGetTreatmentById_expectGivenStatus(treatmentRepository.findAll(), status().isOk());
    }

    @WithMockUser(username = "doctor1@email.com", authorities = {"DOCTOR"})
    @Transactional
    @DisplayName("getTreatmentById: authorized access (doctor) - expect Status OK")
    @Test
    void testGetTreatmentById_givenAuthorizedDoctor_expectStatusOk() throws Exception {
        tryGetTreatmentById_expectGivenStatus(treatmentRepository.findAll(), status().isOk());
    }

    @WithMockUser(username = "chris.anger@email.com", authorities = {"PATIENT"})
    @Transactional
    @DisplayName("getTreatmentById: authorized access (patient) - expect Status OK")
    @Test
    void testGetTreatmentById_givenAuthorizedPatient_expectStatusOk() throws Exception {
        tryGetTreatmentById_expectGivenStatus(treatmentRepository.findByPatient_PatientId(PATIENT1.id()), status().isOk());
    }

    @WithMockUser(username = "invalid@invalid.com", authorities = {"PATIENT"})
    @Transactional
    @DisplayName("getTreatmentById: unauthorized access - expect 403 Forbidden")
    @Test
    void testGetTreatmentById_givenUnauthorizedPatient_expectForbidden() throws Exception {
        tryGetTreatmentById_expectGivenStatus(treatmentRepository.findAll(), status().isForbidden());
    }

    // **** Authorization Tests for updateTreatment() ****

    @WithMockUser(username = "doctor1@email.com", authorities = {"DOCTOR"})
    @Transactional
    @DisplayName("updateTreatment: valid update request - expect status isOk")
    @Test
    void testUpdateTreatment_givenValidDto_givenValidAuthorization_expectStatusOk() throws Exception {
        tryUpdateTreatmentExpectGivenStatus(status().isOk());
    }

    @WithMockUser(username = "patient1@email.com", authorities = {"PATIENT"})
    @Transactional
    @DisplayName("updateTreatment: unauthorized access (wrong Patient)- expect 403 Forbidden")
    @Test
    void testUpdateTreatment_givenUnauthorizedUser_expectForbidden() throws Exception {
        tryUpdateTreatmentExpectGivenStatus(status().isForbidden());
    }

    @WithMockUser(username = "secretary1@email.com", authorities = {"SECRETARY"})
    @Transactional
    @DisplayName("updateTreatment: unauthorized access (Secretary) - expect 403 Forbidden")
    @Test
    void testUpdateTreatment_givenUnauthorizedSecretary_expectForbidden() throws Exception {
        tryUpdateTreatmentExpectGivenStatus(status().isForbidden());
    }

    @WithMockUser(username = "admin@email.com", authorities = {"ADMIN"})
    @Transactional
    @DisplayName("updateTreatment: unauthorized access (Admin) - expect 403 Forbidden")
    @Test
    void testUpdateTreatment_givenUnauthorizedAdmin_expectForbidden() throws Exception {
        tryUpdateTreatmentExpectGivenStatus(status().isForbidden());
    }

    // **** Authorization Tests for createTreatment() ****

    @WithMockUser(username = "doctor1@email.com", authorities = {"DOCTOR"})
    @Transactional
    @DisplayName("updateTreatment: valid create request - expect status isCreated")
    @Test
    void testCreateTreatment_givenValidDto_givenValidAuthorization_expectStatusOk() throws Exception {
        tryCreateTreatmentExpectGivenStatus(status().isCreated());
    }

    @WithMockUser(username = "patient1@email.com", authorities = {"PATIENT"})
    @Transactional
    @DisplayName("updateTreatment: unauthorized access - expect 403 Forbidden")
    @Test
    void testCreateTreatment_givenUnauthorizedUser_expectForbidden() throws Exception {
        tryCreateTreatmentExpectGivenStatus(status().isForbidden());
    }

    @WithMockUser(username = "secretary1@email.com", authorities = {"SECRETARY"})
    @Transactional
    @DisplayName("updateTreatment: unauthorized access (Secretary) - expect 403 Forbidden")
    @Test
    void testCreateTreatment_givenUnauthorizedSecretary_expectForbidden() throws Exception {
        tryCreateTreatmentExpectGivenStatus(status().isForbidden());
    }

    @WithMockUser(username = "admin@email.com", authorities = {"ADMIN"})
    @Transactional
    @DisplayName("updateTreatment: unauthorized access (Secretary) - expect 403 Forbidden")
    @Test
    void testCreateTreatment_givenUnauthorizedAdmin_expectForbidden() throws Exception {
        tryCreateTreatmentExpectGivenStatus(status().isForbidden());
    }

    /**
     * Tries to get a treatment by id and expects a given status - used to check Authorization
     *
     * @param treatments the treatments
     * @param status     the expected status
     * @throws Exception if the request fails
     */
    private void tryGetTreatmentById_expectGivenStatus(List<Treatment> treatments, ResultMatcher status) throws Exception {
        long id = treatments.get(0).getId();
        mockMvc.perform(get(BASE_PATH + "/" + id)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status);
    }

    /**
     * Tries to update a treatment and expects a given status - used to check Authorization
     *
     * @param expectedStatus the expected status
     * @throws Exception if the request fails
     */
    private void tryUpdateTreatmentExpectGivenStatus(ResultMatcher expectedStatus) throws Exception {
        TreatmentDtoCreate updatedTreatmentDtoCreate =
            new TreatmentDtoCreate("Updated Title", TREATMENT_START_DATE1, TREATMENT_END_DATE1, PATIENT1, OUTPATIENT_DEPARTMENT1, TREATMENT_TEXT1,
                List.of(DOCTOR1), List.of(TREATMENT_MEDICINE1_DTO));
        String json = ow.writeValueAsString(updatedTreatmentDtoCreate);
        mockMvc.perform(put(BASE_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(expectedStatus);
    }

    /**
     * Tries to create a treatment and expects a given status - used to check Authorization
     *
     * @param expectedStatus the expected status
     * @throws Exception if the request fails
     */
    private void tryCreateTreatmentExpectGivenStatus(ResultMatcher expectedStatus) throws Exception {
        TreatmentDtoCreate treatmentDtoCreate =
            new TreatmentDtoCreate("Updated Title", TREATMENT_START_DATE1, TREATMENT_END_DATE1, PATIENT1, OUTPATIENT_DEPARTMENT1, TREATMENT_TEXT1,
                List.of(DOCTOR1), List.of(TREATMENT_MEDICINE1_DTO));
        String json = ow.writeValueAsString(treatmentDtoCreate);
        mockMvc.perform(post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(expectedStatus);
    }

    /**
     * Checks if all doctors in the responseJson are equal to the doctors in the expectedTreatment
     *
     * @param expectedTreatment the expected treatment
     * @param responseJson      the response json
     * @throws Exception if the responseJson is invalid
     */
    private void createOrUpdateTreatmentCheckAllDoctors(TreatmentDto expectedTreatment, String responseJson) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseJson);
        JsonNode doctorsNode = rootNode.path("doctors");

        for (int i = 0; i < expectedTreatment.doctors().size(); i++) {
            assertEquals(expectedTreatment.doctors().get(i).email(), doctorsNode.get(i).path("email").asText());
        }
    }

    /**
     * Checks if all treatment medicines in the responseJson are equal to the treatment medicines in the expectedTreatment
     *
     * @param expectedTreatmentDto the expected treatment
     * @param responseJson         the response json
     * @throws Exception if the responseJson is invalid
     */
    private void createOrUpdateTreatmentCheckAllTreatmentMedicine(TreatmentDto expectedTreatmentDto, String responseJson) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseJson);
        JsonNode medicinesNode = rootNode.path("medicines");

        for (int i = 0; i < expectedTreatmentDto.medicines().size(); i++) {
            assertEquals(expectedTreatmentDto.medicines().get(i).medication().name(), medicinesNode.get(i).path("medication").path("name").asText());
            assertEquals(expectedTreatmentDto.medicines().get(i).amount(), medicinesNode.get(i).path("amount").asLong());
        }
    }

    // **** creation of test-data: valid TreatmentDtoCreate ****

    Stream<Arguments> provideValidTreatmentDtoCreate() {
        TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED = new TreatmentDtoCreate(
            TREATMENT_TITLE1,
            TREATMENT_START_DATE1,
            TREATMENT_END_DATE1,
            PATIENT1,
            OUTPATIENT_DEPARTMENT1,
            TREATMENT_TEXT1,
            List.of(DOCTOR1),
            List.of(treatmentTestUtils.createTreatmentMedicineDto(MEDICATION1, TREATMENT_MEDICATION_DATE1))
        );

        TreatmentDto TREATMENT_DTO_VALID_ONE_DOC_ONE_MED = new TreatmentDto(
            1L,
            TREATMENT_TITLE1,
            TREATMENT_START_DATE1,
            TREATMENT_END_DATE1,
            PATIENT1,
            OUTPATIENT_DEPARTMENT1,
            TREATMENT_TEXT1,
            List.of(DOCTOR1),
            List.of(treatmentTestUtils.createTreatmentMedicineDto(MEDICATION1, TREATMENT_MEDICATION_DATE1))
        );

        // valid treatment dto 2 doctors 1 med
        TreatmentDtoCreate TREATMENT_DTO_CREATE_VALID_TWO_DOC_ONE_MED = new TreatmentDtoCreate(
            TREATMENT_TITLE1,
            TREATMENT_START_DATE1,
            TREATMENT_END_DATE1,
            PATIENT1,
            OUTPATIENT_DEPARTMENT2,
            TREATMENT_TEXT1,
            List.of(DOCTOR1, DOCTOR2),
            List.of(treatmentTestUtils.createTreatmentMedicineDto(MEDICATION1, TREATMENT_MEDICATION_DATE1))
        );

        TreatmentDto TREATMENT_DTO_VALID_TWO_DOC_ONE_MED = new TreatmentDto(
            2L,
            TREATMENT_TITLE1,
            TREATMENT_START_DATE1,
            TREATMENT_END_DATE1,
            PATIENT1,
            OUTPATIENT_DEPARTMENT2,
            TREATMENT_TEXT1,
            List.of(DOCTOR1, DOCTOR2),
            List.of(treatmentTestUtils.createTreatmentMedicineDto(MEDICATION1, TREATMENT_MEDICATION_DATE1))
        );

        // valid treatment dto 1 doctor 2 meds
        TreatmentDtoCreate TREATMENT_DTO_CREATE_VALID_ONE_DOC_TWO_MED = new TreatmentDtoCreate(
            TREATMENT_TITLE1,
            TREATMENT_START_DATE1,
            TREATMENT_END_DATE1,
            PATIENT1,
            OUTPATIENT_DEPARTMENT1,
            TREATMENT_TEXT1,
            List.of(DOCTOR1),
            List.of(treatmentTestUtils.createTreatmentMedicineDto(MEDICATION1, TREATMENT_MEDICATION_DATE1),
                treatmentTestUtils.createTreatmentMedicineDto(MEDICATION2, TREATMENT_MEDICATION_DATE1))
        );

        TreatmentDto TREATMENT_DTO_VALID_ONE_DOC_TWO_MED = new TreatmentDto(
            3L,
            TREATMENT_TITLE1,
            TREATMENT_START_DATE1,
            TREATMENT_END_DATE1,
            PATIENT1,
            OUTPATIENT_DEPARTMENT1,
            TREATMENT_TEXT1,
            List.of(DOCTOR1),
            List.of(treatmentTestUtils.createTreatmentMedicineDto(MEDICATION1, TREATMENT_MEDICATION_DATE1),
                treatmentTestUtils.createTreatmentMedicineDto(MEDICATION2, TREATMENT_MEDICATION_DATE1))
        );

        // valid treatment dto empty optional fields
        TreatmentDtoCreate TREATMENT_DTO_CREATE_VALID_EMPTY_OPTIONAL = new TreatmentDtoCreate(
            TREATMENT_TITLE1,
            TREATMENT_START_DATE1,
            TREATMENT_END_DATE1,
            PATIENT1,
            OUTPATIENT_DEPARTMENT1,
            "",
            List.of(DOCTOR1),
            List.of()
        );

        TreatmentDto TREATMENT_DTO_VALID_EMPTY_OPTIONAL = new TreatmentDto(
            3L,
            TREATMENT_TITLE1,
            TREATMENT_START_DATE1,
            TREATMENT_END_DATE1,
            PATIENT1,
            OUTPATIENT_DEPARTMENT1,
            "",
            List.of(DOCTOR1),
            List.of()
        );

        return Stream.of(
            Arguments.of("valid TreatmentDtoCreate with one doctor and one treatment medication",
                TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED,
                TREATMENT_DTO_VALID_ONE_DOC_ONE_MED
            ),
            Arguments.of("valid TreatmentDtoCreate with two doctors and one treatment medication",
                TREATMENT_DTO_CREATE_VALID_TWO_DOC_ONE_MED,
                TREATMENT_DTO_VALID_TWO_DOC_ONE_MED
            ),
            Arguments.of("valid TreatmentDtoCreate with one doctor and two treatment medications",
                TREATMENT_DTO_CREATE_VALID_ONE_DOC_TWO_MED,
                TREATMENT_DTO_VALID_ONE_DOC_TWO_MED
            ),
            Arguments.of("valid TreatmentDtoCreate with empty optional fields",
                TREATMENT_DTO_CREATE_VALID_EMPTY_OPTIONAL,
                TREATMENT_DTO_VALID_EMPTY_OPTIONAL
            )
        );
    }

    // **** creation of test-data: invalid TreatmentDtoCreate ****

    Stream<Arguments> provideInvalidTreatmentDtoCreate() {

        TreatmentDtoCreate TREATMENT_DTO_CREATE_INVALID_NO_DOCTOR = new TreatmentDtoCreate(
            TREATMENT_TITLE1,
            TREATMENT_START_DATE1,
            TREATMENT_END_DATE1,
            PATIENT1,
            OUTPATIENT_DEPARTMENT1,
            TREATMENT_TEXT1,
            List.of(),
            List.of(TREATMENT_MEDICINE1_DTO, TREATMENT_MEDICINE2_DTO)
        );

        TreatmentDtoCreate TREATMENT_DTO_CREATE_INVALID_NO_TITLE = new TreatmentDtoCreate(
            "",
            TREATMENT_START_DATE1,
            TREATMENT_END_DATE1,
            PATIENT1,
            OUTPATIENT_DEPARTMENT1,
            TREATMENT_TEXT1,
            List.of(DOCTOR1),
            List.of(TREATMENT_MEDICINE1_DTO, TREATMENT_MEDICINE2_DTO)
        );

        TreatmentDtoCreate TREATMENT_DTO_CREATE_INVALID_NO_START_DATE = new TreatmentDtoCreate(
            TREATMENT_TITLE1,
            null,
            TREATMENT_END_DATE1,
            PATIENT1,
            OUTPATIENT_DEPARTMENT1,
            TREATMENT_TEXT1,
            List.of(DOCTOR1),
            List.of(TREATMENT_MEDICINE1_DTO, TREATMENT_MEDICINE2_DTO)
        );

        TreatmentDtoCreate TREATMENT_DTO_CREATE_INVALID_NO_END_DATE = new TreatmentDtoCreate(
            TREATMENT_TITLE1,
            TREATMENT_START_DATE1,
            null,
            PATIENT1,
            OUTPATIENT_DEPARTMENT1,
            TREATMENT_TEXT1,
            List.of(DOCTOR1),
            List.of(TREATMENT_MEDICINE1_DTO, TREATMENT_MEDICINE2_DTO)
        );

        TreatmentDtoCreate TREATMENT_DTO_CREATE_INVALID_NO_PATIENT = new TreatmentDtoCreate(
            TREATMENT_TITLE1,
            TREATMENT_START_DATE1,
            TREATMENT_END_DATE1,
            null,
            OUTPATIENT_DEPARTMENT1,
            TREATMENT_TEXT1,
            List.of(DOCTOR1),
            List.of(TREATMENT_MEDICINE1_DTO, TREATMENT_MEDICINE2_DTO)
        );

        TreatmentDtoCreate TREATMENT_DTO_CREATE_INVALID_NO_OUTPATIENT_DEPARTMENT = new TreatmentDtoCreate(
            TREATMENT_TITLE1,
            TREATMENT_START_DATE1,
            TREATMENT_END_DATE1,
            PATIENT1,
            null,
            TREATMENT_TEXT1,
            List.of(DOCTOR1),
            List.of(TREATMENT_MEDICINE1_DTO, TREATMENT_MEDICINE2_DTO)
        );

        return Stream.of(
            Arguments.of("invalid TreatmentDtoCreate (missing doctor)",
                TREATMENT_DTO_CREATE_INVALID_NO_DOCTOR
            ),
            Arguments.of("invalid TreatmentDtoCreate (missing title)",
                TREATMENT_DTO_CREATE_INVALID_NO_TITLE
            ),
            Arguments.of("invalid TreatmentDtoCreate (missing start date)",
                TREATMENT_DTO_CREATE_INVALID_NO_START_DATE
            ),
            Arguments.of("invalid TreatmentDtoCreate (missing end date)",
                TREATMENT_DTO_CREATE_INVALID_NO_END_DATE
            ),
            Arguments.of("invalid TreatmentDtoCreate (missing patient)",
                TREATMENT_DTO_CREATE_INVALID_NO_PATIENT
            ),
            Arguments.of("invalid TreatmentDtoCreate (missing outpatient department)",
                TREATMENT_DTO_CREATE_INVALID_NO_OUTPATIENT_DEPARTMENT
            )
        );
    }


}
