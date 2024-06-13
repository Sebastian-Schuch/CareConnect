package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.TestBase;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AllergyDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoUpdate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.PatientRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "datagen"})
public class PatientServiceTest extends TestBase {
    @Autowired
    private PatientService patientService;

    @Autowired
    PatientRepository patientRepository;

    public PatientServiceTest() {
        super("patient");
    }

    @Transactional
    @Test
    public void givenValidPatientCreateDto_whenCreatePatient_thenCreatedPatientIsReturned() {
        List<MedicationDto> medications = new ArrayList<>();
        List<AllergyDto> allergies = new ArrayList<>();
        PatientDtoCreate toCreate = new PatientDtoCreate("1234123456", "a@a.a", "a", "b", medications, allergies);
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

    @Transactional
    @Test
    public void givenIdOfPatient_whenGetPatientById_thenPatientIsReturned() {
        Patient patient = patientRepository.findAll().get(0);
        List<MedicationDto> medications = new ArrayList<>();
        List<AllergyDto> allergies = new ArrayList<>();
        PatientDto toBeFoundPatient = new PatientDto(patient.getPatientId(), patient.getSvnr(), medications, allergies,
            patient.getCredential().getFirstName(), patient.getCredential().getLastName(), patient.getCredential().getEmail(), patient.getCredential().getPassword(), patient.getCredential().isInitialPassword(),
            patient.getCredential().getActive());
        PatientDtoSparse foundPatient = patientService.getPatientById(toBeFoundPatient.id());
        assertAll("Grouped Assertions of Patient",
            () -> assertEquals(toBeFoundPatient.id(), foundPatient.id(), "ID should be equal"),
            () -> assertEquals(toBeFoundPatient.email(), foundPatient.email(), "Email should be equal"),
            () -> assertEquals(toBeFoundPatient.firstname(), foundPatient.firstname(), "Firstname should be equal"),
            () -> assertEquals(toBeFoundPatient.lastname(), foundPatient.lastname(), "Lastname should be equal"),
            () -> assertEquals(toBeFoundPatient.svnr(), foundPatient.svnr(), "Svnr should be equal"),
            () -> assertEquals(toBeFoundPatient.isInitialPassword(), foundPatient.isInitialPassword(), "InitialPassword should be equal"));
    }

    @Transactional
    @Test
    public void givenPatientDtoUpdate_whenUpdatePatient_thenPatientGetsUpdatedAndDataHasBeenUpdated() {
        long id = patientRepository.findAll().get(0).getPatientId();
        List<MedicationDto> medications = new ArrayList<>();
        List<AllergyDto> allergies = new ArrayList<>();
        PatientDtoUpdate updatePatient = new PatientDtoUpdate("0000000000", medications, allergies, "x", "y", "a@a.a", false, false);

        PatientDtoSparse foundPatient = patientService.getPatientById(id);
        PatientDtoSparse finalFoundPatient1 = foundPatient;

        assertAll("Grouped Assertions of Patient",
            () -> assertNotEquals(updatePatient.email(), finalFoundPatient1.email(), "Email shouldn't be equal"),
            () -> assertNotEquals(updatePatient.svnr(), finalFoundPatient1.svnr(), "Svnr shouldn't be equal"),
            () -> assertNotEquals(updatePatient.firstname(), finalFoundPatient1.firstname(), "Firstname shouldn't be equal"),
            () -> assertNotEquals(updatePatient.lastname(), finalFoundPatient1.lastname(), "Lastname shouldn't be equal"));

        PatientDtoSparse updatedPatient = patientService.updatePatient(id, updatePatient);
        assertAll("Grouped Assertions of Patient",
            () -> assertEquals(updatePatient.email(), updatedPatient.email(), "Email should be equal"),
            () -> assertEquals(updatePatient.svnr(), updatedPatient.svnr(), "Svnr shouldn't be equal"),
            () -> assertEquals(updatePatient.firstname(), updatedPatient.firstname(), "Firstname should be equal"),
            () -> assertEquals(updatePatient.lastname(), updatedPatient.lastname(), "Lastname should be equal"));

        foundPatient = patientService.getPatientById(id);
        PatientDtoSparse finalFoundPatient = foundPatient;
        assertAll("Grouped Assertions of Patient",
            () -> assertEquals(updatePatient.email(), finalFoundPatient.email(), "Email should be equal"),
            () -> assertEquals(updatePatient.svnr(), finalFoundPatient.svnr(), "Svnr shouldn't be equal"),
            () -> assertEquals(updatePatient.firstname(), finalFoundPatient.firstname(), "Firstname should be equal"),
            () -> assertEquals(updatePatient.lastname(), finalFoundPatient.lastname(), "Lastname should be equal"));
    }

    @Transactional
    @Test
    public void givenPatientDtoUpdateWithNonExistentId_whenUpdatePatient_thenThrowsNotFoundAndPatientDoesntGetUpdated() {
        long id = patientRepository.findAll().get(0).getPatientId();
        List<MedicationDto> medications = new ArrayList<>();
        List<AllergyDto> allergies = new ArrayList<>();
        PatientDtoUpdate updatePatient = new PatientDtoUpdate("0000000000", medications, allergies, "x", "y", "a@a.a", false, false);


        PatientDtoSparse foundPatient = patientService.getPatientById(id);
        PatientDtoSparse finalFoundPatient1 = foundPatient;

        assertAll("Grouped Assertions of Patient",
            () -> assertNotEquals(updatePatient.email(), finalFoundPatient1.email(), "Email shouldn't be equal"),
            () -> assertNotEquals(updatePatient.svnr(), finalFoundPatient1.svnr(), "Svnr shouldn't be equal"),
            () -> assertNotEquals(updatePatient.firstname(), finalFoundPatient1.firstname(), "Firstname shouldn't be equal"),
            () -> assertNotEquals(updatePatient.lastname(), finalFoundPatient1.lastname(), "Lastname shouldn't be equal"));

        assertThrows(NotFoundException.class, () -> patientService.updatePatient(-1L, updatePatient));

        foundPatient = patientService.getPatientById(id);
        PatientDtoSparse finalFoundPatient = foundPatient;
        assertAll("Grouped Assertions of Patient",
            () -> assertNotEquals(updatePatient.email(), finalFoundPatient.email(), "Email should be equal"),
            () -> assertNotEquals(updatePatient.svnr(), finalFoundPatient.svnr(), "Svnr shouldn't be equal"),
            () -> assertNotEquals(updatePatient.firstname(), finalFoundPatient.firstname(), "Firstname should be equal"),
            () -> assertNotEquals(updatePatient.lastname(), finalFoundPatient.lastname(), "Lastname should be equal"));
    }

    @Transactional
    @Test
    public void givenIdOfNonExistentPatient_whenGetPatientById_thenThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> patientService.getPatientById(-100L));
    }

    @Transactional
    @Test
    public void givenThreeNewlyCreatePatients_whenGetAllPatients_thenThreePatientsAreReturned() {
        patientRepository.deleteAll();
        List<MedicationDto> medications = new ArrayList<>();
        List<AllergyDto> allergies = new ArrayList<>();
        PatientDtoCreate toCreate1 = new PatientDtoCreate("1234123456", "a@a.a", "a", "b", medications, allergies);
        Credential credential1 = new Credential();
        credential1.setEmail("a@a.a");
        credential1.setActive(true);
        credential1.setFirstName("a");
        credential1.setLastName("b");
        credential1.setPassword("password");
        credential1.setInitialPassword(false);
        credential1.setRole(Role.PATIENT);
        PatientDto createdPatient1 = patientService.createPatient(toCreate1, credential1);

        PatientDtoCreate toCreate2 = new PatientDtoCreate("1234123456", "b@a.a", "a", "b", medications, allergies);
        Credential credential2 = new Credential();
        credential2.setEmail("b@a.a");
        credential2.setActive(true);
        credential2.setFirstName("a");
        credential2.setLastName("b");
        credential2.setPassword("password");
        credential2.setInitialPassword(false);
        credential2.setRole(Role.PATIENT);
        PatientDto createdPatient2 = patientService.createPatient(toCreate2, credential2);

        PatientDtoCreate toCreate3 = new PatientDtoCreate("1234123456", "c@a.a", "a", "b", medications, allergies);
        Credential credential3 = new Credential();
        credential3.setEmail("c@a.a");
        credential3.setActive(true);
        credential3.setFirstName("a");
        credential3.setLastName("b");
        credential3.setPassword("password");
        credential3.setInitialPassword(false);
        credential3.setRole(Role.PATIENT);
        PatientDto createdPatient3 = patientService.createPatient(toCreate3, credential3);

        List<PatientDtoSparse> allPatients = patientService.getAllPatients();
        assertAll("Grouped Assertions of all Patients",
            () -> assertEquals(3, allPatients.size()),
            () -> assertEquals(toCreate1.email(), createdPatient1.email(), "Email should be equal"),
            () -> assertEquals(toCreate2.email(), createdPatient2.email(), "Email should be equal"),
            () -> assertEquals(toCreate3.email(), createdPatient3.email(), "Email should be equal"),
            () -> assertEquals(toCreate1.firstname(), createdPatient1.firstname(), "Firstname should be equal"),
            () -> assertEquals(toCreate2.firstname(), createdPatient2.firstname(), "Firstname should be equal"),
            () -> assertEquals(toCreate3.firstname(), createdPatient3.firstname(), "Firstname should be equal"),
            () -> assertEquals(toCreate1.lastname(), createdPatient1.lastname(), "Lastname should be equal"),
            () -> assertEquals(toCreate2.lastname(), createdPatient2.lastname(), "Lastname should be equal"),
            () -> assertEquals(toCreate3.lastname(), createdPatient3.lastname(), "Lastname should be equal"),
            () -> assertEquals(toCreate1.svnr(), createdPatient1.svnr(), "Svnr should be equal"),
            () -> assertEquals(toCreate2.svnr(), createdPatient2.svnr(), "Svnr should be equal"),
            () -> assertEquals(toCreate3.svnr(), createdPatient3.svnr(), "Svnr should be equal"));
    }

    @Transactional
    @Test
    public void givenNoPatientsInDatabase_whenGetAllPatients_thenEmptyListIsReturned() {
        patientRepository.deleteAll();
        List<PatientDtoSparse> allPatients = patientService.getAllPatients();
        assertThat(allPatients).isEmpty();
    }

    @Transactional
    @Test
    public void givenIdOfPatient_whenGetPatientEntityById_thenPatientEntityIsReturned() {
        Patient toBeFoundPatient = patientRepository.findAll().get(0);
        Patient foundPatient = patientService.getPatientEntityById(toBeFoundPatient.getPatientId());
        assertAll("Grouped Assertions of Patient",
            () -> assertEquals(toBeFoundPatient.getPatientId(), foundPatient.getPatientId(), "ID should be equal"),
            () -> assertEquals(toBeFoundPatient.getCredential().getEmail(), foundPatient.getCredential().getEmail(), "Email should be equal"),
            () -> assertEquals(toBeFoundPatient.getCredential().getFirstName(), foundPatient.getCredential().getFirstName(), "Firstname should be equal"),
            () -> assertEquals(toBeFoundPatient.getSvnr(), foundPatient.getSvnr(), "Svnr should be equal"),
            () -> assertEquals(toBeFoundPatient.getCredential().getLastName(), foundPatient.getCredential().getLastName(), "Lastname should be equal"),
            () -> assertEquals(toBeFoundPatient.getCredential().getPassword(), foundPatient.getCredential().getPassword(), "Password should be equal"),
            () -> assertEquals(toBeFoundPatient.getCredential().getActive(), foundPatient.getCredential().getActive(), "Active should be equal"),
            () -> assertEquals(toBeFoundPatient.getCredential().isInitialPassword(), foundPatient.getCredential().isInitialPassword(), "InitialPassword should be equal"));
    }


    @Transactional
    @Test
    public void givenNonExistentIdOfPatient_whenGetPatientEntityById_thenThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> patientService.getPatientEntityById(-1L));
    }

    @Transactional
    @Test
    public void givenEmailOfPatient_whenGetPatientByEmail_thenPatientIsReturned() {
        long id = patientRepository.findAll().get(0).getPatientId();
        List<MedicationDto> medications = new ArrayList<>();
        List<AllergyDto> allergies = new ArrayList<>();
        PatientDto toBeFoundPatient = new PatientDto(id, "6912120520", medications, allergies, "Chris", "Anger", "chris.anger@email.com", "AngerManagement", false, true);
        PatientDto foundPatient = patientService.getPatientByEmail(toBeFoundPatient.email());
        assertAll("Grouped Assertions of Patient",
            () -> assertEquals(toBeFoundPatient.id(), foundPatient.id(), "ID should be equal"),
            () -> assertEquals(toBeFoundPatient.email(), foundPatient.email(), "Email should be equal"),
            () -> assertEquals(toBeFoundPatient.firstname(), foundPatient.firstname(), "Firstname should be equal"),
            () -> assertEquals(toBeFoundPatient.svnr(), foundPatient.svnr(), "Svnr should be equal"),
            () -> assertEquals(toBeFoundPatient.lastname(), foundPatient.lastname(), "Lastname should be equal"),
            () -> assertEquals(toBeFoundPatient.password(), foundPatient.password(), "Password should be equal"),
            () -> assertEquals(toBeFoundPatient.active(), foundPatient.active(), "Active should be equal"),
            () -> assertEquals(toBeFoundPatient.isInitialPassword(), foundPatient.isInitialPassword(), "InitialPassword should be equal"));
    }

    @Transactional
    @Test
    public void givenCredentialOfPatient_whenFindPatientByCredential_thenCredentialIsReturned() {
        long id = patientRepository.findAll().get(0).getPatientId();
        List<MedicationDto> medications = new ArrayList<>();
        List<AllergyDto> allergies = new ArrayList<>();
        PatientDto toBeFoundPatient = new PatientDto(id, "6912120520", medications, allergies, "Chris", "Anger", "chris.anger@email.com", "AngerManagement", false, true);
        PatientDto foundPatient = patientService.findPatientByCredential(patientRepository.findAll().get(0).getCredential());
        assertAll("Grouped Assertions of Patient",
            () -> assertEquals(toBeFoundPatient.id(), foundPatient.id(), "ID should be equal"),
            () -> assertEquals(toBeFoundPatient.email(), foundPatient.email(), "Email should be equal"),
            () -> assertEquals(toBeFoundPatient.firstname(), foundPatient.firstname(), "Firstname should be equal"),
            () -> assertEquals(toBeFoundPatient.lastname(), foundPatient.lastname(), "Lastname should be equal"),
            () -> assertEquals(toBeFoundPatient.svnr(), foundPatient.svnr(), "Svnr should be equal"),
            () -> assertEquals(toBeFoundPatient.password(), foundPatient.password(), "Password should be equal"),
            () -> assertEquals(toBeFoundPatient.active(), foundPatient.active(), "Active should be equal"),
            () -> assertEquals(toBeFoundPatient.isInitialPassword(), foundPatient.isInitialPassword(), "InitialPassword should be equal"));
    }

    @Transactional
    @Test
    public void givenNonExistentCredential_whenFindPatientByCredential_thenThrowsNotFoundException() {
        Credential credential = patientRepository.findAll().get(0).getCredential();
        patientRepository.deleteById(patientRepository.findAll().get(0).getPatientId());
        assertThrows(NotFoundException.class, () -> patientService.findPatientByCredential(credential));
    }

    @Transactional
    @Test
    public void givenUserDtoSearch_whenSearchPatients_thenReturnFoundPatients() {
        long id = patientRepository.findAll().get(0).getPatientId();
        List<PatientDto> toBeFoundPatient = new ArrayList<>();
        List<MedicationDto> medications = new ArrayList<>();
        List<AllergyDto> allergies = new ArrayList<>();
        toBeFoundPatient.add(new PatientDto(id, "6912120520", medications, allergies, "Chris", "Anger", "chris.anger@email.com", "AngerManagement", false, true));

        UserDtoSearch search = new UserDtoSearch("chris.anger@email.com", "Chris", "Anger");
        List<PatientDtoSparse> foundPatient = patientService.searchPatients(search);
        assertThat(foundPatient).isNotNull().hasSize(1);
        assertAll("Grouped Assertions of Patient",
            () -> assertEquals(toBeFoundPatient.get(0).id(), foundPatient.get(0).id(), "ID should be equal"),
            () -> assertEquals(toBeFoundPatient.get(0).email(), foundPatient.get(0).email(), "Email should be equal"),
            () -> assertEquals(toBeFoundPatient.get(0).firstname(), foundPatient.get(0).firstname(), "Firstname should be equal"),
            () -> assertEquals(toBeFoundPatient.get(0).lastname(), foundPatient.get(0).lastname(), "Lastname should be equal"),
            () -> assertEquals(toBeFoundPatient.get(0).svnr(), foundPatient.get(0).svnr(), "Svnr should be equal"),
            () -> assertEquals(toBeFoundPatient.get(0).isInitialPassword(), foundPatient.get(0).isInitialPassword(), "InitialPassword should be equal"));
    }

    @Transactional
    @Test
    public void givenUserDtoSearchWithNonExistentName_whenSearchPatients_thenReturnsEmptyList() {
        UserDtoSearch search = new UserDtoSearch("x.x@x.x", "x", "x");
        List<PatientDtoSparse> foundPatient = patientService.searchPatients(search);
        assertThat(foundPatient).isNotNull().hasSize(0);
    }

    @Transactional
    @Test
    @WithMockUser(username = "chris.anger@email.com", authorities = {"PATIENT"})
    public void givenRightUsername_whenIsOwnRequest_thenReturnsTrue() {
        long id = patientRepository.findAll().get(0).getPatientId();
        boolean isOwnRequest = patientService.isOwnRequest(id);
        assertThat(isOwnRequest).isTrue();
    }

    @Transactional
    @Test
    @WithMockUser(username = "Patient", authorities = {"PATIENT"})
    public void givenWrongUsername_whenIsOwnRequest_thenReturnsFalse() {
        long id = patientRepository.findAll().get(0).getPatientId();
        boolean isOwnRequest = patientService.isOwnRequest(id);
        assertThat(isOwnRequest).isFalse();
    }
}
