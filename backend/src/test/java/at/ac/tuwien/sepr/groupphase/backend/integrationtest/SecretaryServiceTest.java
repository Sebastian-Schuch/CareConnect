package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.SecretaryRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.SecretaryService;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
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
        Credential credential = new Credential();
        credential.setEmail("a@a.a");
        credential.setActive(true);
        credential.setFirstName("a");
        credential.setLastName("b");
        credential.setPassword("password");
        credential.setInitialPassword(false);
        credential.setRole(Role.SECRETARY);
        SecretaryDetailDto createdSecretary = secretaryService.create(createSecretary, credential);
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
        Credential credential = new Credential();
        credential.setEmail("a@a.a");
        credential.setActive(true);
        credential.setFirstName("a");
        credential.setLastName("b");
        credential.setPassword("password");
        credential.setInitialPassword(false);
        credential.setRole(Role.PATIENT);
        SecretaryDetailDto createdSecretary = secretaryService.create(createSecretary, credential);
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
        Credential credential1 = new Credential();
        credential1.setEmail("a@a.a");
        credential1.setActive(true);
        credential1.setFirstName("a");
        credential1.setLastName("b");
        credential1.setPassword("password");
        credential1.setInitialPassword(false);
        credential1.setRole(Role.PATIENT);
        SecretaryDetailDto createdSecretary1 = secretaryService.create(createSecretary1, credential1);
        SecretaryCreateDto createSecretary2 = new SecretaryCreateDto("b@b.b", "a", "b");
        Credential credential2 = new Credential();
        credential2.setEmail("b@b.b");
        credential2.setActive(true);
        credential2.setFirstName("a");
        credential2.setLastName("b");
        credential2.setPassword("password");
        credential2.setInitialPassword(false);
        credential2.setRole(Role.PATIENT);
        SecretaryDetailDto createdSecretary2 = secretaryService.create(createSecretary2, credential2);
        SecretaryCreateDto createSecretary3 = new SecretaryCreateDto("c@c.c", "a", "b");
        Credential credential3 = new Credential();
        credential3.setEmail("c@c.c");
        credential3.setActive(true);
        credential3.setFirstName("a");
        credential3.setLastName("b");
        credential3.setPassword("password");
        credential3.setInitialPassword(false);
        credential3.setRole(Role.PATIENT);
        SecretaryDetailDto createdSecretary3 = secretaryService.create(createSecretary3, credential3);
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
