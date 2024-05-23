package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PatientMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import at.ac.tuwien.sepr.groupphase.backend.repository.CredentialRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PatientRepository;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PatientEndpointTest {
    @Autowired
    private WebApplicationContext webAppContext;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    ObjectWriter ow;

    private static final String BASE_PATH = "/api/v1/patients";

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    PatientMapper patientMapper;

    @Autowired
    CredentialRepository credentialRepository;

    @BeforeEach
    public void beforeEach() {
        patientRepository.deleteAll();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ow = objectMapper.writer().withDefaultPrettyPrinter();
    }

    @AfterEach
    public void cleanup() {
        patientRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    public void givenInvalidUrl_whenSendRequest_Returns404NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/bla")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    public void givenValidCreatePatientDto_whenCreatePatient_thenReturnCreatedPatient() throws Exception {
        String json = ow.writeValueAsString(new PatientCreateDto("1234123456", "a@a.a", "a", "b"));
        byte[] body = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();

        List<Credential> credentials = patientRepository.findAllPatientCredentials();

        assertNotNull(credentials);
        assertThat(credentials).hasSize(1).extracting(Credential::getEmail, Credential::getFirstName, Credential::getLastName).containsExactly(
            tuple("a@a.a", "a", "b"));
    }

    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    public void givenCreatePatientDtoWithInvalidEmail_whenCreatePatient_thenReturns422UnprocessableEntity() throws Exception {
        String json = ow.writeValueAsString(new PatientCreateDto("1234123456", "a.a.a", "a", "b"));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
        //TODO: fix the status code of the global exception handler (Issue #45)
    }

    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    public void givenCreatePatientDtoWithInvalidSvnr_whenCreatePatient_thenReturns422UnprocessableEntity() throws Exception {
        String json = ow.writeValueAsString(new PatientCreateDto("12341234567", "a@a.a", "a", "b"));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
        //TODO: fix the status code of the global exception handler (Issue #45)
    }

    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    public void givenCreatePatientDtoWithInvalidFirstname_whenCreatePatient_thenReturns422UnprocessableEntity() throws Exception {
        String json = ow.writeValueAsString(new PatientCreateDto("1234123456", "a@a.a", "", "b"));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
        //TODO: fix the status code of the global exception handler (Issue #45)
    }

    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    public void givenCreatePatientDtoWithInvalidLastname_whenCreatePatient_thenReturns422UnprocessableEntity() throws Exception {
        String json = ow.writeValueAsString(new PatientCreateDto("1234123456", "a@a.a", "a",
            "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
        //TODO: fix the status code of the global exception handler (Issue #45)
    }

    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    public void givenCreatedPatient_whenGetPatient_thenReturnPatient() throws Exception {
        String json = ow.writeValueAsString(new PatientCreateDto("1234123456", "a@a.a", "a", "b"));
        byte[] bodyCreate = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_PDF))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();

        List<Patient> patients = patientRepository.findAll();
        assertThat(patients).hasSize(1);
        Patient patientCreate = patients.get(0);

        byte[] bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + patientCreate.getPatientId())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();
        List<PatientDto> patientsGet = objectMapper.readerFor(PatientDto.class).<PatientDto>readValues(bodyGet).readAll();
        assertThat(patientsGet).hasSize(1).extracting(PatientDto::svnr, PatientDto::email, PatientDto::firstname, PatientDto::lastname).containsExactly(
            tuple("1234123456", "a@a.a", "a", "b"));
    }

    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    public void givenNoPatientsInDatabase_whenGetPatientWithId1_thenReturns404NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    public void givenNoMatchingPatient_whenGetPatientWithNoId_thenReturns404NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    public void givenNoMatchingPatient_whenGetPatientWithInvalidId_thenReturns400BadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/testen")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    public void givenThreeCreatedPatients_whenGetAllPatients_thenReturnsThreePatients() throws Exception {
        String json1 = ow.writeValueAsString(new PatientCreateDto("1234123456", "a@a.a", "a", "b"));
        byte[] bodyCreate = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json1)
                .accept(MediaType.APPLICATION_PDF))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();

        String json2 = ow.writeValueAsString(new PatientCreateDto("6543214321", "b@b.b", "a", "b"));
        bodyCreate = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json2)
                .accept(MediaType.APPLICATION_PDF))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();

        String json3 = ow.writeValueAsString(new PatientCreateDto("6543123456", "c@c.c", "a", "b"));
        bodyCreate = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json3)
                .accept(MediaType.APPLICATION_PDF))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();

        List<Credential> credentials = patientRepository.findAllPatientCredentials();

        Credential patient1 = credentials.get(0);
        Credential patient2 = credentials.get(1);
        Credential patient3 = credentials.get(2);

        byte[] bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();
        List<UserLoginDto> patients = objectMapper.readerFor(UserLoginDto.class).<UserLoginDto>readValues(bodyGet).readAll();
        assertThat(patients).hasSize(3)
            .extracting(UserLoginDto::getEmail)
            .containsExactlyInAnyOrder(
                patient1.getEmail(),
                patient2.getEmail(),
                patient3.getEmail());
    }

    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    public void givenNoPatientsInDatabase_whenGetAllPatients_thenReturnsEmptyList() throws Exception {
        byte[] bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();
        List<PatientDto> patients = objectMapper.readerFor(PatientDto.class).<PatientDto>readValues(bodyGet).readAll();
        assertThat(patients).isEmpty();
    }
}