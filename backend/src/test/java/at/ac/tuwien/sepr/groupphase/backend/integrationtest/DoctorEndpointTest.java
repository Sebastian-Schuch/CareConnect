package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.DoctorMapper;
import at.ac.tuwien.sepr.groupphase.backend.repository.CredentialRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.DoctorRepository;
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
public class DoctorEndpointTest {
    @Autowired
    private WebApplicationContext webAppContext;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    ObjectWriter ow;

    private static final String BASE_PATH = "/api/v1/doctors";

    @Autowired
    DoctorMapper doctorMapper;

    @Autowired
    CredentialService credentialService;

    @Autowired
    CredentialRepository credentialRepository;
    @Autowired
    private DoctorRepository doctorRepository;

    @BeforeEach
    public void beforeEach() {
        doctorRepository.deleteAll();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ow = objectMapper.writer().withDefaultPrettyPrinter();
    }

    @AfterEach
    public void cleanup() {
        doctorRepository.deleteAll();
    }

    @Test
    public void givenInvalidUrl_whenSendRequest_Returns404NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/bla")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void givenValidCreateDoctorDto_whenCreateDoctor_thenReturnCreatedDoctor() throws Exception {
        String json = ow.writeValueAsString(new DoctorCreateDto("a@a.a", "a", "b"));
        byte[] body = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();
        List<DoctorDto> doctors = objectMapper.readerFor(DoctorDto.class).<DoctorDto>readValues(body).readAll();
        assertNotNull(doctors);
        assertThat(doctors).hasSize(1).extracting(DoctorDto::firstname, DoctorDto::lastname, DoctorDto::email).containsExactly(
            tuple("a", "b", "a@a.a"));
    }

    @Test
    public void givenCreateDoctorDtoWithInvalidEmail_whenCreateDoctor_thenReturns422UnprocessableEntity() throws Exception {
        String json = ow.writeValueAsString(new DoctorCreateDto("a.a.a", "a", "b"));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
        //TODO: fix the status code of the global exception handler (Issue #45)
    }

    @Test
    public void givenCreateDoctorDtoWithInvalidFirstname_whenCreateDoctor_thenReturns422UnprocessableEntity() throws Exception {
        String json = ow.writeValueAsString(new DoctorCreateDto("a@a.a", "", "b"));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
        //TODO: fix the status code of the global exception handler (Issue #45)
    }

    @Test
    public void givenCreateDoctorDtoWithInvalidLastname_whenCreateDoctor_thenReturns422UnprocessableEntity() throws Exception {
        String json = ow.writeValueAsString(new DoctorCreateDto("a@a.a", "a",
            "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"));
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
        //TODO: fix the status code of the global exception handler (Issue #45)
    }

    @Test
    public void givenCreatedDoctor_whenGetDoctor_thenReturnDoctor() throws Exception {
        String json = ow.writeValueAsString(new DoctorCreateDto("a@a.a", "a", "b"));
        byte[] bodyCreate = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();
        List<DoctorDto> doctorsCreate = objectMapper.readerFor(DoctorDto.class).<DoctorDto>readValues(bodyCreate).readAll();
        DoctorDto doctorCreate = doctorsCreate.getFirst();

        byte[] bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + doctorCreate.id())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();
        List<DoctorDto> doctorsGet = objectMapper.readerFor(DoctorDto.class).<DoctorDto>readValues(bodyGet).readAll();
        assertThat(doctorsGet).hasSize(1).extracting(DoctorDto::email, DoctorDto::firstname, DoctorDto::lastname).containsExactly(
            tuple("a@a.a", "a", "b"));
    }

    @Test
    public void givenNoDoctorsInDatabase_whenGetDoctorWithId1_thenReturns404NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void givenNoMatchingDoctor_whenGetDoctorWithNoId_thenReturns404NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void givenNoMatchingDoctor_whenGetDoctorWithInvalidId_thenReturns400BadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/testen")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenThreeCreatedDoctors_whenGetAllDoctors_thenReturnsThreeDoctors() throws Exception {
        String json = ow.writeValueAsString(new DoctorCreateDto("a@a.a", "a", "b"));
        byte[] bodyCreate = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();
        DoctorDto doctor1 = objectMapper.readerFor(DoctorDto.class).<DoctorDto>readValues(bodyCreate).readAll().getFirst();
        json = ow.writeValueAsString(new DoctorCreateDto("b@b.b", "a", "b"));
        bodyCreate = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();
        DoctorDto doctor2 = objectMapper.readerFor(DoctorDto.class).<DoctorDto>readValues(bodyCreate).readAll().getFirst();
        json = ow.writeValueAsString(new DoctorCreateDto("c@c.c", "a", "b"));
        bodyCreate = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();
        DoctorDto doctor3 = objectMapper.readerFor(DoctorDto.class).<DoctorDto>readValues(bodyCreate).readAll().getFirst();

        byte[] bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();
        List<DoctorDto> doctors = objectMapper.readerFor(DoctorDto.class).<DoctorDto>readValues(bodyGet).readAll();
        assertThat(doctors).hasSize(3).extracting(DoctorDto::id, DoctorDto::email, DoctorDto::firstname, DoctorDto::lastname, DoctorDto::password)
            .containsExactlyInAnyOrder(
                tuple(doctor1.id(), doctor1.email(), doctor1.firstname(), doctor1.lastname(), doctor1.password()),
                tuple(doctor2.id(), doctor2.email(), doctor2.firstname(), doctor2.lastname(), doctor2.password()),
                tuple(doctor3.id(), doctor3.email(), doctor3.firstname(), doctor3.lastname(), doctor3.password()));
    }

    @Test
    public void givenNoDoctorsInDatabase_whenGetAllDoctors_thenReturnsEmptyList() throws Exception {
        byte[] bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();
        List<DoctorDto> doctors = objectMapper.readerFor(DoctorDto.class).<DoctorDto>readValues(bodyGet).readAll();
        assertThat(doctors).isEmpty();
    }
}
