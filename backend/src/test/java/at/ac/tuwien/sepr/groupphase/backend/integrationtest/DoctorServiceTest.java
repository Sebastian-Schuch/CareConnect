package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.TestBase;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoUpdate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Doctor;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.DoctorRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.DoctorService;
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
public class DoctorServiceTest extends TestBase {
    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DoctorRepository doctorRepository;

    public DoctorServiceTest() {
        super("doctor");
    }

    @Transactional
    @Test
    public void givenValidDoctorCreateDto_whenCreateDoctor_thenCreatedDoctorIsReturned() {
        DoctorDtoCreate toCreate = new DoctorDtoCreate("a@a.a", "a", "b");
        Credential credential = new Credential();
        credential.setEmail("a@a.a");
        credential.setActive(true);
        credential.setFirstName("a");
        credential.setLastName("b");
        credential.setPassword("password");
        credential.setInitialPassword(false);
        credential.setRole(Role.DOCTOR);
        DoctorDto createdDoctor = doctorService.createDoctor(toCreate, credential);
        assertAll("Grouped Assertions of Doctor",
            () -> assertEquals(toCreate.email(), createdDoctor.email(), "email should be equal"),
            () -> assertEquals(toCreate.firstname(), createdDoctor.firstname(), "firstname should be equal"),
            () -> assertEquals(toCreate.lastname(), createdDoctor.lastname(), "lastname should be equal"));
    }

    @Transactional
    @Test
    public void givenIdOfDoctor_whenGetDoctorById_thenDoctorIsReturned() {
        Doctor doctor = doctorRepository.findAll().get(0);
        DoctorDto toBeFoundDoctor = new DoctorDto(doctor.getDoctorId(), doctor.getCredential().getFirstName(),
            doctor.getCredential().getLastName(), doctor.getCredential().getEmail(), doctor.getCredential().getPassword(), doctor.getCredential().isInitialPassword(), doctor.getCredential().getActive());
        DoctorDtoSparse foundDoctor = doctorService.getDoctorById(toBeFoundDoctor.id());
        assertAll("Grouped Assertions of Doctor",
            () -> assertEquals(toBeFoundDoctor.id(), foundDoctor.id(), "ID should be equal"),
            () -> assertEquals(toBeFoundDoctor.email(), foundDoctor.email(), "Email should be equal"),
            () -> assertEquals(toBeFoundDoctor.firstname(), foundDoctor.firstname(), "Firstname should be equal"),
            () -> assertEquals(toBeFoundDoctor.lastname(), foundDoctor.lastname(), "Lastname should be equal"),
            () -> assertEquals(toBeFoundDoctor.isInitialPassword(), foundDoctor.isInitialPassword(), "InitialPassword should be equal"));
    }

    @Transactional
    @Test
    public void givenDoctorDtoUpdate_whenUpdateDoctor_thenDoctorGetsUpdatedAndDataHasBeenUpdated() {
        long id = doctorRepository.findAll().get(0).getDoctorId();
        DoctorDtoUpdate updateDoctor = new DoctorDtoUpdate("x", "y", "a@a.a", true, false);

        DoctorDtoSparse foundDoctor = doctorService.getDoctorById(id);
        DoctorDtoSparse finalFoundDoctor1 = foundDoctor;

        assertAll("Grouped Assertions of Doctor",
            () -> assertNotEquals(updateDoctor.email(), finalFoundDoctor1.email(), "Email shouldn't be equal"),
            () -> assertNotEquals(updateDoctor.firstname(), finalFoundDoctor1.firstname(), "Firstname shouldn't be equal"),
            () -> assertNotEquals(updateDoctor.lastname(), finalFoundDoctor1.lastname(), "Lastname shouldn't be equal"));

        DoctorDtoSparse updatedDoctor = doctorService.updateDoctor(id, updateDoctor);
        assertAll("Grouped Assertions of Doctor",
            () -> assertEquals(updateDoctor.email(), updatedDoctor.email(), "Email should be equal"),
            () -> assertEquals(updateDoctor.firstname(), updatedDoctor.firstname(), "Firstname should be equal"),
            () -> assertEquals(updateDoctor.lastname(), updatedDoctor.lastname(), "Lastname should be equal"));

        foundDoctor = doctorService.getDoctorById(id);
        DoctorDtoSparse finalFoundDoctor = foundDoctor;
        assertAll("Grouped Assertions of Doctor",
            () -> assertEquals(updateDoctor.email(), finalFoundDoctor.email(), "Email should be equal"),
            () -> assertEquals(updateDoctor.firstname(), finalFoundDoctor.firstname(), "Firstname should be equal"),
            () -> assertEquals(updateDoctor.lastname(), finalFoundDoctor.lastname(), "Lastname should be equal"));
    }

    @Transactional
    @Test
    public void givenDoctorDtoUpdateWithNonExistentId_whenUpdateDoctor_thenThrowsNotFoundAndDoctorDoesntGetUpdated() {
        long id = doctorRepository.findAll().get(0).getDoctorId();
        DoctorDtoUpdate updateDoctor = new DoctorDtoUpdate("x", "y", "a@a.a", true, false);

        DoctorDtoSparse foundDoctor = doctorService.getDoctorById(id);
        DoctorDtoSparse finalFoundDoctor1 = foundDoctor;

        assertAll("Grouped Assertions of Doctor",
            () -> assertNotEquals(updateDoctor.email(), finalFoundDoctor1.email(), "Email shouldn't be equal"),
            () -> assertNotEquals(updateDoctor.firstname(), finalFoundDoctor1.firstname(), "Firstname shouldn't be equal"),
            () -> assertNotEquals(updateDoctor.lastname(), finalFoundDoctor1.lastname(), "Lastname shouldn't be equal"));

        assertThrows(NotFoundException.class, () -> doctorService.updateDoctor(-1L, updateDoctor));

        foundDoctor = doctorService.getDoctorById(id);
        DoctorDtoSparse finalFoundDoctor = foundDoctor;
        assertAll("Grouped Assertions of Doctor",
            () -> assertNotEquals(updateDoctor.email(), finalFoundDoctor.email(), "Email should be equal"),
            () -> assertNotEquals(updateDoctor.firstname(), finalFoundDoctor.firstname(), "Firstname should be equal"),
            () -> assertNotEquals(updateDoctor.lastname(), finalFoundDoctor.lastname(), "Lastname should be equal"));
    }

    @Transactional
    @Test
    public void givenIdOfNonExistentDoctor_whenGetDoctorById_thenThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> doctorService.getDoctorById(-100L));
    }

    @Transactional
    @Test
    public void givenThreeNewlyCreateDoctors_whenGetAllDoctors_thenThreeDoctorsAreReturned() {
        doctorRepository.deleteAll();
        DoctorDtoCreate toCreate1 = new DoctorDtoCreate("a@a.a", "a", "b");
        Credential credential1 = new Credential();
        credential1.setEmail("a@a.a");
        credential1.setActive(true);
        credential1.setFirstName("a");
        credential1.setLastName("b");
        credential1.setPassword("password");
        credential1.setInitialPassword(false);
        credential1.setRole(Role.DOCTOR);
        DoctorDto createdDoctor1 = doctorService.createDoctor(toCreate1, credential1);
        DoctorDtoCreate toCreate2 = new DoctorDtoCreate("b@b.b", "a", "b");
        Credential credential2 = new Credential();
        credential2.setEmail("b@b.b");
        credential2.setActive(true);
        credential2.setFirstName("a");
        credential2.setLastName("b");
        credential2.setPassword("password");
        credential2.setInitialPassword(false);
        credential2.setRole(Role.DOCTOR);
        DoctorDto createdDoctor2 = doctorService.createDoctor(toCreate2, credential2);
        DoctorDtoCreate toCreate3 = new DoctorDtoCreate("c@c.c", "a", "b");
        Credential credential3 = new Credential();
        credential3.setEmail("c@c.c");
        credential3.setActive(true);
        credential3.setFirstName("a");
        credential3.setLastName("b");
        credential3.setPassword("password");
        credential3.setInitialPassword(false);
        credential3.setRole(Role.DOCTOR);
        DoctorDto createdDoctor3 = doctorService.createDoctor(toCreate3, credential3);

        List<DoctorDtoSparse> allDoctors = doctorService.getAllDoctors();
        assertAll("Grouped Assertions of all Doctors",
            () -> assertEquals(3, allDoctors.size()),
            () -> assertEquals(toCreate1.email(), createdDoctor1.email(), "Email should be equal"),
            () -> assertEquals(toCreate2.email(), createdDoctor2.email(), "Email should be equal"),
            () -> assertEquals(toCreate3.email(), createdDoctor3.email(), "Email should be equal"),
            () -> assertEquals(toCreate1.firstname(), createdDoctor1.firstname(), "Firstname should be equal"),
            () -> assertEquals(toCreate2.firstname(), createdDoctor2.firstname(), "Firstname should be equal"),
            () -> assertEquals(toCreate3.firstname(), createdDoctor3.firstname(), "Firstname should be equal"),
            () -> assertEquals(toCreate1.lastname(), createdDoctor1.lastname(), "Lastname should be equal"),
            () -> assertEquals(toCreate2.lastname(), createdDoctor2.lastname(), "Lastname should be equal"),
            () -> assertEquals(toCreate3.lastname(), createdDoctor3.lastname(), "Lastname should be equal"));
    }

    @Transactional
    @Test
    public void givenNoDoctorsInDatabase_whenGetAllDoctors_thenEmptyListIsReturned() {
        doctorRepository.deleteAll();
        List<DoctorDtoSparse> allDoctors = doctorService.getAllDoctors();
        assertThat(allDoctors).isEmpty();
    }

    @Transactional
    @Test
    public void givenIdOfDoctor_whenGetDoctorEntityById_thenDoctorEntityIsReturned() {
        Doctor toBeFoundDoctor = doctorRepository.findAll().get(0);
        Doctor foundDoctor = doctorService.getDoctorEntityById(toBeFoundDoctor.getDoctorId());
        assertAll("Grouped Assertions of Doctor",
            () -> assertEquals(toBeFoundDoctor.getDoctorId(), foundDoctor.getDoctorId(), "ID should be equal"),
            () -> assertEquals(toBeFoundDoctor.getCredential().getEmail(), foundDoctor.getCredential().getEmail(), "Email should be equal"),
            () -> assertEquals(toBeFoundDoctor.getCredential().getFirstName(), foundDoctor.getCredential().getFirstName(), "Firstname should be equal"),
            () -> assertEquals(toBeFoundDoctor.getCredential().getLastName(), foundDoctor.getCredential().getLastName(), "Lastname should be equal"),
            () -> assertEquals(toBeFoundDoctor.getCredential().getPassword(), foundDoctor.getCredential().getPassword(), "Password should be equal"),
            () -> assertEquals(toBeFoundDoctor.getCredential().getActive(), foundDoctor.getCredential().getActive(), "Active should be equal"),
            () -> assertEquals(toBeFoundDoctor.getCredential().isInitialPassword(), foundDoctor.getCredential().isInitialPassword(), "InitialPassword should be equal"));
    }


    @Transactional
    @Test
    public void givenNonExistentIdOfDoctor_whenGetDoctorEntityById_thenThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> doctorService.getDoctorEntityById(-1L));
    }

    @Transactional
    @Test
    public void givenEmailOfDoctor_whenGetDoctorByEmail_thenDoctorIsReturned() {
        long id = doctorRepository.findAll().get(0).getDoctorId();
        DoctorDto toBeFoundDoctor = new DoctorDto(id, "Doctor",
            "Eggman", "doctor.eggman@email.com", "ChaosEmeralds", false, true);
        DoctorDto foundDoctor = doctorService.getDoctorByEmail(toBeFoundDoctor.email());
        assertAll("Grouped Assertions of Doctor",
            () -> assertEquals(toBeFoundDoctor.id(), foundDoctor.id(), "ID should be equal"),
            () -> assertEquals(toBeFoundDoctor.email(), foundDoctor.email(), "Email should be equal"),
            () -> assertEquals(toBeFoundDoctor.firstname(), foundDoctor.firstname(), "Firstname should be equal"),
            () -> assertEquals(toBeFoundDoctor.lastname(), foundDoctor.lastname(), "Lastname should be equal"),
            () -> assertEquals(toBeFoundDoctor.password(), foundDoctor.password(), "Password should be equal"),
            () -> assertEquals(toBeFoundDoctor.active(), foundDoctor.active(), "Active should be equal"),
            () -> assertEquals(toBeFoundDoctor.isInitialPassword(), foundDoctor.isInitialPassword(), "InitialPassword should be equal"));
    }

    /*
    @Transactional
    @Test
    public void givenNonExistentEmail_whenGetDoctorByEmail_thenThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> doctorService.getDoctorByEmail("x.x@x.x"));
    }
     */

    @Transactional
    @Test
    public void givenCredentialOfDoctor_whenFindDoctorByCredential_thenCredentialIsReturned() {
        long id = doctorRepository.findAll().get(0).getDoctorId();
        DoctorDto toBeFoundDoctor = new DoctorDto(id, "Doctor",
            "Eggman", "doctor.eggman@email.com", "ChaosEmeralds", false, true);
        DoctorDtoSparse foundDoctor = doctorService.findDoctorByCredential(doctorRepository.findAll().get(0).getCredential());
        assertAll("Grouped Assertions of Doctor",
            () -> assertEquals(toBeFoundDoctor.id(), foundDoctor.id(), "ID should be equal"),
            () -> assertEquals(toBeFoundDoctor.email(), foundDoctor.email(), "Email should be equal"),
            () -> assertEquals(toBeFoundDoctor.firstname(), foundDoctor.firstname(), "Firstname should be equal"),
            () -> assertEquals(toBeFoundDoctor.lastname(), foundDoctor.lastname(), "Lastname should be equal"),
            () -> assertEquals(toBeFoundDoctor.isInitialPassword(), foundDoctor.isInitialPassword(), "InitialPassword should be equal"));
    }

    @Transactional
    @Test
    public void givenNonExistentCredential_whenFindDoctorByCredential_thenThrowsNotFoundException() {
        Credential credential = doctorRepository.findAll().get(0).getCredential();
        doctorRepository.deleteById(doctorRepository.findAll().get(0).getDoctorId());
        assertThrows(NotFoundException.class, () -> doctorService.findDoctorByCredential(credential));
    }

    @Transactional
    @Test
    public void givenUserDtoSearch_whenSearchDoctors_thenReturnFoundDoctors() {
        long id = doctorRepository.findAll().get(0).getDoctorId();
        List<DoctorDto> toBeFoundDoctor = new ArrayList<>();
        toBeFoundDoctor.add(new DoctorDto(id, "Doctor",
            "Eggman", "doctor.eggman@email.com", "ChaosEmeralds", false, true));

        UserDtoSearch search = new UserDtoSearch("doctor.eggman@email.com", "Doctor", "Eggman");
        List<DoctorDtoSparse> foundDoctor = doctorService.searchDoctors(search);
        assertThat(foundDoctor).isNotNull().hasSize(1);
        assertAll("Grouped Assertions of Doctor",
            () -> assertEquals(toBeFoundDoctor.get(0).id(), foundDoctor.get(0).id(), "ID should be equal"),
            () -> assertEquals(toBeFoundDoctor.get(0).email(), foundDoctor.get(0).email(), "Email should be equal"),
            () -> assertEquals(toBeFoundDoctor.get(0).firstname(), foundDoctor.get(0).firstname(), "Firstname should be equal"),
            () -> assertEquals(toBeFoundDoctor.get(0).lastname(), foundDoctor.get(0).lastname(), "Lastname should be equal"),
            () -> assertEquals(toBeFoundDoctor.get(0).isInitialPassword(), foundDoctor.get(0).isInitialPassword(), "InitialPassword should be equal"));
    }

    @Transactional
    @Test
    public void givenUserDtoSearchWithNonExistentName_whenSearchDoctors_thenReturnsEmptyList() {
        UserDtoSearch search = new UserDtoSearch("x.x@x.x", "x", "x");
        List<DoctorDtoSparse> foundDoctor = doctorService.searchDoctors(search);
        assertThat(foundDoctor).isNotNull().hasSize(0);
    }

    @Transactional
    @Test
    @WithMockUser(username = "doctor.eggman@email.com", authorities = {"DOCTOR"})
    public void givenRightUsername_whenIsOwnRequest_thenReturnsTrue() {
        long id = doctorRepository.findAll().get(0).getDoctorId();
        boolean isOwnRequest = doctorService.isOwnRequest(id);
        assertThat(isOwnRequest).isTrue();
    }

    @Transactional
    @Test
    @WithMockUser(username = "doctor", authorities = {"DOCTOR"})
    public void givenWrongUsername_whenIsOwnRequest_thenReturnsFalse() {
        long id = doctorRepository.findAll().get(0).getDoctorId();
        boolean isOwnRequest = doctorService.isOwnRequest(id);
        assertThat(isOwnRequest).isFalse();
    }

}
