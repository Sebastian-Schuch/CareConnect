package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.TestBase;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.InpatientDepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.InpatientDepartmentDtoCreate;
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

import static at.ac.tuwien.sepr.groupphase.backend.basetest.InpatientDepartmentTestData.INPATIENT_DEPARTMENT_BASE_URI;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.InpatientDepartmentTestData.INPATIENT_DEPARTMENT_CAPACITY;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.InpatientDepartmentTestData.INPATIENT_DEPARTMENT_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "datagen"})
@AutoConfigureMockMvc
public class InpatientDepartmentEndpointTest extends TestBase {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    public InpatientDepartmentEndpointTest() {
        super("inpatientDepartment");
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenInpatientDepartment_whenCreateInpatientDepartment_thenInpatientDepartmentIsCreated() throws Exception {
        // given
        InpatientDepartmentDtoCreate inpatientDepartmentDtoCreate = new InpatientDepartmentDtoCreate(INPATIENT_DEPARTMENT_NAME, INPATIENT_DEPARTMENT_CAPACITY);

        // when
        MockHttpServletResponse response = mockMvc.perform(post(INPATIENT_DEPARTMENT_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inpatientDepartmentDtoCreate)))
            .andDo(print())
            .andReturn()
            .getResponse();

        // then
        assertEquals(201, response.getStatus());
        InpatientDepartmentDto createdInpatientDepartmentDto = objectMapper.readValue(response.getContentAsString(), InpatientDepartmentDto.class);
        assertNotNull(createdInpatientDepartmentDto.id());
        assertEquals(INPATIENT_DEPARTMENT_NAME, createdInpatientDepartmentDto.name());
        assertEquals(INPATIENT_DEPARTMENT_CAPACITY, createdInpatientDepartmentDto.capacity());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenInpatientDepartment_whenCreateAndRetrieveInpatientDepartment_thenInpatientDepartmentIsCreatedAndRetrieved() throws Exception {
        // given
        InpatientDepartmentDtoCreate inpatientDepartmentDtoCreate = new InpatientDepartmentDtoCreate(INPATIENT_DEPARTMENT_NAME, INPATIENT_DEPARTMENT_CAPACITY);

        // when
        MockHttpServletResponse createResponse = mockMvc.perform(post(INPATIENT_DEPARTMENT_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inpatientDepartmentDtoCreate)))
            .andDo(print())
            .andReturn()
            .getResponse();

        // then
        assertEquals(201, createResponse.getStatus());
        InpatientDepartmentDto createdInpatientDepartmentDto = objectMapper.readValue(createResponse.getContentAsString(), InpatientDepartmentDto.class);
        assertNotNull(createdInpatientDepartmentDto.id());
        assertEquals(INPATIENT_DEPARTMENT_NAME, createdInpatientDepartmentDto.name());
        assertEquals(INPATIENT_DEPARTMENT_CAPACITY, createdInpatientDepartmentDto.capacity());

        // when
        MockHttpServletResponse retrieveResponse = mockMvc.perform(get(INPATIENT_DEPARTMENT_BASE_URI + "/" + createdInpatientDepartmentDto.id())
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn()
            .getResponse();

        // then
        assertEquals(200, retrieveResponse.getStatus());
        InpatientDepartmentDto retrievedInpatientDepartmentDto = objectMapper.readValue(retrieveResponse.getContentAsString(), InpatientDepartmentDto.class);
        assertNotNull(retrievedInpatientDepartmentDto.id());
        assertEquals(INPATIENT_DEPARTMENT_NAME, retrievedInpatientDepartmentDto.name());
        assertEquals(INPATIENT_DEPARTMENT_CAPACITY, retrievedInpatientDepartmentDto.capacity());
    }

    @Test
    public void givenInvalidInpatientDepartment_whenCreateInpatientDepartment_then400() throws Exception {
        // given
        InpatientDepartmentDtoCreate inpatientDepartmentDtoCreate = new InpatientDepartmentDtoCreate(INPATIENT_DEPARTMENT_NAME, -1);

        // when
        MockHttpServletResponse response = mockMvc.perform(post(INPATIENT_DEPARTMENT_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inpatientDepartmentDtoCreate)))
            .andDo(print())
            .andReturn()
            .getResponse();

        // then
        assertEquals(422, response.getStatus());
    }


}
