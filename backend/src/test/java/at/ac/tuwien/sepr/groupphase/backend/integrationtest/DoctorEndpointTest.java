package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.TestBase;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoUpdate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.DoctorMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.repository.CredentialRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.DoctorRepository;
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
public class DoctorEndpointTest extends TestBase {
    @Autowired
    private WebApplicationContext webAppContext;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    ObjectWriter ow;

    @Autowired
    DoctorRepository doctorRepository;

    private static final String BASE_PATH = "/api/v1/doctors";

    @Autowired
    DoctorMapper doctorMapper;

    @Autowired
    CredentialRepository credentialRepository;

    public DoctorEndpointTest() {
        super("doctor");
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

    @Transactional
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenInvalidUrl_whenSendRequest_Returns404NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/bla")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Transactional
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenValidCreateDoctorDto_whenCreateDoctor_thenReturnCreatedDoctor() throws Exception {
        String json = ow.writeValueAsString(new DoctorDtoCreate("a@a.a", "a", "b"));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_PDF))
            .andExpect(status().isCreated());

        List<Credential> credentials = doctorRepository.findAllDoctorCredentials();
        assertNotNull(credentials);
        assertThat(credentials).extracting(Credential::getEmail, Credential::getFirstName, Credential::getLastName).contains(
            tuple("a@a.a", "a", "b"));
    }

    @Transactional
    @Test
    @WithMockUser(username = "admin", authorities = {"SECRETARY"})
    public void givenWrongAuthority_whenCreateDoctor_thenReturnForbiddenStatus() throws Exception {
        String json = ow.writeValueAsString(new DoctorDtoCreate("a@a.a", "a", "b"));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Transactional
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenCreateDoctorDtoWithInvalidEmail_whenCreateDoctor_thenReturns422UnprocessableEntity() throws Exception {
        String json = ow.writeValueAsString(new DoctorDtoCreate("a.a.a", "a", "b"));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity());
    }

    @Transactional
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenCreateDoctorDtoWithInvalidFirstname_whenCreateDoctor_thenReturns422UnprocessableEntity() throws Exception {
        String json = ow.writeValueAsString(new DoctorDtoCreate("a@a.a", "", "b"));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity());
    }


    @Transactional
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenCreateDoctorDtoWithInvalidLastname_whenCreateDoctor_thenReturns422UnprocessableEntity() throws Exception {
        String json = ow.writeValueAsString(new DoctorDtoCreate("a@a.a", "a",
            "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity());
    }

    @Transactional
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenCreateDoctorDtoWithAlreadyExistingEmail_whenCreateDoctor_thenReturns409Conflict() throws Exception {
        String json = ow.writeValueAsString(new DoctorDtoCreate("a@a.a", "a", "b"));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());

        json = ow.writeValueAsString(new DoctorDtoCreate("a@a.a", "c", "d"));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());
    }

    @Transactional
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenIdOfDoctor_whenGetDoctor_thenReturnDoctor() throws Exception {
        byte[] bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + doctorRepository.findAll().get(0).getDoctorId())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();
        List<DoctorDto> doctorsGet = objectMapper.readerFor(DoctorDto.class).<DoctorDto>readValues(bodyGet).readAll();
        assertNotNull(doctorsGet);
        assertThat(doctorsGet).extracting(DoctorDto::email, DoctorDto::firstname, DoctorDto::lastname).contains(
            tuple("doctor.eggman@email.com", "Doctor", "Eggman"));
    }

    @Transactional
    @Test
    @WithMockUser(username = "patient", authorities = {"PATIENT"})
    public void givenIdOfDoctor_whenGetDoctorWithWrongAuthority_thenReturnForbiddenStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + doctorRepository.findAll().get(0).getDoctorId())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Transactional
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenNoDoctorsWithNegativeId_whenGetDoctorWithNegativeId_thenReturns404NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/-1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Transactional
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenNoMatchingDoctor_whenGetDoctorWithNoId_thenReturns404NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Transactional
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenNoMatchingDoctor_whenGetDoctorWithInvalidId_thenReturns400BadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/testen")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Transactional
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenNoDoctorsInDatabase_whenGetAllDoctors_thenReturnsEmptyList() throws Exception {
        doctorRepository.deleteAll();
        byte[] bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();
        List<DoctorDto> doctors = objectMapper.readerFor(DoctorDto.class).<DoctorDto>readValues(bodyGet).readAll();
        assertThat(doctors).isEmpty();
    }

    @Transactional
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenThreeCreatedDoctors_whenGetAllDoctors_thenReturnsThreeDoctors() throws Exception {
        doctorRepository.deleteAll();
        String json1 = ow.writeValueAsString(new DoctorDtoCreate("a@a.a", "a", "b"));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json1)
                .accept(MediaType.APPLICATION_PDF))
            .andExpect(status().isCreated());

        String json2 = ow.writeValueAsString(new DoctorDtoCreate("b@b.b", "a", "b"));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json2)
                .accept(MediaType.APPLICATION_PDF))
            .andExpect(status().isCreated());

        String json3 = ow.writeValueAsString(new DoctorDtoCreate("c@c.c", "a", "b"));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json3)
                .accept(MediaType.APPLICATION_PDF))
            .andExpect(status().isCreated());

        List<Credential> credentials = doctorRepository.findAllDoctorCredentials();
        Credential doctor1 = credentials.get(0);
        Credential doctor2 = credentials.get(1);
        Credential doctor3 = credentials.get(2);

        byte[] bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();
        List<UserLoginDto> doctors = objectMapper.readerFor(UserLoginDto.class).<UserLoginDto>readValues(bodyGet).readAll();
        assertThat(doctors).hasSize(3).extracting(UserLoginDto::getEmail)
            .containsExactlyInAnyOrder(
                doctor1.getEmail(),
                doctor2.getEmail(),
                doctor3.getEmail());
    }

    @Transactional
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenNonExistentId_whenUpdateDoctor_thenReturns404NotFound() throws Exception {
        String json = ow.writeValueAsString(new DoctorDtoUpdate("Updated", "Doctor", "updated@email.com", false, true));
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + "/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_PDF))
            .andExpect(status().isNotFound());
    }

    @Transactional
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenDoctorDtoUpdate_whenUpdateDoctor_thenDoctorGetsUpdatedAndNowHasUpdatedData() throws Exception {
        long id = doctorRepository.findAll().get(0).getDoctorId();
        byte[] bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + id)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();
        List<DoctorDto> doctorsGet = objectMapper.readerFor(DoctorDto.class).<DoctorDto>readValues(bodyGet).readAll();
        assertThat(doctorsGet).extracting(DoctorDto::email, DoctorDto::firstname, DoctorDto::lastname).contains(
            tuple("doctor.eggman@email.com", "Doctor", "Eggman"));

        String json = ow.writeValueAsString(new DoctorDtoUpdate("Updated", "Doctor", "updated@email.com", false, true));
        byte[] bodyUpdate = mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();
        doctorsGet = objectMapper.readerFor(DoctorDto.class).<DoctorDto>readValues(bodyUpdate).readAll();
        assertThat(doctorsGet).extracting(DoctorDto::email, DoctorDto::firstname, DoctorDto::lastname).contains(
            tuple("updated@email.com", "Updated", "Doctor"));

        bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + id)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();
        doctorsGet = objectMapper.readerFor(DoctorDto.class).<DoctorDto>readValues(bodyGet).readAll();
        assertThat(doctorsGet).extracting(DoctorDto::email, DoctorDto::firstname, DoctorDto::lastname).contains(
            tuple("updated@email.com", "Updated", "Doctor"));
    }

    @Transactional
    @Test
    @WithMockUser(username = "admin", authorities = {"SECRETARY"})
    public void givenWrongAuthority_whenUpdateDoctor_thenReturnForbiddenStatus() throws Exception {
        String json = ow.writeValueAsString(new DoctorDtoUpdate("Updated", "Doctor", "updated@email.com", false, true));
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Transactional
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenUserDtoSearch_whenSearchDoctors_thenReturnListOfMatchingDoctors() throws Exception {
        byte[] body = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/search")
                .queryParam("email", "doctor.eggman@email.com")
                .queryParam("firstName", "Doctor")
                .queryParam("lastName", "Eggman")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        List<DoctorDto> doctors = objectMapper.readerFor(DoctorDto.class).<DoctorDto>readValues(body).readAll();
        assertThat(doctors).hasSize(1)
            .extracting(DoctorDto::email, DoctorDto::firstname, DoctorDto::lastname).contains(
                AssertionsForClassTypes.tuple("doctor.eggman@email.com", "Doctor", "Eggman"));
    }

    @Transactional
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenNonExistentUserDtoSearch_whenSearchDoctors_thenReturnEmptyList() throws Exception {
        byte[] body = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/search")
                .queryParam("email", "doctor.nonExistent@email.com")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        List<DoctorDto> doctors = objectMapper.readerFor(DoctorDto.class).<DoctorDto>readValues(body).readAll();
        assertThat(doctors).hasSize(0);
    }

    @Transactional
    @Test
    @WithMockUser(username = "admin", authorities = {"PATIENT"})
    public void givenWrongAuthority_whenSearchDoctors_thenReturnForbiddenStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/search")
                .queryParam("email", "doctor.proper@email.com")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }
}
