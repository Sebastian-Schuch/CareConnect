package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.MedicationMapper;
import at.ac.tuwien.sepr.groupphase.backend.repository.MedicationRepository;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class MedicationEndpointTest {
    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    ObjectWriter ow;
    static final String BASE_PATH = "/api/v1/medications";
    @Autowired
    private MedicationRepository medicationRepository;


    @Autowired
    private MedicationMapper medicationMapper;


    @BeforeEach
    public void beforeEach() {
        medicationRepository.deleteAll();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ow = objectMapper.writer().withDefaultPrettyPrinter();
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenInvalidUrl_whenSendRequest_Returns404NotFoundStatus() throws Exception {
        mockMvc
            .perform(MockMvcRequestBuilders
                .get("/asdf123")
                .accept(MediaType.APPLICATION_JSON)
            ).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenValidData_whenCreateNewMedication_thenReturn200OkStatusAndCreatedMedication() throws Exception {
        String json = ow.writeValueAsString(new MedicationCreateDto("WieAgra"));
        byte[] body = mockMvc
            .perform(MockMvcRequestBuilders
                .post(BASE_PATH).contentType(MediaType.APPLICATION_JSON).content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();

        List<MedicationDto> medicationResult = objectMapper.readerFor(MedicationDto.class)
            .<MedicationDto>readValues(body).readAll();

        assertThat(medicationResult)
            .isNotNull()
            .hasSize(1)
            .extracting(MedicationDto::name)
            .containsExactly("WieAgra");
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenEmptyName_whenCreateNewMedication_thenReturn422UnprocessableEntityStatus() throws Exception {
        String json = ow.writeValueAsString(new MedicationCreateDto(""));
        mockMvc
            .perform(MockMvcRequestBuilders
                .post(BASE_PATH).contentType(MediaType.APPLICATION_JSON).content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void giveNameTooLong_whenCreateNewMedication_thenReturn422UnprocessableEntityStatus() throws Exception {
        String json = ow.writeValueAsString(new MedicationCreateDto(
            "We're no strangers to loveYou know the rules and so do IA full commitment's what I'm thinking ofYou wouldn't get this from any other guyI just wanna tell you how I'm feelingGotta make you understandNever gonna give you upNever gonna let you downNever gonna run around and desert youNever gonna make you cryNever gonna say goodbyeNever gonna tell a lie and hurt youWe've known each other for so longYour heart's been aching, butYou're too shy to say itInside, we both know what's been going onWe know the game and we're gonna play itAnd if you ask me how I'm feelingDon't tell me you're too blind to seeNever gonna give you upNever gonna let you downNever gonna run around and desert youNever gonna make you cryNever gonna say goodbyeNever gonna tell a lie and hurt youNever gonna give you upNever gonna let you downNever gonna run around and desert youNever gonna make you cryNever gonna say goodbyeNever gonna tell a lie and hurt you(Ooh, give you up)(Ooh, give you up)Never gonna give, never gonna give(Give you up)Never gonna give, never gonna give(Give you up)We've known each other for so longYour heart's been aching, butYou're too shy to say itInside, we both know what's been going onWe know the game and we're gonna play itI just wanna tell you how I'm feelingGotta make you understandNever gonna give you upNever gonna let you downNever gonna run around and desert youNever gonna make you cryNever gonna say goodbyeNever gonna tell a lie and hurt youNever gonna give you upNever gonna let you downNever gonna run around and desert youNever gonna make you cryNever gonna say goodbyeNever gonna tell a lie and hurt youNever gonna give you upNever gonna let you downNever gonna run around and desert youNever gonna make you cryNever gonna say goodbyeNever gonna tell a lie and hurt you"));
        mockMvc
            .perform(MockMvcRequestBuilders
                .post(BASE_PATH).contentType(MediaType.APPLICATION_JSON).content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenNewlyCreatedMedication_whenGetMedication_thenReturnMedication() throws Exception {
        String json = ow.writeValueAsString(new MedicationCreateDto("WieAgra"));
        byte[] bodyCreate =
            mockMvc
                .perform(MockMvcRequestBuilders.post(BASE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsByteArray();
        List<MedicationDto> medicationResult = objectMapper.readerFor(MedicationDto.class)
            .<MedicationDto>readValues(bodyCreate).readAll();

        byte[] bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + medicationResult.get(0).id())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();
        List<MedicationDto> medicationGet = objectMapper.readerFor(MedicationDto.class).<MedicationDto>readValues(bodyGet).readAll();
        assertThat(medicationGet).hasSize(1).extracting(MedicationDto::name).containsExactly("WieAgra");
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenInvalidId_whenGetMedication_thenReturn404NotFoundStatus() throws Exception {
        mockMvc
            .perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + -1)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenThreeCreatedMedications_whenGetAllMedications_thenReturnsThreeMedications() throws Exception {
        String json = ow.writeValueAsString(new MedicationCreateDto("WieAgra"));
        byte[] bodyCreate = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();
        MedicationDto medication1 = objectMapper.readerFor(MedicationDto.class).<MedicationDto>readValues(bodyCreate).readAll().get(0);

        bodyCreate = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();
        MedicationDto medication2 = objectMapper.readerFor(MedicationDto.class).<MedicationDto>readValues(bodyCreate).readAll().get(0);

        bodyCreate = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();
        MedicationDto medication3 = objectMapper.readerFor(MedicationDto.class).<MedicationDto>readValues(bodyCreate).readAll().get(0);

        byte[] bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();
        List<MedicationDto> medications = objectMapper.readerFor(MedicationDto.class).<MedicationDto>readValues(bodyGet).readAll();
        assertThat(medications).hasSize(3).extracting(MedicationDto::id, MedicationDto::name).containsExactlyInAnyOrder(
            tuple(medication1.id(), medication1.name()),
            tuple(medication2.id(), medication2.name()),
            tuple(medication3.id(), medication3.name()));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenNoMedicationsInDatabase_whenGetAllMedicationss_thenReturnsEmptyList() throws Exception {
        byte[] bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();
        List<MedicationDto> medications = objectMapper.readerFor(MedicationDto.class).<MedicationDto>readValues(bodyGet).readAll();
        assertThat(medications).isEmpty();
    }
}
