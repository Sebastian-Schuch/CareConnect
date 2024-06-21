package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.TestBase;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AllergyDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoUpdate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PatientMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.repository.CredentialRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PatientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.assertj.core.api.AssertionsForClassTypes;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "datagen"})
@AutoConfigureMockMvc
public class PatientEndpointTest extends TestBase {
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

    public PatientEndpointTest() {
        super("patient");
    }

    @BeforeEach
    public void beforeEach() {
        this.mockMvc = MockMvcBuilders
            .webAppContextSetup(webAppContext)
            .apply(springSecurity())
            .build();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ow = objectMapper.writer().withDefaultPrettyPrinter();
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
        List<MedicationDto> medications = new ArrayList<>();
        List<AllergyDto> allergies = new ArrayList<>();
        String json = ow.writeValueAsString(new PatientDtoCreate("1234123456", "a@a.a", "a", "b", medications, allergies));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());

        List<Credential> credentials = patientRepository.findAllPatientCredentials();

        assertNotNull(credentials);
        assertThat(credentials).extracting(Credential::getEmail, Credential::getFirstName, Credential::getLastName).contains(
            tuple("a@a.a", "a", "b"));
    }

    @Transactional
    @Test
    @WithMockUser(username = "admin", authorities = {"DOCTOR"})
    public void givenWrongAuthority_whenCreatePatient_thenReturnForbiddenStatus() throws Exception {
        List<MedicationDto> medications = new ArrayList<>();
        List<AllergyDto> allergies = new ArrayList<>();
        String json = ow.writeValueAsString(new PatientDtoCreate("1234123456", "a@a.a", "a", "b", medications, allergies));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    public void givenCreatePatientDtoWithInvalidEmail_whenCreatePatient_thenReturns422UnprocessableEntity() throws Exception {
        String json = ow.writeValueAsString(new PatientDtoCreate("1234123456", "a.a.a", "a", "b", null, null));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    public void givenCreatePatientDtoWithInvalidSvnr_whenCreatePatient_thenReturns422UnprocessableEntity() throws Exception {
        String json = ow.writeValueAsString(new PatientDtoCreate("12341234567", "a@a.a", "a", "b", null, null));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    public void givenCreatePatientDtoWithInvalidFirstname_whenCreatePatient_thenReturns422UnprocessableEntity() throws Exception {
        String json = ow.writeValueAsString(new PatientDtoCreate("1234123456", "a@a.a", "", "b", null, null));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    public void givenCreatePatientDtoWithInvalidLastname_whenCreatePatient_thenReturns422UnprocessableEntity() throws Exception {
        String json = ow.writeValueAsString(new PatientDtoCreate("1234123456", "a@a.a", "a",
            "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
            null, null));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    public void givenValidCreatePatientDtoWithAlreadyExistingEmail_whenCreatePatient_thenReturn409ConflictStatus() throws Exception {
        List<MedicationDto> medications = new ArrayList<>();
        List<AllergyDto> allergies = new ArrayList<>();
        String json = ow.writeValueAsString(new PatientDtoCreate("1234123456", "a@a.a", "a", "b", medications, allergies));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    public void givenCreatedPatient_whenGetPatient_thenReturnPatient() throws Exception {
        byte[] bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + patientRepository.findAll().get(0).getPatientId())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();
        List<PatientDto> patientsGet = objectMapper.readerFor(PatientDto.class).<PatientDto>readValues(bodyGet).readAll();
        assertThat(patientsGet).extracting(PatientDto::svnr, PatientDto::email, PatientDto::firstname, PatientDto::lastname).contains(
            tuple("6912120520", "chris.anger@email.com", "Chris", "Anger"));
    }

    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    public void givenNoPatientsInDatabase_whenGetPatientWithId1_thenReturns404NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/-1")
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

    @Transactional
    @Test
    @WithMockUser(username = "patient", authorities = {"PATIENT"})
    public void givenIdOfPatient_whenGetPatientWithWrongAuthority_thenReturnForbiddenStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + patientRepository.findAll().get(0).getPatientId())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    public void givenThreeCreatedPatients_whenGetAllPatients_thenReturnsThreePatients() throws Exception {
        patientRepository.deleteAll();
        List<MedicationDto> medications = new ArrayList<>();
        List<AllergyDto> allergies = new ArrayList<>();
        String json1 = ow.writeValueAsString(new PatientDtoCreate("1234123456", "a@a.a", "a", "b", medications, allergies));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json1)
                .accept(MediaType.APPLICATION_PDF))
            .andExpect(status().isCreated());

        String json2 = ow.writeValueAsString(new PatientDtoCreate("6543214321", "b@b.b", "a", "b", medications, allergies));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json2)
                .accept(MediaType.APPLICATION_PDF))
            .andExpect(status().isCreated());

        String json3 = ow.writeValueAsString(new PatientDtoCreate("6543123456", "c@c.c", "a", "b", medications, allergies));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json3)
                .accept(MediaType.APPLICATION_PDF))
            .andExpect(status().isCreated());

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
        patientRepository.deleteAll();
        byte[] bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();
        List<PatientDto> patients = objectMapper.readerFor(PatientDto.class).<PatientDto>readValues(bodyGet).readAll();
        assertThat(patients).isEmpty();
    }

    @Transactional
    @Test
    @WithMockUser(username = "admin", authorities = {"SECRETARY"})
    public void givenNonExistentId_whenUpdateDoctor_thenReturns404NotFound() throws Exception {
        List<MedicationDto> medications = new ArrayList<>();
        List<AllergyDto> allergies = new ArrayList<>();
        PatientDtoUpdate patientToUpdate = new PatientDtoUpdate("0000000000", medications, allergies, "x", "y", "a@a.a", false, false);
        String json = ow.writeValueAsString(patientToUpdate);
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + "/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_PDF))
            .andExpect(status().isNotFound());
    }

    @Transactional
    @Test
    @WithMockUser(username = "admin", authorities = {"SECRETARY"})
    public void givenSecretaryDtoUpdate_whenUpdateSecretary_thenSecretaryGetsUpdatedAndNowHasUpdatedData() throws Exception {
        long id = patientRepository.findAll().get(0).getPatientId();
        byte[] bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + id)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();
        List<PatientDto> patientsGet = objectMapper.readerFor(PatientDto.class).<PatientDto>readValues(bodyGet).readAll();
        assertThat(patientsGet).extracting(PatientDto::svnr, PatientDto::email, PatientDto::firstname, PatientDto::lastname).contains(
            tuple("6912120520", "chris.anger@email.com", "Chris", "Anger"));

        List<MedicationDto> medications = new ArrayList<>();
        List<AllergyDto> allergies = new ArrayList<>();
        PatientDtoUpdate patientToUpdate = new PatientDtoUpdate("0000000005", medications, allergies, "x", "y", "a@a.a", false, false);
        String json = ow.writeValueAsString(patientToUpdate);
        byte[] bodyUpdate = mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();
        patientsGet = objectMapper.readerFor(PatientDto.class).<PatientDto>readValues(bodyUpdate).readAll();
        assertThat(patientsGet).extracting(PatientDto::svnr, PatientDto::email, PatientDto::firstname, PatientDto::lastname).contains(
            tuple("0000000005", "a@a.a", "x", "y"));

        bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + id)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();
        patientsGet = objectMapper.readerFor(PatientDto.class).<PatientDto>readValues(bodyGet).readAll();
        assertThat(patientsGet).extracting(PatientDto::svnr, PatientDto::email, PatientDto::firstname, PatientDto::lastname).contains(
            tuple("0000000005", "a@a.a", "x", "y"));
    }

    @Transactional
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenWrongAuthority_whenUpdateSecretary_thenReturnForbiddenStatus() throws Exception {
        List<MedicationDto> medications = new ArrayList<>();
        List<AllergyDto> allergies = new ArrayList<>();
        PatientDtoUpdate patientToUpdate = new PatientDtoUpdate("0000000000", medications, allergies, "x", "y", "a@a.a", false, false);
        String json = ow.writeValueAsString(patientToUpdate);
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Transactional
    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    public void givenUserDtoSearch_whenSearchPatients_thenReturnListOfMatchingPatients() throws Exception {
        byte[] body = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/search")
                .queryParam("email", "chris.anger@email.com")
                .queryParam("firstName", "Chris")
                .queryParam("lastName", "Anger")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        List<PatientDto> patients = objectMapper.readerFor(PatientDto.class).<PatientDto>readValues(body).readAll();
        assertThat(patients).hasSize(1)
            .extracting(PatientDto::email, PatientDto::firstname, PatientDto::lastname).contains(
                AssertionsForClassTypes.tuple("chris.anger@email.com", "Chris", "Anger"));
    }

    @Transactional
    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    public void givenNonExistentUserDtoSearch_whenSearchPatients_thenReturnEmptyList() throws Exception {
        byte[] body = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/search")
                .queryParam("email", "patient.nonExistent@email.com")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        List<PatientDto> patients = objectMapper.readerFor(PatientDto.class).<PatientDto>readValues(body).readAll();
        assertThat(patients).hasSize(0);
    }

    @Transactional
    @Test
    @WithMockUser(username = "admin", authorities = {"PATIENT"})
    public void givenWrongAuthority_whenSearchPatients_thenReturnForbiddenStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/search")
                .queryParam("email", "asylum.patient@email.com")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

}