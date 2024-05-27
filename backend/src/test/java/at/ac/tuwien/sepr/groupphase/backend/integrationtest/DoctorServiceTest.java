package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.TestBase;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.DoctorService;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "datagen"})
public class DoctorServiceTest extends TestBase {
    @Autowired
    private DoctorService doctorService;

    @Test
    public void givenValidDoctorCreateDto_whenCreateDoctor_thenCreatedDoctorIsReturned() {
        DoctorCreateDto toCreate = new DoctorCreateDto("a@a.a", "a", "b");
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

    @Test
    public void givenIdOfNonExistentDoctor_whenGetDoctorById_thenNothingIsReturned() {
        assertThrows(NotFoundException.class, () -> doctorService.getDoctorById(-100L));
    }

    @Test
    public void givenCreateDoctor_whenGetDoctorById_thenDoctorIsReturned() {
        DoctorCreateDto toCreate = new DoctorCreateDto("a@a.a", "a", "b");
        Credential credential = new Credential();
        credential.setEmail("a@a.a");
        credential.setActive(true);
        credential.setFirstName("a");
        credential.setLastName("b");
        credential.setPassword("password");
        credential.setInitialPassword(false);
        credential.setRole(Role.DOCTOR);
        DoctorDto createdDoctor = doctorService.createDoctor(toCreate, credential);
        DoctorDto searchedDoctor = doctorService.getDoctorById(createdDoctor.id());
        assertAll("Grouped Assertions of Doctor",
            () -> assertEquals(createdDoctor.id(), searchedDoctor.id(), "ID should be equal"),
            () -> assertEquals(createdDoctor.email(), searchedDoctor.email(), "Email should be equal"),
            () -> assertEquals(createdDoctor.firstname(), searchedDoctor.firstname(), "Firstname should be equal"),
            () -> assertEquals(createdDoctor.lastname(), searchedDoctor.lastname(), "Lastname should be equal"),
            () -> assertEquals(createdDoctor.password(), searchedDoctor.password(), "Password should be equal"),
            () -> assertEquals(createdDoctor.active(), searchedDoctor.active(), "Active should be equal"));
    }
    //TODO: adjust and refactor tests (Issue #60)
    /*
    @Test
    public void givenThreeCreateDoctors_whenGetAllDoctors_thenThreeDoctorsAreReturned() {
        DoctorCreateDto toCreate1 = new DoctorCreateDto("a@a.a", "a", "b");
        Credential credential1 = new Credential();
        credential1.setEmail("a@a.a");
        credential1.setActive(true);
        credential1.setFirstName("a");
        credential1.setLastName("b");
        credential1.setPassword("password");
        credential1.setInitialPassword(false);
        credential1.setRole(Role.DOCTOR);
        DoctorDto createdDoctor1 = doctorService.createDoctor(toCreate1, credential1);
        DoctorCreateDto toCreate2 = new DoctorCreateDto("b@b.b", "a", "b");
        Credential credential2 = new Credential();
        credential2.setEmail("b@b.b");
        credential2.setActive(true);
        credential2.setFirstName("a");
        credential2.setLastName("b");
        credential2.setPassword("password");
        credential2.setInitialPassword(false);
        credential2.setRole(Role.DOCTOR);
        DoctorDto createdDoctor2 = doctorService.createDoctor(toCreate2, credential2);
        DoctorCreateDto toCreate3 = new DoctorCreateDto("c@c.c", "a", "b");
        Credential credential3 = new Credential();
        credential3.setEmail("c@c.c");
        credential3.setActive(true);
        credential3.setFirstName("a");
        credential3.setLastName("b");
        credential3.setPassword("password");
        credential3.setInitialPassword(false);
        credential3.setRole(Role.DOCTOR);
        DoctorDto createdDoctor3 = doctorService.createDoctor(toCreate3, credential3);

        List<DoctorDto> allDoctors = doctorService.getAllDoctors();
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

    @Test
    public void givenNoDoctorsInDatabase_whenGetAllDoctors_thenEmptyListIsReturned() {
        List<DoctorDto> allDoctors = doctorService.getAllDoctors();
        assertEquals(0, allDoctors.size());
    }
     */
}
