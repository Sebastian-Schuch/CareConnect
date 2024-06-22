package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.TestBase;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AllergyDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AppointmentCalendarDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AppointmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AppointmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpeningHoursDayDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpeningHoursDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Allergy;
import at.ac.tuwien.sepr.groupphase.backend.entity.Medication;
import at.ac.tuwien.sepr.groupphase.backend.entity.OpeningHours;
import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import at.ac.tuwien.sepr.groupphase.backend.repository.AppointmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.OutpatientDepartmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PatientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.transaction.Transactional;
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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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
public class AppointmentEndpointTest extends TestBase {
    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_PATH = "/api/v1/appointments";
    ObjectWriter ow;

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    OutpatientDepartmentRepository outpatientDepartmentRepository;

    public AppointmentEndpointTest() {
        super("appointment");
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
    @WithMockUser(username = "admin", authorities = {"SECRETARY"})
    void givenInvalidUrl_whenSendRequest_thenReturns404NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/invalid")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    void givenValidCreateRequestAndSecretaryAuthority_whenCreateNewAppointment_thenReturnNewlyCreatedAppointmentAndAppointmentCanNowBeFound() throws Exception {
        Patient patient = patientRepository.findAll().get(0);
        List<MedicationDto> medications = new ArrayList<>();
        for (Medication medication : patient.getMedicines()) {
            medications.add(new MedicationDto(medication.getId(), medication.getName(), medication.getActive(), medication.getUnitOfMeasurement()));
        }
        List<AllergyDto> allergies = new ArrayList<>();
        for (Allergy allergy : patient.getAllergies()) {
            allergies.add(new AllergyDto(allergy.getId(), allergy.getName(), allergy.isActive()));
        }
        PatientDto patientDto = new PatientDto(patient.getPatientId(), patient.getSvnr(), medications, allergies, patient.getCredential().getFirstName(),
            patient.getCredential().getLastName(),
            patient.getCredential().getEmail(), patient.getCredential().getPassword(), patient.getCredential().isInitialPassword(),
            patient.getCredential().getActive());

        OutpatientDepartment outpatientDepartment = outpatientDepartmentRepository.findAll().get(0);
        OpeningHours openingHours = outpatientDepartment.getOpeningHours();

        OpeningHoursDayDto openingHoursDayDto = new OpeningHoursDayDto(LocalTime.of(8, 0), LocalTime.of(14, 0));
        OpeningHoursDto openingHoursDto =
            new OpeningHoursDto(openingHours.getId(), openingHoursDayDto, openingHoursDayDto, openingHoursDayDto, openingHoursDayDto, openingHoursDayDto,
                openingHoursDayDto, openingHoursDayDto);
        OutpatientDepartmentDto outpatientDepartmentDto =
            new OutpatientDepartmentDto(outpatientDepartment.getId(), outpatientDepartment.getName(), outpatientDepartment.getDescription(),
                outpatientDepartment.getCapacity(), openingHoursDto, true);

        AppointmentDtoCreate appointmentToCreate =
            new AppointmentDtoCreate(patientDto, outpatientDepartmentDto, new Date(2023, 1, 1, 8, 0), new Date(2023, 1, 1, 8, 30), "notes");
        String json = ow.writeValueAsString(appointmentToCreate);
        byte[] bodyGet = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();

        List<AppointmentDto> appointments = objectMapper.readerFor(AppointmentDto.class).<AppointmentDto>readValues(bodyGet).readAll();

        assertNotNull(appointments);
        assertThat(appointments).hasSize(1)
            .extracting(AppointmentDto::patient, AppointmentDto::outpatientDepartment, AppointmentDto::startDate, AppointmentDto::endDate,
                AppointmentDto::notes)
            .contains(tuple(patientDto, outpatientDepartmentDto, new Date(2023, 1, 1, 8, 0), new Date(2023, 1, 1, 8, 30), "notes"));

        bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/all")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        appointments = objectMapper.readerFor(AppointmentDto.class).<AppointmentDto>readValues(bodyGet).readAll();
        assertThat(appointments).extracting(AppointmentDto::patient, AppointmentDto::outpatientDepartment, AppointmentDto::startDate, AppointmentDto::endDate,
                AppointmentDto::notes)
            .contains(tuple(patientDto, outpatientDepartmentDto, new Date(2023, 1, 1, 8, 0), new Date(2023, 1, 1, 8, 30), "notes"));
    }

    @Transactional
    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    void givenValidCreateRequest_whenCreateMultipleAppointmentsForSamePatientAtSameTime_thenReturnConflictStatus() throws Exception {
        Patient patient = patientRepository.findAll().get(0);
        List<MedicationDto> medications = new ArrayList<>();
        for (Medication medication : patient.getMedicines()) {
            medications.add(new MedicationDto(medication.getId(), medication.getName(), medication.getActive(), medication.getUnitOfMeasurement()));
        }
        List<AllergyDto> allergies = new ArrayList<>();
        for (Allergy allergy : patient.getAllergies()) {
            allergies.add(new AllergyDto(allergy.getId(), allergy.getName(), allergy.isActive()));
        }
        PatientDto patientDto = new PatientDto(patient.getPatientId(), patient.getSvnr(), medications, allergies, patient.getCredential().getFirstName(),
            patient.getCredential().getLastName(),
            patient.getCredential().getEmail(), patient.getCredential().getPassword(), patient.getCredential().isInitialPassword(),
            patient.getCredential().getActive());

        OutpatientDepartment outpatientDepartment = outpatientDepartmentRepository.findAll().get(0);
        OpeningHours openingHours = outpatientDepartment.getOpeningHours();

        OpeningHoursDayDto openingHoursDayDto = new OpeningHoursDayDto(LocalTime.of(8, 0), LocalTime.of(14, 0));
        OpeningHoursDto openingHoursDto =
            new OpeningHoursDto(openingHours.getId(), openingHoursDayDto, openingHoursDayDto, openingHoursDayDto, openingHoursDayDto, openingHoursDayDto,
                openingHoursDayDto, openingHoursDayDto);
        OutpatientDepartmentDto outpatientDepartmentDto =
            new OutpatientDepartmentDto(outpatientDepartment.getId(), outpatientDepartment.getName(), outpatientDepartment.getDescription(),
                outpatientDepartment.getCapacity(), openingHoursDto, true);

        AppointmentDtoCreate appointmentToCreate =
            new AppointmentDtoCreate(patientDto, outpatientDepartmentDto, new Date(2023, 1, 1, 8, 0), new Date(2023, 1, 1, 8, 30), "notes");
        String json = ow.writeValueAsString(appointmentToCreate);
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

    @Transactional
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void givenValidCreateRequestButWrongAuthority_whenCreateNewAppointment_thenReturnForbiddenStatus() throws Exception {
        Patient patient = patientRepository.findAll().get(0);
        List<MedicationDto> medications = new ArrayList<>();
        for (Medication medication : patient.getMedicines()) {
            medications.add(new MedicationDto(medication.getId(), medication.getName(), medication.getActive(), medication.getUnitOfMeasurement()));
        }
        List<AllergyDto> allergies = new ArrayList<>();
        for (Allergy allergy : patient.getAllergies()) {
            allergies.add(new AllergyDto(allergy.getId(), allergy.getName(), allergy.isActive()));
        }
        PatientDto patientDto = new PatientDto(patient.getPatientId(), patient.getSvnr(), medications, allergies, patient.getCredential().getFirstName(),
            patient.getCredential().getLastName(),
            patient.getCredential().getEmail(), patient.getCredential().getPassword(), patient.getCredential().isInitialPassword(),
            patient.getCredential().getActive());

        OutpatientDepartment outpatientDepartment = outpatientDepartmentRepository.findAll().get(0);
        OpeningHours openingHours = outpatientDepartment.getOpeningHours();

        OpeningHoursDayDto openingHoursDayDto = new OpeningHoursDayDto(LocalTime.of(8, 0), LocalTime.of(14, 0));
        OpeningHoursDto openingHoursDto =
            new OpeningHoursDto(openingHours.getId(), openingHoursDayDto, openingHoursDayDto, openingHoursDayDto, openingHoursDayDto, openingHoursDayDto,
                openingHoursDayDto, openingHoursDayDto);
        OutpatientDepartmentDto outpatientDepartmentDto =
            new OutpatientDepartmentDto(outpatientDepartment.getId(), outpatientDepartment.getName(), outpatientDepartment.getDescription(),
                outpatientDepartment.getCapacity(), openingHoursDto, true);

        AppointmentDtoCreate appointmentToCreate =
            new AppointmentDtoCreate(patientDto, outpatientDepartmentDto, new Date(2023, 1, 1, 8, 0), new Date(2023, 1, 1, 8, 30), "notes");
        String json = ow.writeValueAsString(appointmentToCreate);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Transactional
    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    void givenAppointmentDtoCreateWithDateOutsideOpeningHours_whenCreateNewAppointment_thenReturn422UnprocessableEntity() throws Exception {
        Patient patient = patientRepository.findAll().get(0);
        List<MedicationDto> medications = new ArrayList<>();
        for (Medication medication : patient.getMedicines()) {
            medications.add(new MedicationDto(medication.getId(), medication.getName(), medication.getActive(), medication.getUnitOfMeasurement()));
        }
        List<AllergyDto> allergies = new ArrayList<>();
        for (Allergy allergy : patient.getAllergies()) {
            allergies.add(new AllergyDto(allergy.getId(), allergy.getName(), allergy.isActive()));
        }
        PatientDto patientDto = new PatientDto(patient.getPatientId(), patient.getSvnr(), medications, allergies, patient.getCredential().getFirstName(),
            patient.getCredential().getLastName(),
            patient.getCredential().getEmail(), patient.getCredential().getPassword(), patient.getCredential().isInitialPassword(),
            patient.getCredential().getActive());

        OutpatientDepartment outpatientDepartment = outpatientDepartmentRepository.findAll().get(0);
        OpeningHours openingHours = outpatientDepartment.getOpeningHours();

        OpeningHoursDayDto openingHoursDayDto = new OpeningHoursDayDto(LocalTime.of(8, 0), LocalTime.of(14, 0));
        OpeningHoursDto openingHoursDto =
            new OpeningHoursDto(openingHours.getId(), openingHoursDayDto, openingHoursDayDto, openingHoursDayDto, openingHoursDayDto, openingHoursDayDto,
                openingHoursDayDto, openingHoursDayDto);
        OutpatientDepartmentDto outpatientDepartmentDto =
            new OutpatientDepartmentDto(outpatientDepartment.getId(), outpatientDepartment.getName(), outpatientDepartment.getDescription(),
                outpatientDepartment.getCapacity(), openingHoursDto, true);

        AppointmentDtoCreate appointmentToCreate =
            new AppointmentDtoCreate(patientDto, outpatientDepartmentDto, new Date(2023, 1, 1, 7, 0), new Date(2023, 1, 1, 7, 30), "notes");
        String json = ow.writeValueAsString(appointmentToCreate);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity());
    }

    @Transactional
    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    void givenAppointmentDtoCreateWithTimeLengthLessThan30Minutes_whenCreateNewAppointment_thenReturn422UnprocessableEntity() throws Exception {
        Patient patient = patientRepository.findAll().get(0);
        List<MedicationDto> medications = new ArrayList<>();
        for (Medication medication : patient.getMedicines()) {
            medications.add(new MedicationDto(medication.getId(), medication.getName(), medication.getActive(), medication.getUnitOfMeasurement()));
        }
        List<AllergyDto> allergies = new ArrayList<>();
        for (Allergy allergy : patient.getAllergies()) {
            allergies.add(new AllergyDto(allergy.getId(), allergy.getName(), allergy.isActive()));
        }
        PatientDto patientDto = new PatientDto(patient.getPatientId(), patient.getSvnr(), medications, allergies, patient.getCredential().getFirstName(),
            patient.getCredential().getLastName(),
            patient.getCredential().getEmail(), patient.getCredential().getPassword(), patient.getCredential().isInitialPassword(),
            patient.getCredential().getActive());

        OutpatientDepartment outpatientDepartment = outpatientDepartmentRepository.findAll().get(0);
        OpeningHours openingHours = outpatientDepartment.getOpeningHours();

        OpeningHoursDayDto openingHoursDayDto = new OpeningHoursDayDto(LocalTime.of(8, 0), LocalTime.of(14, 0));
        OpeningHoursDto openingHoursDto =
            new OpeningHoursDto(openingHours.getId(), openingHoursDayDto, openingHoursDayDto, openingHoursDayDto, openingHoursDayDto, openingHoursDayDto,
                openingHoursDayDto, openingHoursDayDto);
        OutpatientDepartmentDto outpatientDepartmentDto =
            new OutpatientDepartmentDto(outpatientDepartment.getId(), outpatientDepartment.getName(), outpatientDepartment.getDescription(),
                outpatientDepartment.getCapacity(), openingHoursDto, true);

        AppointmentDtoCreate appointmentToCreate =
            new AppointmentDtoCreate(patientDto, outpatientDepartmentDto, new Date(2023, 1, 1, 8, 0), new Date(2023, 1, 1, 8, 29), "notes");
        String json = ow.writeValueAsString(appointmentToCreate);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity());
    }

    @Transactional
    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    void givenIdForAppointmentToDelete_whenDeleteAppointment_thenAppointmentCannotBeFoundAnymore() throws Exception {
        byte[] bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/all")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        List<AppointmentDto> appointments = objectMapper.readerFor(AppointmentDto.class).<AppointmentDto>readValues(bodyGet).readAll();
        AppointmentDto appointment = appointments.get(0);
        long id = appointment.id();

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + "/" + id)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/all")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        appointments = objectMapper.readerFor(AppointmentDto.class).<AppointmentDto>readValues(bodyGet).readAll();
        assertThat(appointments).extracting(AppointmentDto::id).doesNotContain(id);
    }

    @Transactional
    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    void givenValidAppointment_whenCreateNewAppointmentWithOutpatientDepartmentAtMaxCapacityForGivenDate_thenReturnConflictStatus() throws Exception {
        Patient patient = patientRepository.findAll().get(0);
        List<MedicationDto> medications = new ArrayList<>();
        for (Medication medication : patient.getMedicines()) {
            medications.add(new MedicationDto(medication.getId(), medication.getName(), medication.getActive(), medication.getUnitOfMeasurement()));
        }
        List<AllergyDto> allergies = new ArrayList<>();
        for (Allergy allergy : patient.getAllergies()) {
            allergies.add(new AllergyDto(allergy.getId(), allergy.getName(), allergy.isActive()));
        }
        PatientDto patientDto = new PatientDto(patient.getPatientId(), patient.getSvnr(), medications, allergies, patient.getCredential().getFirstName(),
            patient.getCredential().getLastName(),
            patient.getCredential().getEmail(), patient.getCredential().getPassword(), patient.getCredential().isInitialPassword(),
            patient.getCredential().getActive());

        OutpatientDepartment outpatientDepartment = outpatientDepartmentRepository.findAll().get(0);
        OpeningHours openingHours = outpatientDepartment.getOpeningHours();

        OpeningHoursDayDto openingHoursDayDto = new OpeningHoursDayDto(LocalTime.of(8, 0), LocalTime.of(14, 0));
        OpeningHoursDto openingHoursDto =
            new OpeningHoursDto(openingHours.getId(), openingHoursDayDto, openingHoursDayDto, openingHoursDayDto, openingHoursDayDto, openingHoursDayDto,
                openingHoursDayDto, openingHoursDayDto);
        OutpatientDepartmentDto outpatientDepartmentDto =
            new OutpatientDepartmentDto(outpatientDepartment.getId(), outpatientDepartment.getName(), outpatientDepartment.getDescription(),
                outpatientDepartment.getCapacity(), openingHoursDto, true);

        AppointmentDtoCreate appointmentToCreate =
            new AppointmentDtoCreate(patientDto, outpatientDepartmentDto, new Date(2023, 1, 1, 8, 0), new Date(2023, 1, 1, 8, 30), "notes");
        String json = ow.writeValueAsString(appointmentToCreate);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());

        patient = patientRepository.findAll().get(1);
        patientDto = new PatientDto(patient.getPatientId(), patient.getSvnr(), medications, allergies, patient.getCredential().getFirstName(),
            patient.getCredential().getLastName(),
            patient.getCredential().getEmail(), patient.getCredential().getPassword(), patient.getCredential().isInitialPassword(),
            patient.getCredential().getActive());
        appointmentToCreate = new AppointmentDtoCreate(patientDto, outpatientDepartmentDto, new Date(2023, 1, 1, 8, 0), new Date(2023, 1, 1, 8, 30), "notes");
        json = ow.writeValueAsString(appointmentToCreate);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());

        patient = patientRepository.findAll().get(2);
        patientDto = new PatientDto(patient.getPatientId(), patient.getSvnr(), medications, allergies, patient.getCredential().getFirstName(),
            patient.getCredential().getLastName(),
            patient.getCredential().getEmail(), patient.getCredential().getPassword(), patient.getCredential().isInitialPassword(),
            patient.getCredential().getActive());
        appointmentToCreate = new AppointmentDtoCreate(patientDto, outpatientDepartmentDto, new Date(2023, 1, 1, 8, 0), new Date(2023, 1, 1, 8, 30), "notes");
        json = ow.writeValueAsString(appointmentToCreate);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());

        patient = patientRepository.findAll().get(3);
        patientDto = new PatientDto(patient.getPatientId(), patient.getSvnr(), medications, allergies, patient.getCredential().getFirstName(),
            patient.getCredential().getLastName(),
            patient.getCredential().getEmail(), patient.getCredential().getPassword(), patient.getCredential().isInitialPassword(),
            patient.getCredential().getActive());
        appointmentToCreate = new AppointmentDtoCreate(patientDto, outpatientDepartmentDto, new Date(2023, 1, 1, 8, 0), new Date(2023, 1, 1, 8, 30), "notes");
        json = ow.writeValueAsString(appointmentToCreate);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());
    }

    @Transactional
    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    void givenPatientId_whenGetCertainPatientsAppointmentsWithPatientId_thenReturnAllAppointments() throws Exception {
        Patient patient = patientRepository.findAll().get(5);
        List<MedicationDto> medications = new ArrayList<>();
        for (Medication medication : patient.getMedicines()) {
            medications.add(new MedicationDto(medication.getId(), medication.getName(), medication.getActive(), medication.getUnitOfMeasurement()));
        }
        List<AllergyDto> allergies = new ArrayList<>();
        for (Allergy allergy : patient.getAllergies()) {
            allergies.add(new AllergyDto(allergy.getId(), allergy.getName(), allergy.isActive()));
        }
        PatientDto patientDto = new PatientDto(patient.getPatientId(), patient.getSvnr(), medications, allergies, patient.getCredential().getFirstName(),
            patient.getCredential().getLastName(),
            patient.getCredential().getEmail(), patient.getCredential().getPassword(), patient.getCredential().isInitialPassword(),
            patient.getCredential().getActive());

        OutpatientDepartment outpatientDepartment = outpatientDepartmentRepository.findAll().get(0);
        OpeningHours openingHours = outpatientDepartment.getOpeningHours();

        OpeningHoursDayDto openingHoursDayDto = new OpeningHoursDayDto(LocalTime.of(8, 0), LocalTime.of(14, 0));
        OpeningHoursDto openingHoursDto =
            new OpeningHoursDto(openingHours.getId(), openingHoursDayDto, openingHoursDayDto, openingHoursDayDto, openingHoursDayDto, openingHoursDayDto,
                openingHoursDayDto, openingHoursDayDto);
        OutpatientDepartmentDto outpatientDepartmentDto =
            new OutpatientDepartmentDto(outpatientDepartment.getId(), outpatientDepartment.getName(), outpatientDepartment.getDescription(),
                outpatientDepartment.getCapacity(), openingHoursDto, true);

        byte[] bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/patients/" + patient.getPatientId())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        List<AppointmentDto> appointments = objectMapper.readerFor(AppointmentDto.class).<AppointmentDto>readValues(bodyGet).readAll();
        assertThat(appointments).hasSize(0);

        AppointmentDtoCreate appointmentToCreate1 =
            new AppointmentDtoCreate(patientDto, outpatientDepartmentDto, new Date(2023, 1, 1, 8, 0), new Date(2023, 1, 1, 8, 30), "notes");
        String json = ow.writeValueAsString(appointmentToCreate1);
        bodyGet = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();

        List<AppointmentDto> createdAppointment1 = objectMapper.readerFor(AppointmentDto.class).<AppointmentDto>readValues(bodyGet).readAll();

        AppointmentDtoCreate appointmentToCreate2 =
            new AppointmentDtoCreate(patientDto, outpatientDepartmentDto, new Date(2023, 1, 1, 8, 30), new Date(2023, 1, 1, 9, 0), "notes");
        json = ow.writeValueAsString(appointmentToCreate2);
        bodyGet = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();

        List<AppointmentDto> createdAppointment2 = objectMapper.readerFor(AppointmentDto.class).<AppointmentDto>readValues(bodyGet).readAll();

        AppointmentDtoCreate appointmentToCreate3 =
            new AppointmentDtoCreate(patientDto, outpatientDepartmentDto, new Date(2023, 1, 1, 9, 0), new Date(2023, 1, 1, 9, 30), "notes");
        json = ow.writeValueAsString(appointmentToCreate3);
        bodyGet = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();

        List<AppointmentDto> createdAppointment3 = objectMapper.readerFor(AppointmentDto.class).<AppointmentDto>readValues(bodyGet).readAll();

        bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/patients/" + patient.getPatientId())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        appointments = objectMapper.readerFor(AppointmentDto.class).<AppointmentDto>readValues(bodyGet).readAll();
        assertThat(appointments).isNotNull().hasSize(3).contains(createdAppointment1.get(0), createdAppointment2.get(0), createdAppointment3.get(0));
    }


    @Transactional
    @Test
    @WithMockUser(username = "secretary", authorities = {"SECRETARY"})
    void givenAppointmentSearchDto_whenGetAllAppointmentsFromOutpatientDepartmentWithOutpatientDepartmentId_thenReturnAllAppointmentsForThatOutpatientDepartment()
        throws Exception {
        byte[] bodyGet = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH)
                .queryParam("outpatientDepartmentId", "" + outpatientDepartmentRepository.findAll().get(0).getId())
                .queryParam("startDate", "2022-01-01T00:00:00.000Z")
                .queryParam("endDate", "2022-01-02T00:00:00.000Z")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        List<AppointmentCalendarDto> appointments = objectMapper.readerFor(AppointmentCalendarDto.class).<AppointmentCalendarDto>readValues(bodyGet).readAll();
        assertThat(appointments).isNotNull().hasSize(5)
            .extracting(AppointmentCalendarDto::getOutpatientDepartmentId, AppointmentCalendarDto::getStartDate, AppointmentCalendarDto::getEndDate,
                AppointmentCalendarDto::getCount)
            .contains(tuple(outpatientDepartmentRepository.findAll().get(0).getId(),
                    Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 8, 0).atZone(ZoneId.systemDefault()).toInstant()),
                    Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 8, 30).atZone(ZoneId.systemDefault()).toInstant()), 1),
                tuple(outpatientDepartmentRepository.findAll().get(0).getId(),
                    Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 9, 0).atZone(ZoneId.systemDefault()).toInstant()),
                    Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 9, 30).atZone(ZoneId.systemDefault()).toInstant()), 1),
                tuple(outpatientDepartmentRepository.findAll().get(0).getId(),
                    Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 10, 0).atZone(ZoneId.systemDefault()).toInstant()),
                    Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 10, 30).atZone(ZoneId.systemDefault()).toInstant()), 1),
                tuple(outpatientDepartmentRepository.findAll().get(0).getId(),
                    Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 11, 0).atZone(ZoneId.systemDefault()).toInstant()),
                    Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 11, 30).atZone(ZoneId.systemDefault()).toInstant()), 1),
                tuple(outpatientDepartmentRepository.findAll().get(0).getId(),
                    Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 12, 0).atZone(ZoneId.systemDefault()).toInstant()),
                    Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 12, 30).atZone(ZoneId.systemDefault()).toInstant()), 1));
    }
}
