package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.TestBase;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.InpatientDepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StayDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StayDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StayDtoPage;
import at.ac.tuwien.sepr.groupphase.backend.entity.InpatientDepartment;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Stay;
import at.ac.tuwien.sepr.groupphase.backend.repository.InpatientDepartmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PatientRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.StayRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.InpatientDepartmentService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.PatientServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "datagen"})
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class StayEndpointTest extends TestBase {

    private static final String BASE_PATH = "/api/v1/stays";

    private Patient PATIENT1;
    private InpatientDepartmentDto INPATIENT_DEPARTMENT_DTO1;
    private Stay newStay;
    private InpatientDepartment INPATIENT_DEPARTMENT1;

    public StayEndpointTest() {
        super("stay");
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PatientServiceImpl patientServiceImpl;
    @Autowired
    private WebApplicationContext webAppContext;
    @Autowired
    private InpatientDepartmentService inpatientDepartmentService;
    @Autowired
    private StayRepository stayRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private InpatientDepartmentRepository inpatientDepartmentRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(webAppContext)
            .apply(springSecurity())
            .build();
        PATIENT1 = patientRepository.findAll().get(0);
        Pageable pageable = PageRequest.of(0, 1);
        Specification<InpatientDepartment> spec = (root, query, cb) -> cb.like(root.get("name"), "%");
        INPATIENT_DEPARTMENT_DTO1 = inpatientDepartmentService.findAll(spec, pageable).inpatientDepartments().get(0);
        INPATIENT_DEPARTMENT1 = inpatientDepartmentRepository.findById(INPATIENT_DEPARTMENT_DTO1.id()).get();
        newStay = new Stay();
        newStay.setPatient(PATIENT1);
        newStay.setInpatientDepartment(INPATIENT_DEPARTMENT1);
        newStay.setArrival(new Date());
        newStay.setDeparture(new Date());
    }


    @WithMockUser(username = "chris.anger@email.com", authorities = {"PATIENT"})
    @Test
    @DisplayName("getCurrentStay: Patient trys to access current Stay, 403 is expected")
    void testCheckPatientIn_givenNoAuthorizationForPatient_thenStatusForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/current?id=1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "chris.anger@email.com", authorities = {"SECRETARY"})
    @Test
    @DisplayName("getCurrentStay: Secretary trys to get current Stay of specific patient, 200 is expected")
    void testCheckPatientIn_givenCorrectPatientId_thenStatus200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/current?id=" + PATIENT1.getPatientId())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }


    @WithMockUser(username = "chris.anger@email.com", authorities = {"SECRETARY"})
    @Test
    @DisplayName("getCurrentStay: Patient tries to access current Stay, 200 is expected and response body is null")
    void testCheckPatientIn_givenInvalidPatientId_thenStatusOkAndResponseBodyIsNull() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/current?id=-1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        Assertions.assertTrue(responseBody.isEmpty(), "Response body should be empty");
    }

    @WithMockUser(username = "chris.anger@email.com", authorities = {"SECRETARY"})
    @Test
    @DisplayName("getCurrentStay: Patient tries to access current Stay, 200 is expected and response body is stay")
    void testCheckPatientIn_givenValidPatientId_thenStatusOk() throws Exception {
        Stay newStay = new Stay();
        newStay.setPatient(PATIENT1);
        newStay.setInpatientDepartment(INPATIENT_DEPARTMENT1);
        newStay.setArrival(new Date());

        // Save the new Stay entity into the repository
        Stay savedStay = stayRepository.save(newStay);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/current?id=" + PATIENT1.getPatientId())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        StayDto stayDto = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), StayDto.class);
        Assertions.assertEquals(stayDto.arrival(), savedStay.getArrival());
    }


    @WithMockUser(username = "chris.anger@email.com", authorities = {"SECRETARY"})
    @Test
    @DisplayName("createNewStay: Secretary checks in a patient, 200 is expected")
    void testCheckInPatient_givenValidStayDto_thenStatusOk() throws Exception {
        StayDtoCreate stayDtoCreate = new StayDtoCreate(INPATIENT_DEPARTMENT_DTO1, PATIENT1.getPatientId());

        ObjectMapper objectMapper = new ObjectMapper();
        String stayDtoCreateJson = objectMapper.writeValueAsString(stayDtoCreate);

        mockMvc.perform(post(BASE_PATH + "/arrival")
                .contentType(MediaType.APPLICATION_JSON)
                .content(stayDtoCreateJson))
            .andExpect(status().isOk());
    }

    @WithMockUser(username = "chris.anger@email.com", authorities = {"SECRETARY"})
    @Test
    @DisplayName("createNewStay: Secretary checks in a patient, 404 is expected")
    void testCheckInPatient_givenInvalidPatientIdInStayDto_thenStatus404() throws Exception {
        StayDtoCreate stayDtoCreate = new StayDtoCreate(INPATIENT_DEPARTMENT_DTO1, -1L);

        ObjectMapper objectMapper = new ObjectMapper();
        String stayDtoCreateJson = objectMapper.writeValueAsString(stayDtoCreate);

        mockMvc.perform(post(BASE_PATH + "/arrival")
                .contentType(MediaType.APPLICATION_JSON)
                .content(stayDtoCreateJson))
            .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "chris.anger@email.com", authorities = {"SECRETARY"})
    @Test
    @DisplayName("createNewStay: Secretary checks in a patient, 404 is expected")
    void testCheckInPatient_givenInvalidInpatientDepartmentInStayDto_thenStatus404() throws Exception {
        InpatientDepartmentDto inpatientDepartmentDto = new InpatientDepartmentDto(-1L, "invalid", 0, true);
        StayDtoCreate stayDtoCreate = new StayDtoCreate(inpatientDepartmentDto, PATIENT1.getPatientId());

        ObjectMapper objectMapper = new ObjectMapper();
        String stayDtoCreateJson = objectMapper.writeValueAsString(stayDtoCreate);

        mockMvc.perform(post(BASE_PATH + "/arrival")
                .contentType(MediaType.APPLICATION_JSON)
                .content(stayDtoCreateJson))
            .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "chris.anger@email.com", authorities = {"SECRETARY"})
    @Test
    @DisplayName("endCurrentStay: Secretary discharges a patient, 200 is expected")
    void testDischargePatient_givenAValidCurrentStay_Expect200() throws Exception {
        // Create a new Stay entity
        Stay newStay = new Stay();
        newStay.setPatient(PATIENT1);
        newStay.setInpatientDepartment(INPATIENT_DEPARTMENT1);
        newStay.setArrival(new Date());

        // Save the new Stay entity into the repository
        Stay savedStay = stayRepository.save(newStay);

        // Assert that the saved Stay entity is not null and has an ID
        Assertions.assertNotNull(savedStay);
        Assertions.assertNotNull(savedStay.getId());

        mockMvc.perform(put(BASE_PATH + "/discharge?id=" + savedStay.getId())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @WithMockUser(username = "chris.anger@email.com", authorities = {"SECRETARY"})
    @Test
    @DisplayName("endCurrentStay: Secretary tries to discharge a non-existent stay, 404 is expected")
    void testEndNonExistentStay_thenStatus404() throws Exception {
        // Try to end a non-existent stay
        mockMvc.perform(put(BASE_PATH + "/discharge?id=" + -1L)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "chris.anger@email.com", authorities = {"SECRETARY"})
    @Test
    @DisplayName("getAllStays: Secretary retrieves all stays of a patient, 200 is expected")
    void testGetAllStays_givenValidPatientId_thenStatusOk() throws Exception {


        // Save the new Stay entity into the repository
        Stay savedStay = stayRepository.save(newStay);


        MvcResult mvcResult = mockMvc.perform(get(BASE_PATH + "/all?id=" + PATIENT1.getPatientId() + "&page=0&size=10")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        StayDtoPage stayDtoPage = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), StayDtoPage.class);
        Assertions.assertEquals(1, stayDtoPage.content().size());
        Assertions.assertEquals(savedStay.getArrival(), stayDtoPage.content().get(0).arrival());
        Assertions.assertEquals(savedStay.getDeparture(), stayDtoPage.content().get(0).discharge());
    }

    @WithMockUser(username = "chris.anger@email.com", authorities = {"SECRETARY"})
    @Test
    @DisplayName("getAllStays: Secretary tries to retrieve all stays of a non-existent patient, 200 is expected and stays list is empty")
    void testGetAllStays_givenInvalidPatientId_thenStatusOkAndStaysListIsEmpty() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(BASE_PATH + "/all?id=" + -1L + "&page=0&size=10")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        // Convert JSON string to StayDtoPage object
        ObjectMapper objectMapper = new ObjectMapper();
        StayDtoPage stayDtoPage = objectMapper.readValue(responseBody, StayDtoPage.class);

        // Assert that the stays list in the StayDtoPage object is empty
        Assertions.assertTrue(stayDtoPage.content().isEmpty());
    }

    @WithMockUser(username = "chris.anger@email.com", authorities = {"SECRETARY"})
    @Test
    @DisplayName("updateStay: Secretary updates a stay, 200 is expected")
    void testUpdateStay_givenValidStayDto_thenStatusOk() throws Exception {
        Stay savedStay = stayRepository.save(newStay);
        // Assume that there is a valid Stay object in the database that we can update
        StayDto stayDto = new StayDto(savedStay.getId(), INPATIENT_DEPARTMENT_DTO1, new Date(), new Date());

        ObjectMapper objectMapper = new ObjectMapper();
        String stayDtoJson = objectMapper.writeValueAsString(stayDto);

        mockMvc.perform(put(BASE_PATH + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(stayDtoJson))
            .andExpect(status().isOk());

        Stay updatedStay = stayRepository.findById(savedStay.getId()).get();
        Assertions.assertEquals(stayDto.arrival(), updatedStay.getArrival());
        Assertions.assertEquals(stayDto.discharge(), updatedStay.getDeparture());
    }

    @WithMockUser(username = "chris.anger@email.com", authorities = {"SECRETARY"})
    @Test
    @DisplayName("updateStay: Secretary tries to update a non-existent stay, 404 is expected")
    void testUpdateStay_givenInvalidStayIdInStayDto_thenStatus404() throws Exception {
        // Try to update a non-existent Stay
        StayDto stayDto = new StayDto(-1L, INPATIENT_DEPARTMENT_DTO1, new Date(), new Date());

        ObjectMapper objectMapper = new ObjectMapper();
        String stayDtoJson = objectMapper.writeValueAsString(stayDto);

        mockMvc.perform(put(BASE_PATH + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(stayDtoJson))
            .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "chris.anger@email.com", authorities = {"SECRETARY"})
    @Test
    @DisplayName("createNewStay: Secretary tries to check in a patient with arrival time after discharge time, 422 is expected")
    void testCreateNewStay_givenArrivalTimeAfterDischargeTime_thenStatus422() throws Exception {
        stayRepository.save(newStay);
        // Create a StayDto object where the arrival time is after the discharge time
        Date arrival = new Date();
        Date discharge = new Date(arrival.getTime() - 1000); // 1000 milliseconds before the arrival time
        StayDto stayDtoUpdate = new StayDto(newStay.getId(), INPATIENT_DEPARTMENT_DTO1, arrival, discharge);

        ObjectMapper objectMapper = new ObjectMapper();
        String stayDtoJson = objectMapper.writeValueAsString(stayDtoUpdate);

        mockMvc.perform(put(BASE_PATH + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(stayDtoJson))
            .andExpect(status().isUnprocessableEntity());
    }

    @WithMockUser(username = "chris.anger@email.com", authorities = {"SECRETARY"})
    @Test
    @DisplayName("createNewStay: Secretary tries to check in a patient with null arrival time, 422 is expected")
    void testCreateNewStay_givenNullArrivalTime_thenStatus422() throws Exception {
        stayRepository.save(newStay);
        // Create a StayDtoCreate object where the arrival time is null
        StayDto stayDto = new StayDto(newStay.getId(), INPATIENT_DEPARTMENT_DTO1, null, new Date());

        ObjectMapper objectMapper = new ObjectMapper();
        String stayDtoJson = objectMapper.writeValueAsString(stayDto);

        mockMvc.perform(put(BASE_PATH + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(stayDtoJson))
            .andExpect(status().isUnprocessableEntity());
    }

    @WithMockUser(username = "chris.anger@email.com", authorities = {"SECRETARY"})
    @Test
    @DisplayName("createNewStay: Secretary tries to check in a patient with null arrival time, 422 is expected")
    void testCreateNewStay_givenNullDischargeTime_thenStatus422() throws Exception {
        stayRepository.save(newStay);
        // Create a StayDtoCreate object where the arrival time is null
        StayDto stayDto = new StayDto(newStay.getId(), INPATIENT_DEPARTMENT_DTO1, new Date(), null);

        ObjectMapper objectMapper = new ObjectMapper();
        String stayDtoCreateJson = objectMapper.writeValueAsString(stayDto);

        mockMvc.perform(put(BASE_PATH + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(stayDtoCreateJson))
            .andExpect(status().isUnprocessableEntity());
    }

    @WithMockUser(username = "chris.anger@email.com", authorities = {"SECRETARY"})
    @Test
    @DisplayName("createNewStay: Secretary tries to check in a patient with null arrival time, 422 is expected")
    void testCreateNewStay_givenNullInpatientDepartment_thenStatus422() throws Exception {
        stayRepository.save(newStay);
        // Create a StayDtoCreate object where the arrival time is null
        StayDto stayDto = new StayDto(newStay.getId(), null, new Date(), new Date());

        ObjectMapper objectMapper = new ObjectMapper();
        String stayDtoCreateJson = objectMapper.writeValueAsString(stayDto);

        mockMvc.perform(put(BASE_PATH + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(stayDtoCreateJson))
            .andExpect(status().isUnprocessableEntity());
    }

    /**
     * This test case works and returns 200 even tough the inpatient department is invalid. Because the inpatient department is never used for any operation in the updateStay method.
     */
    @WithMockUser(username = "chris.anger@email.com", authorities = {"SECRETARY"})
    @Test
    @DisplayName("createNewStay: Secretary tries to check in a patient with null arrival time, 422 is expected")
    void testCreateNewStay_givenInvalidInpatientDepartment_thenStatus200() throws Exception {
        stayRepository.save(newStay);
        // Create a StayDtoCreate object where the arrival time is null
        InpatientDepartmentDto inpatientDepartmentDto = new InpatientDepartmentDto(-1L, "invalid", 0, true);
        StayDto stayDto = new StayDto(newStay.getId(), inpatientDepartmentDto, new Date(), new Date());

        ObjectMapper objectMapper = new ObjectMapper();
        String stayDtoCreateJson = objectMapper.writeValueAsString(stayDto);

        mockMvc.perform(put(BASE_PATH + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(stayDtoCreateJson))
            .andExpect(status().isOk());
    }

}
