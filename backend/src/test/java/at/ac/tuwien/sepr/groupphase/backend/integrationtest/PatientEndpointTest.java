package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PatientMapper;
import at.ac.tuwien.sepr.groupphase.backend.repository.CredentialRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PatientRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.CredentialService;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PatientEndpointTest implements TestData {
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
    CredentialService credentialService;

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
    public void givenInvalidUrl_whenSendRequest_Returns404NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/bla")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void givenValidCreatePatientDto_whenCreatePatient_thenReturnCreatedPatient() throws Exception {
        String json = ow.writeValueAsString(new PatientCreateDto("1234123456", "a@a.a", "a", "b"));
        byte[] body = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();
        List<PatientDto> patients = objectMapper.readerFor(PatientDto.class).<PatientDto>readValues(body).readAll();
        assertNotNull(patients);
        assertThat(patients).hasSize(1).extracting(PatientDto::firstname, PatientDto::lastname, PatientDto::email, PatientDto::svnr).containsExactly(
            tuple("a", "b", "a@a.a", "1234123456"));
    }

    @Test
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
    public void givenCreatePatientDtoWithInvalidLastname_whenCreatePatient_thenReturns422UnprocessableEntity() throws Exception {
        String json = ow.writeValueAsString(new PatientCreateDto("1234123456", "a@a.a", "a",
            "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
        //TODO: is this the right statuscode?
    }

    @Test
    public void givenCreatedPatient_whenGetPatient_thenReturnPatient() throws Exception {
        String json = ow.writeValueAsString(new PatientCreateDto("1234123456", "a@a.a", "a", "b"));
        byte[] bodyCreate = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();
        List<PatientDto> patientsCreate = objectMapper.readerFor(PatientDto.class).<PatientDto>readValues(bodyCreate).readAll();
        PatientDto patientCreate = patientsCreate.getFirst();

        byte[] bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + patientCreate.id())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();
        List<PatientDto> patientsGet = objectMapper.readerFor(PatientDto.class).<PatientDto>readValues(bodyGet).readAll();
        assertThat(patientsGet).hasSize(1).extracting(PatientDto::svnr, PatientDto::email, PatientDto::firstname, PatientDto::lastname).containsExactly(
            tuple("1234123456", "a@a.a", "a", "b"));
    }

    @Test
    public void givenNoPatientsInDatabase_whenGetPatientWithId1_thenReturns404NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void givenNoMatchingPatient_whenGetPatientWithNoId_thenReturns404NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void givenNoMatchingPatient_whenGetPatientWithInvalidId_thenReturns400BadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/testen")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenThreeCreatedPatients_whenGetAllPatients_thenReturnsThreePatients() throws Exception {
        String json = ow.writeValueAsString(new PatientCreateDto("1234123456", "a@a.a", "a", "b"));
        byte[] bodyCreate = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();
        PatientDto patient1 = objectMapper.readerFor(PatientDto.class).<PatientDto>readValues(bodyCreate).readAll().getFirst();
        json = ow.writeValueAsString(new PatientCreateDto("1234123456", "b@b.b", "a", "b"));
        bodyCreate = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();
        PatientDto patient2 = objectMapper.readerFor(PatientDto.class).<PatientDto>readValues(bodyCreate).readAll().getFirst();
        json = ow.writeValueAsString(new PatientCreateDto("1234123456", "c@c.c", "a", "b"));
        bodyCreate = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();
        PatientDto patient3 = objectMapper.readerFor(PatientDto.class).<PatientDto>readValues(bodyCreate).readAll().getFirst();

        byte[] bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();
        List<PatientDto> patients = objectMapper.readerFor(PatientDto.class).<PatientDto>readValues(bodyGet).readAll();
        assertThat(patients).hasSize(3).extracting(PatientDto::id, PatientDto::svnr, PatientDto::email, PatientDto::firstname, PatientDto::lastname, PatientDto::password).containsExactlyInAnyOrder(
            tuple(patient1.id(), patient1.svnr(), patient1.email(), patient1.firstname(), patient1.lastname(), patient1.password()),
            tuple(patient2.id(), patient2.svnr(), patient2.email(), patient2.firstname(), patient2.lastname(), patient2.password()),
            tuple(patient3.id(), patient3.svnr(), patient3.email(), patient3.firstname(), patient3.lastname(), patient3.password()));
    }

    @Test
    public void givenNoPatientsInDatabase_whenGetAllPatients_thenReturnsEmptyList() throws Exception {
        byte[] bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();
        List<PatientDto> patients = objectMapper.readerFor(PatientDto.class).<PatientDto>readValues(bodyGet).readAll();
        assertThat(patients).isEmpty();
    }
}