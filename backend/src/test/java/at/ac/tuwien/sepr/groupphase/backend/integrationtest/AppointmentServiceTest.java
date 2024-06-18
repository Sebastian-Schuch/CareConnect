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
import at.ac.tuwien.sepr.groupphase.backend.entity.Appointment;
import at.ac.tuwien.sepr.groupphase.backend.entity.Medication;
import at.ac.tuwien.sepr.groupphase.backend.entity.OpeningHours;
import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.AppointmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.OutpatientDepartmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PatientRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.AppointmentService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "datagen"})
public class AppointmentServiceTest extends TestBase {
    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    OutpatientDepartmentRepository outpatientDepartmentRepository;


    public AppointmentServiceTest() {
        super("appointment");
    }

    @Transactional
    @Test
    public void givenValidAppointmentCreateDto_whenCreateAppointment_thenCreatedAppointmentIsReturnedAndCanNowBeFound() {
        PatientDto patientDto = createPatientDto(patientRepository.findAll().get(0));

        OutpatientDepartmentDto outpatientDepartmentDto = createOutpatientDepartmentDto(outpatientDepartmentRepository.findAll().get(0));

        AppointmentDtoCreate appointmentToCreate = new AppointmentDtoCreate(patientDto, outpatientDepartmentDto, new Date(2023, 1, 1, 8, 0), new Date(2023, 1, 1, 8, 30), "notes");
        AppointmentDto createdAppointment = appointmentService.create(appointmentToCreate);

        assertAll("Grouped Assertions of Appointment",
            () -> assertEquals(appointmentToCreate.patient().id(), createdAppointment.patient().id(), "Patient ID should be equal"),
            () -> assertEquals(appointmentToCreate.outpatientDepartment().id(), createdAppointment.outpatientDepartment().id(), "Outpatient Department ID should be equal"),
            () -> assertEquals(appointmentToCreate.startDate(), createdAppointment.startDate(), "Start Date should be equal"),
            () -> assertEquals(appointmentToCreate.endDate(), createdAppointment.endDate(), "End Date should be equal"),
            () -> assertEquals(appointmentToCreate.notes(), createdAppointment.notes(), "Notes should be equal"));

        AppointmentDto foundAppointment = appointmentService.getAppointmentById(createdAppointment.id());
        assertAll("Grouped Assertions of Found Appointment",
            () -> assertEquals(createdAppointment.id(), foundAppointment.id(), "ID should be equal"),
            () -> assertEquals(createdAppointment.patient().id(), foundAppointment.patient().id(), "Patient ID should be equal"),
            () -> assertEquals(createdAppointment.outpatientDepartment().id(), foundAppointment.outpatientDepartment().id(), "Outpatient Department ID should be equal"),
            () -> assertEquals(createdAppointment.startDate(), foundAppointment.startDate(), "Start Date should be equal"),
            () -> assertEquals(createdAppointment.endDate(), foundAppointment.endDate(), "End Date should be equal"),
            () -> assertEquals(createdAppointment.notes(), foundAppointment.notes(), "Notes should be equal"));
    }

    @Transactional
    @Test
    public void givenValidAppointmentCreateDtoWithSlotFull_whenCreateAppointment_thenThrowsConflictExceptionAndAppointmentCannotBeFound() {
        List<AppointmentDto> allAppointments = appointmentService.getAllAppointments();
        long id = allAppointments.get(allAppointments.size() - 1).id() + 1;
        assertThat(appointmentRepository.existsById(id)).isFalse();

        PatientDto patientDto = createPatientDto(patientRepository.findAll().get(5));
        OutpatientDepartmentDto outpatientDepartmentDto = createOutpatientDepartmentDto(outpatientDepartmentRepository.findAll().get(0));
        AppointmentDtoCreate appointmentToCreate = new AppointmentDtoCreate(patientDto, outpatientDepartmentDto, Date.from(LocalDateTime.of(2023, Month.JANUARY, 1, 8, 0).atZone(ZoneId.systemDefault()).toInstant()),
            Date.from(LocalDateTime.of(2023, Month.JANUARY, 1, 8, 30).atZone(ZoneId.systemDefault()).toInstant()), "notes");

        assertThrows(ConflictException.class, () -> appointmentService.create(appointmentToCreate));
        assertThat(appointmentRepository.existsById(id)).isFalse();
    }

    @Transactional
    @Test
    public void givenTwoValidAppointmentsSamePatient_whenCreateAppointment_thenThrowsConflictExceptionAndSecondAppointmentCannotBeFound() {
        PatientDto patientDto = createPatientDto(patientRepository.findAll().get(0));
        OutpatientDepartmentDto outpatientDepartmentDto = createOutpatientDepartmentDto(outpatientDepartmentRepository.findAll().get(0));
        AppointmentDtoCreate appointmentToCreate = new AppointmentDtoCreate(patientDto, outpatientDepartmentDto, new Date(2023, Calendar.JANUARY, 1, 8, 0), new Date(2023, Calendar.JANUARY, 1, 8, 30), "notes");

        AppointmentDto createdAppointment = appointmentService.create(appointmentToCreate);
        assertThrows(ConflictException.class, () -> appointmentService.create(appointmentToCreate));
        assertThat(appointmentRepository.existsById(createdAppointment.id() + 1)).isFalse();
    }

    @Transactional
    @Test
    public void givenIdOfAppointment_whenGetAppointmentById_thenReturnAppointment() {
        PatientDto patientDto = createPatientDto(patientRepository.findAll().get(0));
        OutpatientDepartmentDto outpatientDepartmentDto = createOutpatientDepartmentDto(outpatientDepartmentRepository.findAll().get(0));
        AppointmentDto appointmentToFind = createAppointmentDto(patientDto, outpatientDepartmentDto, appointmentRepository.findAll().get(0));

        AppointmentDto foundAppointment = appointmentService.getAppointmentById(appointmentToFind.id());
        assertAll("Grouped Assertions of Found Appointment",
            () -> assertEquals(appointmentToFind.id(), foundAppointment.id(), "ID should be equal"),
            () -> assertEquals(appointmentToFind.patient().id(), foundAppointment.patient().id(), "Patient ID should be equal"),
            () -> assertEquals(appointmentToFind.outpatientDepartment().id(), foundAppointment.outpatientDepartment().id(), "Outpatient Department ID should be equal"),
            () -> assertEquals(appointmentToFind.startDate(), foundAppointment.startDate(), "Start Date should be equal"),
            () -> assertEquals(appointmentToFind.endDate(), foundAppointment.endDate(), "End Date should be equal"),
            () -> assertEquals(appointmentToFind.notes(), foundAppointment.notes(), "Notes should be equal"));
    }

    @Transactional
    @Test
    public void givenNonExistentIdOfAppointment_whenGetAppointmentById_thenReturnNull() {
        assertThrows(NotFoundException.class, () -> appointmentService.getAppointmentById(-1L));
    }

    @Transactional
    @Test
    public void givenIdOfAppointment_whenDeleteAppointment_thenAppointmentGetsDeletedAndCannotBeFoundAnymore() {
        PatientDto patientDto = createPatientDto(patientRepository.findAll().get(0));
        OutpatientDepartmentDto outpatientDepartmentDto = createOutpatientDepartmentDto(outpatientDepartmentRepository.findAll().get(0));
        AppointmentDto appointmentToFind =
            new AppointmentDto(appointmentRepository.getAllAppointments().get(0).getId(), patientDto, outpatientDepartmentDto, Date.from(LocalDateTime.of(2023, Month.JANUARY, 1, 8, 0).atZone(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDateTime.of(2023, Month.JANUARY, 1, 8, 30).atZone(ZoneId.systemDefault()).toInstant()), "Notes1");

        AppointmentDto foundAppointment = appointmentService.getAppointmentById(appointmentToFind.id());
        assertAll("Grouped Assertions of Found Appointment",
            () -> assertEquals(appointmentToFind.id(), foundAppointment.id(), "ID should be equal"),
            () -> assertEquals(appointmentToFind.patient().id(), foundAppointment.patient().id(), "Patient ID should be equal"),
            () -> assertEquals(appointmentToFind.outpatientDepartment().id(), foundAppointment.outpatientDepartment().id(), "Outpatient Department ID should be equal"),
            () -> assertEquals(appointmentToFind.startDate(), foundAppointment.startDate(), "Start Date should be equal"),
            () -> assertEquals(appointmentToFind.endDate(), foundAppointment.endDate(), "End Date should be equal"),
            () -> assertEquals(appointmentToFind.notes(), foundAppointment.notes(), "Notes should be equal"));

        appointmentService.delete(appointmentToFind.id());
        assertThrows(NotFoundException.class, () -> appointmentService.getAppointmentById(appointmentToFind.id()));
    }

    @Transactional
    @Test
    public void givenNonExistentId_whenDeleteAppointment_thenAppointmentCannotBeFound() {
        assertThrows(NotFoundException.class, () -> appointmentService.delete(-1L));
    }

    @Transactional
    @Test
    public void givenIdOfNonExistentAppointment_whenDeleteAppointment_thenThrowsEntityNotFoundException() {
        assertThrows(NotFoundException.class, () -> appointmentService.delete(-1L));
    }

    @Transactional
    @Test
    public void givenAppointmentSearchDto_whenGetAllAppointmentsFromOutpatientDepartmentWithOutpatientDepartmentId_thenReturnAllAppointments() {
        List<AppointmentCalendarDto> appointments =
            appointmentService.getAllAppointmentsFromStartDateToEndDateWithOutpatientDepartmentId(outpatientDepartmentRepository.findAll().get(0).getId(), "2022-01-01T00:00:00.000Z", "2022-01-02T00:00:00.000Z");
        assertThat(appointments).isNotNull().hasSize(5).extracting(AppointmentCalendarDto::getOutpatientDepartmentId, AppointmentCalendarDto::getStartDate, AppointmentCalendarDto::getEndDate, AppointmentCalendarDto::getCount)
            .contains(tuple(outpatientDepartmentRepository.findAll().get(0).getId(), Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 8, 0).atZone(ZoneId.systemDefault()).toInstant()),
                    Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 8, 30).atZone(ZoneId.systemDefault()).toInstant()), 1),
                tuple(outpatientDepartmentRepository.findAll().get(0).getId(), Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 9, 0).atZone(ZoneId.systemDefault()).toInstant()),
                    Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 9, 30).atZone(ZoneId.systemDefault()).toInstant()), 1),
                tuple(outpatientDepartmentRepository.findAll().get(0).getId(), Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 10, 0).atZone(ZoneId.systemDefault()).toInstant()),
                    Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 10, 30).atZone(ZoneId.systemDefault()).toInstant()), 1),
                tuple(outpatientDepartmentRepository.findAll().get(0).getId(), Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 11, 0).atZone(ZoneId.systemDefault()).toInstant()),
                    Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 11, 30).atZone(ZoneId.systemDefault()).toInstant()), 1),
                tuple(outpatientDepartmentRepository.findAll().get(0).getId(), Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 12, 0).atZone(ZoneId.systemDefault()).toInstant()),
                    Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 12, 30).atZone(ZoneId.systemDefault()).toInstant()), 1));
    }

    @Transactional
    @Test
    public void givenPatientId_whenGetAllAppointmentsFromPatientWithPatientId_thenReturnAllAppointments() {
        List<AppointmentDto> appointments = appointmentService.getAllAppointmentsFromPatientWithPatientId(patientRepository.findAll().get(0).getPatientId());

        assertThat(appointments)
            .isNotNull()
            .hasSize(2)
            .extracting(AppointmentDto::patient, AppointmentDto::outpatientDepartment, AppointmentDto::startDate, AppointmentDto::endDate, AppointmentDto::notes)
            .contains(
                tuple(createPatientDto(patientRepository.findAll().get(0)), createOutpatientDepartmentDto(outpatientDepartmentRepository.findAll().get(0)),
                    Date.from(LocalDateTime.of(2023, Month.JANUARY, 1, 8, 0).atZone(ZoneId.systemDefault()).toInstant()),
                    Date.from(LocalDateTime.of(2023, Month.JANUARY, 1, 8, 30).atZone(ZoneId.systemDefault()).toInstant()), "Notes1"),

                tuple(createPatientDto(patientRepository.findAll().get(0)), createOutpatientDepartmentDto(outpatientDepartmentRepository.findAll().get(0)),
                    Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 8, 0).atZone(ZoneId.systemDefault()).toInstant()),
                    Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 8, 30).atZone(ZoneId.systemDefault()).toInstant()), "Notes1"));
    }

    @Transactional
    @Test
    public void givenPatientId_whenGetAllAppointmentsFromPatientWithPatientIdWhoHasNoAppointments_thenReturnEmptyList() {
        List<AppointmentDto> appointments = appointmentService.getAllAppointmentsFromPatientWithPatientId(patientRepository.findAll().get(5).getPatientId());

        assertThat(appointments)
            .isNotNull()
            .hasSize(0);
    }

    @Transactional
    @Test
    public void givenThreeAppointmentsInDatabase_whenGetAllAppointments_thenReturnThreeAppointments() {
        List<Appointment> appointments = appointmentRepository.getAllAppointments();
        for (int i = 3; i < appointments.size(); i++) {
            appointmentRepository.deleteById(appointments.get(i).getId());
        }

        List<AppointmentDto> allAppointments = appointmentService.getAllAppointments();
        assertThat(allAppointments)
            .isNotNull()
            .hasSize(3)
            .extracting(AppointmentDto::patient, AppointmentDto::outpatientDepartment, AppointmentDto::startDate, AppointmentDto::endDate, AppointmentDto::notes)
            .contains(
                tuple(createPatientDto(patientRepository.findAll().get(0)), createOutpatientDepartmentDto(outpatientDepartmentRepository.findAll().get(0)),
                    Date.from(LocalDateTime.of(2023, Month.JANUARY, 1, 8, 0).atZone(ZoneId.systemDefault()).toInstant()),
                    Date.from(LocalDateTime.of(2023, Month.JANUARY, 1, 8, 30).atZone(ZoneId.systemDefault()).toInstant()), "Notes1"),

                tuple(createPatientDto(patientRepository.findAll().get(1)), createOutpatientDepartmentDto(outpatientDepartmentRepository.findAll().get(0)),
                    Date.from(LocalDateTime.of(2023, Month.JANUARY, 1, 8, 0).atZone(ZoneId.systemDefault()).toInstant()),
                    Date.from(LocalDateTime.of(2023, Month.JANUARY, 1, 8, 30).atZone(ZoneId.systemDefault()).toInstant()), "Notes1"),

                tuple(createPatientDto(patientRepository.findAll().get(2)), createOutpatientDepartmentDto(outpatientDepartmentRepository.findAll().get(0)),
                    Date.from(LocalDateTime.of(2023, Month.JANUARY, 1, 8, 0).atZone(ZoneId.systemDefault()).toInstant()),
                    Date.from(LocalDateTime.of(2023, Month.JANUARY, 1, 8, 30).atZone(ZoneId.systemDefault()).toInstant()), "Notes1"));
    }


    private PatientDto createPatientDto(Patient patient) {
        List<MedicationDto> medications = new ArrayList<>();
        for (Medication medication : patient.getMedicines()) {
            medications.add(new MedicationDto(medication.getId(), medication.getName(), medication.getActive()));
        }
        List<AllergyDto> allergies = new ArrayList<>();
        for (Allergy allergy : patient.getAllergies()) {
            allergies.add(new AllergyDto(allergy.getId(), allergy.getName(), allergy.isActive()));
        }
        return new PatientDto(patient.getPatientId(), patient.getSvnr(), medications, allergies, patient.getCredential().getFirstName(), patient.getCredential().getLastName(),
            patient.getCredential().getEmail(), patient.getCredential().getPassword(), patient.getCredential().isInitialPassword(), patient.getCredential().getActive());
    }

    private OutpatientDepartmentDto createOutpatientDepartmentDto(OutpatientDepartment outpatientDepartment) {
        OpeningHours openingHours = outpatientDepartment.getOpeningHours();

        OpeningHoursDayDto openingHoursDayDto = new OpeningHoursDayDto(LocalTime.of(8, 0), LocalTime.of(14, 0));
        OpeningHoursDto openingHoursDto = new OpeningHoursDto(openingHours.getId(), openingHoursDayDto, openingHoursDayDto, openingHoursDayDto, openingHoursDayDto, openingHoursDayDto, openingHoursDayDto, openingHoursDayDto);
        return new OutpatientDepartmentDto(outpatientDepartment.getId(), outpatientDepartment.getName(), outpatientDepartment.getDescription(), outpatientDepartment.getCapacity(), openingHoursDto, true);
    }

    private AppointmentDto createAppointmentDto(PatientDto patientDto, OutpatientDepartmentDto outpatientDepartmentDto, Appointment appointment) {
        return new AppointmentDto(appointment.getId(), patientDto, outpatientDepartmentDto, appointment.getStartDate(), appointment.getEndDate(), appointment.getNotes());
    }
}
