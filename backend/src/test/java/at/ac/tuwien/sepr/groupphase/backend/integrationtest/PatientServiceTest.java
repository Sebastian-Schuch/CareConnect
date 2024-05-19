package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.PatientRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.junit.jupiter.api.AfterEach;
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

    @AfterEach
    void clear() {
        patientRepository.deleteAll();
    }

    @Test
    public void givenValidPatientCreateDto_whenCreatePatient_thenCreatedPatientIsReturned() {
        PatientCreateDto toCreate = new PatientCreateDto("0123456789", "a@a.a", "a", "b");
        Credential credential = new Credential();
        credential.setEmail("a@a.a");
        credential.setActive(true);
        credential.setFirstName("a");
        credential.setLastName("b");
        credential.setPassword("password");
        credential.setInitialPassword(false);
        credential.setRole(Role.PATIENT);
        PatientDto createdPatient = patientService.createPatient(toCreate, credential);
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
        Credential credential = new Credential();
        credential.setEmail("a@a.a");
        credential.setActive(true);
        credential.setFirstName("a");
        credential.setLastName("b");
        credential.setPassword("password");
        credential.setInitialPassword(false);
        credential.setRole(Role.PATIENT);
        PatientDto createdPatient = patientService.createPatient(toCreate, credential);
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
        PatientCreateDto toCreate1 = new PatientCreateDto("0123456789", "a@a.a", "a", "b");
        Credential credential1 = new Credential();
        credential1.setEmail("a@a.a");
        credential1.setActive(true);
        credential1.setFirstName("a");
        credential1.setLastName("b");
        credential1.setPassword("password");
        credential1.setInitialPassword(false);
        credential1.setRole(Role.PATIENT);
        PatientDto createdPatient1 = patientService.createPatient(toCreate1, credential1);
        PatientCreateDto toCreate2 = new PatientCreateDto("0123456789", "b@b.b", "a", "b");
        Credential credential2 = new Credential();
        credential2.setEmail("b@b.b");
        credential2.setActive(true);
        credential2.setFirstName("a");
        credential2.setLastName("b");
        credential2.setPassword("password");
        credential2.setInitialPassword(false);
        credential2.setRole(Role.PATIENT);
        PatientDto createdPatient2 = patientService.createPatient(toCreate2, credential2);
        PatientCreateDto toCreate3 = new PatientCreateDto("0123456789", "c@c.c", "a", "b");
        Credential credential3 = new Credential();
        credential3.setEmail("c@c.c");
        credential3.setActive(true);
        credential3.setFirstName("a");
        credential3.setLastName("b");
        credential3.setPassword("password");
        credential3.setInitialPassword(false);
        credential3.setRole(Role.PATIENT);
        PatientDto createdPatient3 = patientService.createPatient(toCreate3, credential3);

        List<PatientDto> allPatients = patientService.getAllPatients();
        assertAll("Grouped Assertions of all Patients",
            () -> assertEquals(3, allPatients.size()),
            () -> assertEquals(toCreate1.email(), createdPatient1.email(), "Email should be equal"),
            () -> assertEquals(toCreate2.email(), createdPatient2.email(), "Email should be equal"),
            () -> assertEquals(toCreate3.email(), createdPatient3.email(), "Email should be equal"),
            () -> assertEquals(toCreate1.svnr(), createdPatient1.svnr(), "SVNR should be equal"),
            () -> assertEquals(toCreate2.svnr(), createdPatient2.svnr(), "SVNR should be equal"),
            () -> assertEquals(toCreate3.svnr(), createdPatient3.svnr(), "SVNR should be equal"),
            () -> assertEquals(toCreate1.firstname(), createdPatient1.firstname(), "Firstname should be equal"),
            () -> assertEquals(toCreate2.firstname(), createdPatient2.firstname(), "Firstname should be equal"),
            () -> assertEquals(toCreate3.firstname(), createdPatient3.firstname(), "Firstname should be equal"),
            () -> assertEquals(toCreate1.lastname(), createdPatient1.lastname(), "Lastname should be equal"),
            () -> assertEquals(toCreate2.lastname(), createdPatient2.lastname(), "Lastname should be equal"),
            () -> assertEquals(toCreate3.lastname(), createdPatient3.lastname(), "Lastname should be equal"));
    }

    @Test
    public void givenNoPatientsInDatabase_whenGetAllPatients_thenEmptyListIsReturned() {
        List<PatientDto> allPatients = patientService.getAllPatients();
        assertEquals(0, allPatients.size());
    }
}
