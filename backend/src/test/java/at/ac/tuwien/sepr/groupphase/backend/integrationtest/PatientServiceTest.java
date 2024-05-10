package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.PatientRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PatientServiceTest {
    @Autowired
    private PatientService patientService;

    @Autowired
    PatientRepository patientRepository;

    @BeforeEach
    void clean() {
        patientRepository.deleteAll();
    }

    @Test
    public void givenValidPatientCreateDto_whenCreatePatient_thenCreatedPatientIsReturned() {
        PatientCreateDto toCreate = new PatientCreateDto("0123456789", "a@a.a", "a", "b");
        PatientDto createdPatient = patientService.createPatient(toCreate);
        assertAll("Grouped Assertions of Patient",
            () -> assertEquals(toCreate.svnr(), createdPatient.svnr(), "SVNR should be equal"),
            () -> assertEquals(toCreate.email(), createdPatient.email(), "email should be equal"),
            () -> assertEquals(toCreate.firstname(), createdPatient.firstname(), "firstname should be equal"),
            () -> assertEquals(toCreate.lastname(), createdPatient.lastname(), "lastname should be equal"));
    }

    @Test
    public void givenIdOfNonExistentPatient_whenGetPatientById_thenNothingIsReturned() {
        assertThrows(NotFoundException.class, () -> patientService.getPatientById(-100L));
    }

    @Test
    public void givenCreatePatient_whenGetPatientById_thenPatientIsReturned() {
        PatientCreateDto toCreate = new PatientCreateDto("0123456789", "a@a.a", "a", "b");
        PatientDto createdPatient = patientService.createPatient(toCreate);
        PatientDto searchedPatient = patientService.getPatientById(createdPatient.id());
        assertAll("Grouped Assertions of Patient",
            () -> assertEquals(createdPatient.id(), searchedPatient.id(), "ID should be equal"),
            () -> assertEquals(createdPatient.svnr(), searchedPatient.svnr(), "SVNR should be equal"),
            () -> assertEquals(createdPatient.email(), searchedPatient.email(), "Email should be equal"),
            () -> assertEquals(createdPatient.firstname(), searchedPatient.firstname(), "Firstname should be equal"),
            () -> assertEquals(createdPatient.lastname(), searchedPatient.lastname(), "Lastname should be equal"),
            () -> assertEquals(createdPatient.password(), searchedPatient.password(), "Password should be equal"),
            () -> assertEquals(createdPatient.active(), searchedPatient.active(), "Active should be equal"));
    }

    @Test
    public void givenThreeCreatePatients_whenGetAllPatients_thenThreePatientsAreReturned() {
        PatientCreateDto toCreate = new PatientCreateDto("0123456789", "a@a.a", "a", "b");
        PatientDto createdPatient1 = patientService.createPatient(toCreate);
        PatientDto createdPatient2 = patientService.createPatient(toCreate);
        PatientDto createdPatient3 = patientService.createPatient(toCreate);

        List<PatientDto> allPatients = patientService.getAllPatients();
        assertAll("Grouped Assertions of all Patients",
            () -> assertEquals(3, allPatients.size()),
            () -> assertEquals(toCreate.email(), createdPatient1.email(), "Email should be equal"),
            () -> assertEquals(toCreate.email(), createdPatient2.email(), "Email should be equal"),
            () -> assertEquals(toCreate.email(), createdPatient3.email(), "Email should be equal"),
            () -> assertEquals(toCreate.svnr(), createdPatient1.svnr(), "SVNR should be equal"),
            () -> assertEquals(toCreate.svnr(), createdPatient2.svnr(), "SVNR should be equal"),
            () -> assertEquals(toCreate.svnr(), createdPatient3.svnr(), "SVNR should be equal"),
            () -> assertEquals(toCreate.firstname(), createdPatient1.firstname(), "Firstname should be equal"),
            () -> assertEquals(toCreate.firstname(), createdPatient2.firstname(), "Firstname should be equal"),
            () -> assertEquals(toCreate.firstname(), createdPatient3.firstname(), "Firstname should be equal"),
            () -> assertEquals(toCreate.lastname(), createdPatient1.lastname(), "Lastname should be equal"),
            () -> assertEquals(toCreate.lastname(), createdPatient2.lastname(), "Lastname should be equal"),
            () -> assertEquals(toCreate.lastname(), createdPatient3.lastname(), "Lastname should be equal"));
    }

    @Test
    public void givenNoPatientsInDatabase_whenGetAllPatients_thenEmptyListIsReturned() {
        List<PatientDto> allPatients = patientService.getAllPatients();
        assertEquals(0, allPatients.size());
    }
}
