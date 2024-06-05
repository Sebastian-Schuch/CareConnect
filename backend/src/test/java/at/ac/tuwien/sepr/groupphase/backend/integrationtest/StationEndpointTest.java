package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.TestBase;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StationDtoCreate;
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

import static at.ac.tuwien.sepr.groupphase.backend.basetest.StationTestData.MESSAGE_BASE_URI;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.StationTestData.STATION_CAPACITY;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.StationTestData.STATION_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "datagen"})
@AutoConfigureMockMvc
public class StationEndpointTest extends TestBase {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    public StationEndpointTest() {
        super("station");
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenStation_whenCreateStation_thenStationIsCreated() throws Exception {
        // given
        StationDtoCreate stationDtoCreate = new StationDtoCreate(STATION_NAME, STATION_CAPACITY);

        // when
        MockHttpServletResponse response = mockMvc.perform(post(MESSAGE_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stationDtoCreate)))
            .andDo(print())
            .andReturn()
            .getResponse();

        // then
        assertEquals(201, response.getStatus());
        StationDto stationDto = objectMapper.readValue(response.getContentAsString(), StationDto.class);
        assertNotNull(stationDto.getId());
        assertEquals(STATION_NAME, stationDto.getName());
        assertEquals(STATION_CAPACITY, stationDto.getCapacity());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenStation_whenCreateAndRetrieveStation_thenStationIsCreatedAndRetrieved() throws Exception {
        // given
        StationDtoCreate stationDtoCreate = new StationDtoCreate(STATION_NAME, STATION_CAPACITY);

        // when
        MockHttpServletResponse createResponse = mockMvc.perform(post(MESSAGE_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stationDtoCreate)))
            .andDo(print())
            .andReturn()
            .getResponse();

        // then
        assertEquals(201, createResponse.getStatus());
        StationDto createdStationDto = objectMapper.readValue(createResponse.getContentAsString(), StationDto.class);
        assertNotNull(createdStationDto.getId());
        assertEquals(STATION_NAME, createdStationDto.getName());
        assertEquals(STATION_CAPACITY, createdStationDto.getCapacity());

        // when
        MockHttpServletResponse retrieveResponse = mockMvc.perform(get(MESSAGE_BASE_URI + "/" + createdStationDto.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn()
            .getResponse();

        // then
        assertEquals(200, retrieveResponse.getStatus());
        StationDto retrievedStationDto = objectMapper.readValue(retrieveResponse.getContentAsString(), StationDto.class);
        assertNotNull(retrievedStationDto.getId());
        assertEquals(STATION_NAME, retrievedStationDto.getName());
        assertEquals(STATION_CAPACITY, retrievedStationDto.getCapacity());
    }

    @Test
    public void givenInvalidStation_whenCreateStation_then400() throws Exception {
        // given
        StationDtoCreate stationDtoCreate = new StationDtoCreate(STATION_NAME, -1);

        // when
        MockHttpServletResponse response = mockMvc.perform(post(MESSAGE_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stationDtoCreate)))
            .andDo(print())
            .andReturn()
            .getResponse();

        // then
        assertEquals(422, response.getStatus());
    }


}
