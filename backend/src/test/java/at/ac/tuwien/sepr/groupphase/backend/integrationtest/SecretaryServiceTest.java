package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.SecretaryRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.SecretaryService;
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
public class SecretaryServiceTest {
    @Autowired
    private SecretaryService secretaryService;

    @Autowired
    SecretaryRepository secretaryRepository;

    @BeforeEach
    void clean() {
        secretaryRepository.deleteAll();
    }

    @AfterEach
    void clear() {
        secretaryRepository.deleteAll();
    }


    @Test
    public void givenNewlyCreatedSecretary_whenGettingSecretaryWithId_thenReturnSecretary() {
        SecretaryCreateDto createSecretary = new SecretaryCreateDto("a@a.a", "a", "b");
        SecretaryDetailDto createdSecretary = secretaryService.create(createSecretary);
        SecretaryDetailDto foundSecretary = secretaryService.getById(createdSecretary.id());
        assertAll("Grouped Assertions of Secretary",
            () -> assertEquals(createdSecretary.id(), foundSecretary.id(), "ID should be equal"),
            () -> assertEquals(createdSecretary.email(), foundSecretary.email(), "Email should be equal"),
            () -> assertEquals(createdSecretary.firstname(), foundSecretary.firstname(), "Firstname should be equal"),
            () -> assertEquals(createdSecretary.lastname(), foundSecretary.lastname(), "Lastname should be equal"),
            () -> assertEquals(createdSecretary.password(), foundSecretary.password(), "Password should be equal"),
            () -> assertEquals(createdSecretary.active(), foundSecretary.active(), "Active should be equal"));
    }

    @Test
    public void givenIdOfNonExistentSecretary_whenGettingSecretaryWithId_thenThrowNotFoundException() {
        long id = -1;
        Exception e = assertThrows(NotFoundException.class, () -> secretaryService.getById(id));
        assertEquals("Secretary not found", e.getMessage());
    }

    @Test
    public void givenSecretaryCreateDto_whenCreatingNewSecretary_thenReturnNewlyCreated() {
        SecretaryCreateDto createSecretary = new SecretaryCreateDto("a@a.a", "a", "b");
        SecretaryDetailDto createdSecretary = secretaryService.create(createSecretary);
        assertAll("Grouped Assertions of Secretary",
            () -> assertEquals(createdSecretary.email(), createSecretary.email(), "Email should be equal"),
            () -> assertEquals(createdSecretary.firstname(), createSecretary.firstname(), "Firstname should be equal"),
            () -> assertEquals(createdSecretary.lastname(), createSecretary.lastname(), "Lastname should be equal"));
    }

    @Test
    public void givenNoSecretariesInDatabase_whenGettingAllSecretaries_thenReturnEmptyList() {
        List<SecretaryDetailDto> allSecretaries = secretaryService.getAllSecretaries();
        assertEquals(0, allSecretaries.size());
    }

    @Test
    public void givenThreeNewlyCreatedSecretariesInDatabase_whenGettingAllSecretaries_thenReturnThreeSecretaries() {
        SecretaryCreateDto createSecretary1 = new SecretaryCreateDto("a@a.a", "a", "b");
        SecretaryDetailDto createdSecretary1 = secretaryService.create(createSecretary1);
        SecretaryCreateDto createSecretary2 = new SecretaryCreateDto("b@b.b", "a", "b");
        SecretaryDetailDto createdSecretary2 = secretaryService.create(createSecretary2);
        SecretaryCreateDto createSecretary3 = new SecretaryCreateDto("c@c.c", "a", "b");
        SecretaryDetailDto createdSecretary3 = secretaryService.create(createSecretary3);
        assertAll("Grouped Assertions of Secretary",
            () -> assertEquals(createSecretary1.email(), createdSecretary1.email(), "Email should be equal"),
            () -> assertEquals(createSecretary1.firstname(), createdSecretary1.firstname(), "Firstname should be equal"),
            () -> assertEquals(createSecretary1.lastname(), createdSecretary1.lastname(), "Lastname should be equal"),

            () -> assertEquals(createSecretary2.email(), createdSecretary2.email(), "Email should be equal"),
            () -> assertEquals(createSecretary2.firstname(), createdSecretary2.firstname(), "Firstname should be equal"),
            () -> assertEquals(createSecretary2.lastname(), createdSecretary2.lastname(), "Lastname should be equal"),

            () -> assertEquals(createSecretary3.email(), createdSecretary3.email(), "Email should be equal"),
            () -> assertEquals(createSecretary3.firstname(), createdSecretary3.firstname(), "Firstname should be equal"),
            () -> assertEquals(createSecretary3.lastname(), createdSecretary3.lastname(), "Lastname should be equal"));
    }
}
