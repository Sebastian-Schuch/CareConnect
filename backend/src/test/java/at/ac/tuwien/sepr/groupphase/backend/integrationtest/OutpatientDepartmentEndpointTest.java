package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.TestBase;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.OutpatientDepartmentEndpoint;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpeningHoursDayDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpeningHoursDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentCapacityDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.entity.Appointment;
import at.ac.tuwien.sepr.groupphase.backend.entity.OpeningHours;
import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import at.ac.tuwien.sepr.groupphase.backend.repository.AppointmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.OutpatientDepartmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PatientRepository;
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

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "datagen"})
@AutoConfigureMockMvc
public class OutpatientDepartmentEndpointTest extends TestBase {

    @Autowired
    private OutpatientDepartmentEndpoint outpatientDepartmentEndpoint;

    @Autowired
    private OutpatientDepartmentRepository outpatientDepartmentRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Date futureDate;

    private final OpeningHours openingHours = new OpeningHours()
        .setId(null)
        .setMonday("08:00-14:00")
        .setTuesday("08:00-14:00")
        .setWednesday("08:00-14:00")
        .setThursday("08:00-14:00")
        .setFriday("08:00-14:00")
        .setSaturday("08:00-14:00")
        .setSunday("08:00-14:00");

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
        .setOpeningHours(openingHours)
        .setActive(true);

    private long testObjectId = 1L;

    public OutpatientDepartmentEndpointTest() {
        super("outpatientDepartment");
    }

    @BeforeEach
    public void setUp() {
        testObjectId = outpatientDepartmentRepository.save(outpatientDepartment).getId();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.YEAR, 10);
        futureDate = cal.getTime();


        for (int i = 0; i < 10; i++) {
            Appointment ap = new Appointment();
            ap.setStartDate(new Date(futureDate.getTime() + (60 * 60 * 1000L) + (24 * 60 * 60 * 1000L) * i));
            ap.setEndDate(new Date(futureDate.getTime() + (60 * 60 * 1000L) + ((24 * 60 * 60 * 1000L) * i + 30 * 60 * 1000L)));
            ap.setPatient(patientRepository.findAll().getFirst());
            ap.setOutpatientDepartment(outpatientDepartmentRepository.getReferenceById(testObjectId));
            ap.setNotes("Test");
            appointmentRepository.save(ap);
        }
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
                .andExpect(status().isUnprocessableEntity())
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

    @Test
    @WithMockUser(authorities = {"SECRETARY"})
    public void givenValidDate_whenGetOutpatientDepartmentCapacitiesForDay_thenReturnCapacities() throws Exception {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(futureDate);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/outpatient-departments/capacities/day")
                .param("date", date)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        List<OutpatientDepartmentCapacityDto> capacities = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, OutpatientDepartmentCapacityDto.class));
        assertEquals(4, capacities.size());

        OutpatientDepartmentCapacityDto capacity = null;
        for (OutpatientDepartmentCapacityDto cap : capacities) {
            if (cap.outpatientDepartment().id() == testObjectId) {
                capacity = cap;
                break;
            }
        }
        assertNotNull(capacity);

        // 12 = 6h open x 2 slots/h
        assertEquals(outpatientDepartment.getCapacity() * 12, capacity.capacityDto().capacity());
        assertEquals(1, capacity.capacityDto().occupied());
    }

    @Test
    @WithMockUser(authorities = {"SECRETARY"})
    public void givenValidStartDate_whenGetOutpatientDepartmentCapacitiesForWeek_thenReturnCapacities() throws Exception {
        String startDate = new SimpleDateFormat("yyyy-MM-dd").format(futureDate);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/outpatient-departments/capacities/week")
                .param("startDate", startDate)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        List<OutpatientDepartmentCapacityDto> capacities = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, OutpatientDepartmentCapacityDto.class));
        assertEquals(4, capacities.size());

        OutpatientDepartmentCapacityDto capacity = null;
        for (OutpatientDepartmentCapacityDto cap : capacities) {
            if (cap.outpatientDepartment().id() == testObjectId) {
                capacity = cap;
                break;
            }
        }
        assertNotNull(capacity);

        assertEquals(outpatientDepartment.getCapacity() * 12 * 7, capacity.capacityDto().capacity());
        assertEquals(7, capacity.capacityDto().occupied());
    }

    @Test
    @WithMockUser(authorities = {"SECRETARY"})
    public void givenValidDate_whenGetOutpatientDepartmentCapacitiesForMonth_thenReturnCapacities() throws Exception {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(futureDate);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/outpatient-departments/capacities/month")
                .param("date", date)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        List<OutpatientDepartmentCapacityDto> capacities = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, OutpatientDepartmentCapacityDto.class));
        assertEquals(4, capacities.size());

        OutpatientDepartmentCapacityDto capacity = null;
        for (OutpatientDepartmentCapacityDto cap : capacities) {
            if (cap.outpatientDepartment().id() == testObjectId) {
                capacity = cap;
                break;
            }
        }
        assertNotNull(capacity);

        assertEquals(9000, capacity.capacityDto().capacity());
        assertEquals(10, capacity.capacityDto().occupied());
    }
}
