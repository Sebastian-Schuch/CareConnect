package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.TestBase;
import at.ac.tuwien.sepr.groupphase.backend.basetest.AllergyTestData;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AllergyDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AllergyDtoCreate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "datagen"})
@AutoConfigureMockMvc
public class AllergyEndpointTest extends TestBase implements AllergyTestData {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;


    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenAValidAllergyCreateDto_whenCreateAllergy_thenAllergyIsCreated() throws Exception {
        AllergyDtoCreate allergyCreateDto = new AllergyDtoCreate();
        allergyCreateDto.setName(ALLERGY_NAME_1);

        MvcResult mvcResult = mockMvc.perform(post(baseUri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(allergyCreateDto)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(201, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        AllergyDto allergyDto = mapper.readValue(response.getContentAsString(), AllergyDto.class);

        assertEquals(ALLERGY_NAME_1, allergyDto.getName());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenAnInvalidAllergyCreateDto_whenCreateAllergy_thenBadRequest() throws Exception {
        AllergyDtoCreate allergyCreateDto = new AllergyDtoCreate();
        allergyCreateDto.setName(null);

        MvcResult mvcResult = mockMvc.perform(post(baseUri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(allergyCreateDto)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(422, response.getStatus());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenAnAllergyId_whenCreateAllergy_thenAllergyIsCreated() throws Exception {
        AllergyDtoCreate allergyCreateDto = new AllergyDtoCreate();
        allergyCreateDto.setName(ALLERGY_NAME_1);
        allergyCreateDto.setId(1L);

        MvcResult mvcResult = mockMvc.perform(post(baseUri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(allergyCreateDto)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(201, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        AllergyDto allergyDto = mapper.readValue(response.getContentAsString(), AllergyDto.class);

        assertEquals(ALLERGY_NAME_1, allergyDto.getName());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenAnInvalidAllergyId_whenRequesting_thanNotFound() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get(baseUri + "/-1"))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(404, response.getStatus());
    }

}
