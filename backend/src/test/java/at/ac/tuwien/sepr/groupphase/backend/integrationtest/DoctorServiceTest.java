package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.DoctorRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.DoctorService;
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
public class DoctorServiceTest {
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private DoctorRepository doctorRepository;

    @BeforeEach
    void clean() {
        doctorRepository.deleteAll();
    }

    @Test
    public void givenValidDoctorCreateDto_whenCreateDoctor_thenCreatedDoctorIsReturned() {
        DoctorCreateDto toCreate = new DoctorCreateDto("a@a.a", "a", "b");
        DoctorDto createdDoctor = doctorService.createDoctor(toCreate);
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
        DoctorDto createdDoctor = doctorService.createDoctor(toCreate);
        DoctorDto searchedDoctor = doctorService.getDoctorById(createdDoctor.id());
        assertAll("Grouped Assertions of Doctor",
            () -> assertEquals(createdDoctor.id(), searchedDoctor.id(), "ID should be equal"),
            () -> assertEquals(createdDoctor.email(), searchedDoctor.email(), "Email should be equal"),
            () -> assertEquals(createdDoctor.firstname(), searchedDoctor.firstname(), "Firstname should be equal"),
            () -> assertEquals(createdDoctor.lastname(), searchedDoctor.lastname(), "Lastname should be equal"),
            () -> assertEquals(createdDoctor.password(), searchedDoctor.password(), "Password should be equal"),
            () -> assertEquals(createdDoctor.active(), searchedDoctor.active(), "Active should be equal"));
    }

    @Test
    public void givenThreeCreateDoctors_whenGetAllDoctors_thenThreeDoctorsAreReturned() {
        DoctorCreateDto toCreate = new DoctorCreateDto("a@a.a", "a", "b");
        DoctorDto createdDoctor1 = doctorService.createDoctor(toCreate);
        DoctorDto createdDoctor2 = doctorService.createDoctor(toCreate);
        DoctorDto createdDoctor3 = doctorService.createDoctor(toCreate);

        List<DoctorDto> allDoctors = doctorService.getAllDoctors();
        assertAll("Grouped Assertions of all Doctors",
            () -> assertEquals(3, allDoctors.size()),
            () -> assertEquals(toCreate.email(), createdDoctor1.email(), "Email should be equal"),
            () -> assertEquals(toCreate.email(), createdDoctor2.email(), "Email should be equal"),
            () -> assertEquals(toCreate.email(), createdDoctor3.email(), "Email should be equal"),
            () -> assertEquals(toCreate.firstname(), createdDoctor1.firstname(), "Firstname should be equal"),
            () -> assertEquals(toCreate.firstname(), createdDoctor2.firstname(), "Firstname should be equal"),
            () -> assertEquals(toCreate.firstname(), createdDoctor3.firstname(), "Firstname should be equal"),
            () -> assertEquals(toCreate.lastname(), createdDoctor1.lastname(), "Lastname should be equal"),
            () -> assertEquals(toCreate.lastname(), createdDoctor2.lastname(), "Lastname should be equal"),
            () -> assertEquals(toCreate.lastname(), createdDoctor3.lastname(), "Lastname should be equal"));
    }

    @Test
    public void givenNoDoctorsInDatabase_whenGetAllDoctors_thenEmptyListIsReturned() {
        List<DoctorDto> allDoctors = doctorService.getAllDoctors();
        assertEquals(0, allDoctors.size());
    }
}
