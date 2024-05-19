package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.OutpatientDepartmentEndpoint;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpeningHoursDayDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpeningHoursDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.entity.OpeningHours;
import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import at.ac.tuwien.sepr.groupphase.backend.repository.OutpatientDepartmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class OutpatientDepartmentEndpointTest {

    @Autowired
    private OutpatientDepartmentEndpoint outpatientDepartmentEndpoint;

    @Autowired
    private OutpatientDepartmentRepository outpatientDepartmentRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final OpeningHours openingHours = new OpeningHours()
        .setId(null)
        .setMonday("08:00-14:00")
        .setTuesday("08:00-14:00")
        .setWednesday("08:00-14:00")
        .setThursday("08:00-14:00")
        .setFriday("08:00-14:00")
        .setSaturday("08:00-14:00")
        .setSunday(null);

    private final OpeningHoursDtoCreate openingHoursDtoCreate = new OpeningHoursDtoCreate(
        new OpeningHoursDayDto(LocalTime.of(8, 0),
            LocalTime.of(14, 0)),
        new OpeningHoursDayDto(LocalTime.of(8, 0),
            LocalTime.of(14, 0)),
        new OpeningHoursDayDto(LocalTime.of(8, 0),
            LocalTime.of(14, 0)),
        new OpeningHoursDayDto(LocalTime.of(8, 0),
            LocalTime.of(14, 0)),
        new OpeningHoursDayDto(LocalTime.of(8, 0),
            LocalTime.of(14, 0)),
        new OpeningHoursDayDto(LocalTime.of(8, 0),
            LocalTime.of(14, 0)),
        new OpeningHoursDayDto(LocalTime.of(8, 0),
            LocalTime.of(14, 0))
    );

    private final OutpatientDepartmentDtoCreate outpatientDepartmentDtoCreate = new OutpatientDepartmentDtoCreate(
        "Outpatient Department",
        "Outpatient Department Description",
        25,
        openingHoursDtoCreate
    );

    private final OutpatientDepartmentDtoCreate outpatientDepartmentDtoCreateInvalid = new OutpatientDepartmentDtoCreate(
        "Outpatient Department",
        "Outpatient Department Description",
        25,
        null
    );


    private final OutpatientDepartment outpatientDepartment = new OutpatientDepartment()
        .setId(null)
        .setName("Outpatient Department")
        .setDescription("Outpatient Department Description")
        .setCapacity(25)
        .setOpeningHours(openingHours);

    private long testObjectId = 1L;

    @BeforeEach
    public void setUp() {
        testObjectId = outpatientDepartmentRepository.save(outpatientDepartment).getId();
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenAValidId_whenGetOutpatientDepartmentById_thenGetOutpatientDepartment() {
        OutpatientDepartmentDto outpatientDepartment = outpatientDepartmentEndpoint.getOutpatientDepartmentById(testObjectId);
        assertEquals(outpatientDepartment.id(), this.outpatientDepartment.getId());
        assertEquals(outpatientDepartment.name(), this.outpatientDepartment.getName());
        assertEquals(outpatientDepartment.description(), this.outpatientDepartment.getDescription());
        assertEquals(outpatientDepartment.capacity(), this.outpatientDepartment.getCapacity());
        assertEquals(outpatientDepartment.openingHours().monday().toString(), this.outpatientDepartment.getOpeningHours().getMonday());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenAInvalidOutpatientDepartmentDtoCreate_whenCreateOutpatientDepartment_thenMethodArgumentNotValidException() {
        assertDoesNotThrow(() -> {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/outpatient-departments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(outpatientDepartmentDtoCreateInvalid)))
                .andExpect(status().isBadRequest())
                .andReturn();
        });
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void givenAValidOutpatientDepartmentDtoCreate_whenCreateOutpatientDepartment_thenOutpatientDepartmentIsCreated() {
        assertDoesNotThrow(() -> {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/outpatient-departments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(outpatientDepartmentDtoCreate)))
                .andExpect(status().isCreated())
                .andReturn();
            OutpatientDepartmentDto outpatientDepartmentDto =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), OutpatientDepartmentDto.class);
            assertEquals(outpatientDepartmentDto.name(), outpatientDepartmentDtoCreate.name());
            assertEquals(outpatientDepartmentDto.description(), outpatientDepartmentDtoCreate.description());
            assertEquals(outpatientDepartmentDto.capacity(), outpatientDepartmentDtoCreate.capacity());
            assertEquals(outpatientDepartmentDto.openingHours().monday().toString(),
                outpatientDepartmentDtoCreate.openingHours().monday().toString());
        });
    }
}
