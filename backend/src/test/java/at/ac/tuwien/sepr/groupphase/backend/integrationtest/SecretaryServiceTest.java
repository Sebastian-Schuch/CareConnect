package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.TestBase;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDtoUpdate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Secretary;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.SecretaryRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.SecretaryService;
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
public class SecretaryServiceTest extends TestBase {
    @Autowired
    private SecretaryService secretaryService;

    @Autowired
    SecretaryRepository secretaryRepository;

    public SecretaryServiceTest() {
        super("secretary");
    }


    @Transactional
    @Test
    public void givenValidSecretaryCreateDto_whenCreateSecretary_thenCreatedSecretaryIsReturned() {
        SecretaryDtoCreate toCreate = new SecretaryDtoCreate("a@a.a", "a", "b");
        Credential credential = new Credential();
        credential.setEmail("a@a.a");
        credential.setActive(true);
        credential.setFirstName("a");
        credential.setLastName("b");
        credential.setPassword("password");
        credential.setInitialPassword(false);
        credential.setRole(Role.SECRETARY);
        SecretaryDto createdSecretary = secretaryService.create(toCreate, credential);
        assertAll("Grouped Assertions of Secretary",
            () -> assertEquals(toCreate.email(), createdSecretary.email(), "email should be equal"),
            () -> assertEquals(toCreate.firstname(), createdSecretary.firstname(), "firstname should be equal"),
            () -> assertEquals(toCreate.lastname(), createdSecretary.lastname(), "lastname should be equal"));
    }

    @Transactional
    @Test
    public void givenIdOfSecretary_whenGetSecretaryById_thenSecretaryIsReturned() {
        Secretary secretary = secretaryRepository.findAll().get(0);
        SecretaryDto toBeFoundSecretary = new SecretaryDto(secretary.getSecretaryId(), secretary.getCredential().getFirstName(),
            secretary.getCredential().getLastName(), secretary.getCredential().getEmail(), secretary.getCredential().getPassword(), secretary.getCredential().isInitialPassword(), secretary.getCredential().getActive());
        SecretaryDtoSparse foundSecretary = secretaryService.getById(toBeFoundSecretary.id());
        assertAll("Grouped Assertions of Secretary",
            () -> assertEquals(toBeFoundSecretary.id(), foundSecretary.id(), "ID should be equal"),
            () -> assertEquals(toBeFoundSecretary.email(), foundSecretary.email(), "Email should be equal"),
            () -> assertEquals(toBeFoundSecretary.firstname(), foundSecretary.firstname(), "Firstname should be equal"),
            () -> assertEquals(toBeFoundSecretary.lastname(), foundSecretary.lastname(), "Lastname should be equal"),
            () -> assertEquals(toBeFoundSecretary.isInitialPassword(), foundSecretary.isInitialPassword(), "InitialPassword should be equal"));
    }

    @Transactional
    @Test
    public void givenSecretaryDtoUpdate_whenUpdateSecretary_thenSecretaryGetsUpdatedAndDataHasBeenUpdated() {
        long id = secretaryRepository.findAll().get(0).getSecretaryId();
        SecretaryDtoUpdate updateSecretary = new SecretaryDtoUpdate("x", "y", "a@a.a", true, false);

        SecretaryDtoSparse foundSecretary = secretaryService.getById(id);
        SecretaryDtoSparse finalFoundSecretary1 = foundSecretary;

        assertAll("Grouped Assertions of Secretary",
            () -> assertNotEquals(updateSecretary.email(), finalFoundSecretary1.email(), "Email shouldn't be equal"),
            () -> assertNotEquals(updateSecretary.firstname(), finalFoundSecretary1.firstname(), "Firstname shouldn't be equal"),
            () -> assertNotEquals(updateSecretary.lastname(), finalFoundSecretary1.lastname(), "Lastname shouldn't be equal"));

        SecretaryDtoSparse updatedSecretary = secretaryService.updateSecretary(id, updateSecretary);
        assertAll("Grouped Assertions of Secretary",
            () -> assertEquals(updateSecretary.email(), updatedSecretary.email(), "Email should be equal"),
            () -> assertEquals(updateSecretary.firstname(), updatedSecretary.firstname(), "Firstname should be equal"),
            () -> assertEquals(updateSecretary.lastname(), updatedSecretary.lastname(), "Lastname should be equal"));

        foundSecretary = secretaryService.getById(id);
        SecretaryDtoSparse finalFoundSecretary = foundSecretary;
        assertAll("Grouped Assertions of Secretary",
            () -> assertEquals(updateSecretary.email(), finalFoundSecretary.email(), "Email should be equal"),
            () -> assertEquals(updateSecretary.firstname(), finalFoundSecretary.firstname(), "Firstname should be equal"),
            () -> assertEquals(updateSecretary.lastname(), finalFoundSecretary.lastname(), "Lastname should be equal"));
    }

    @Transactional
    @Test
    public void givenSecretaryDtoUpdateWithNonExistentId_whenUpdateSecretary_thenThrowsNotFoundAndSecretaryDoesntGetUpdated() {
        long id = secretaryRepository.findAll().get(0).getSecretaryId();
        SecretaryDtoUpdate updateSecretary = new SecretaryDtoUpdate("x", "y", "a@a.a", true, false);

        SecretaryDtoSparse foundSecretary = secretaryService.getById(id);
        SecretaryDtoSparse finalFoundSecretary1 = foundSecretary;

        assertAll("Grouped Assertions of Secretary",
            () -> assertNotEquals(updateSecretary.email(), finalFoundSecretary1.email(), "Email shouldn't be equal"),
            () -> assertNotEquals(updateSecretary.firstname(), finalFoundSecretary1.firstname(), "Firstname shouldn't be equal"),
            () -> assertNotEquals(updateSecretary.lastname(), finalFoundSecretary1.lastname(), "Lastname shouldn't be equal"));

        assertThrows(NotFoundException.class, () -> secretaryService.updateSecretary(-1L, updateSecretary));

        foundSecretary = secretaryService.getById(id);
        SecretaryDtoSparse finalFoundSecretary = foundSecretary;
        assertAll("Grouped Assertions of Secretary",
            () -> assertNotEquals(updateSecretary.email(), finalFoundSecretary.email(), "Email should be equal"),
            () -> assertNotEquals(updateSecretary.firstname(), finalFoundSecretary.firstname(), "Firstname should be equal"),
            () -> assertNotEquals(updateSecretary.lastname(), finalFoundSecretary.lastname(), "Lastname should be equal"));
    }

    @Transactional
    @Test
    public void givenIdOfNonExistentSecretary_whenGetSecretaryById_thenThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> secretaryService.getById(-100L));
    }

    @Transactional
    @Test
    public void givenThreeNewlyCreateSecretaries_whenGetAllSecretaries_thenThreeSecretariesAreReturned() {
        secretaryRepository.deleteAll();
        SecretaryDtoCreate toCreate1 = new SecretaryDtoCreate("a@a.a", "a", "b");
        Credential credential1 = new Credential();
        credential1.setEmail("a@a.a");
        credential1.setActive(true);
        credential1.setFirstName("a");
        credential1.setLastName("b");
        credential1.setPassword("password");
        credential1.setInitialPassword(false);
        credential1.setRole(Role.SECRETARY);
        SecretaryDto createdSecretary1 = secretaryService.create(toCreate1, credential1);
        SecretaryDtoCreate toCreate2 = new SecretaryDtoCreate("b@b.b", "a", "b");
        Credential credential2 = new Credential();
        credential2.setEmail("b@b.b");
        credential2.setActive(true);
        credential2.setFirstName("a");
        credential2.setLastName("b");
        credential2.setPassword("password");
        credential2.setInitialPassword(false);
        credential2.setRole(Role.SECRETARY);
        SecretaryDto createdSecretary2 = secretaryService.create(toCreate2, credential2);
        SecretaryDtoCreate toCreate3 = new SecretaryDtoCreate("c@c.c", "a", "b");
        Credential credential3 = new Credential();
        credential3.setEmail("c@c.c");
        credential3.setActive(true);
        credential3.setFirstName("a");
        credential3.setLastName("b");
        credential3.setPassword("password");
        credential3.setInitialPassword(false);
        credential3.setRole(Role.SECRETARY);
        SecretaryDto createdSecretary3 = secretaryService.create(toCreate3, credential3);

        List<SecretaryDtoSparse> allSecretaries = secretaryService.getAllSecretaries();
        assertAll("Grouped Assertions of all Secretaries",
            () -> assertEquals(3, allSecretaries.size()),
            () -> assertEquals(toCreate1.email(), createdSecretary1.email(), "Email should be equal"),
            () -> assertEquals(toCreate2.email(), createdSecretary2.email(), "Email should be equal"),
            () -> assertEquals(toCreate3.email(), createdSecretary3.email(), "Email should be equal"),
            () -> assertEquals(toCreate1.firstname(), createdSecretary1.firstname(), "Firstname should be equal"),
            () -> assertEquals(toCreate2.firstname(), createdSecretary2.firstname(), "Firstname should be equal"),
            () -> assertEquals(toCreate3.firstname(), createdSecretary3.firstname(), "Firstname should be equal"),
            () -> assertEquals(toCreate1.lastname(), createdSecretary1.lastname(), "Lastname should be equal"),
            () -> assertEquals(toCreate2.lastname(), createdSecretary2.lastname(), "Lastname should be equal"),
            () -> assertEquals(toCreate3.lastname(), createdSecretary3.lastname(), "Lastname should be equal"));
    }

    @Transactional
    @Test
    public void givenNoSecretariesInDatabase_whenGetAllSecretaries_thenEmptyListIsReturned() {
        secretaryRepository.deleteAll();
        List<SecretaryDtoSparse> allSecretaries = secretaryService.getAllSecretaries();
        assertThat(allSecretaries).isEmpty();
    }

    @Transactional
    @Test
    public void givenIdOfSecretary_whenGetSecretaryEntityById_thenSecretaryEntityIsReturned() {
        Secretary toBeFoundSecretary = secretaryRepository.findAll().get(0);
        Secretary foundSecretary = secretaryService.getEntityById(toBeFoundSecretary.getSecretaryId());
        assertAll("Grouped Assertions of Secretary",
            () -> assertEquals(toBeFoundSecretary.getSecretaryId(), foundSecretary.getSecretaryId(), "ID should be equal"),
            () -> assertEquals(toBeFoundSecretary.getCredential().getEmail(), foundSecretary.getCredential().getEmail(), "Email should be equal"),
            () -> assertEquals(toBeFoundSecretary.getCredential().getFirstName(), foundSecretary.getCredential().getFirstName(), "Firstname should be equal"),
            () -> assertEquals(toBeFoundSecretary.getCredential().getLastName(), foundSecretary.getCredential().getLastName(), "Lastname should be equal"),
            () -> assertEquals(toBeFoundSecretary.getCredential().getPassword(), foundSecretary.getCredential().getPassword(), "Password should be equal"),
            () -> assertEquals(toBeFoundSecretary.getCredential().getActive(), foundSecretary.getCredential().getActive(), "Active should be equal"),
            () -> assertEquals(toBeFoundSecretary.getCredential().isInitialPassword(), foundSecretary.getCredential().isInitialPassword(), "InitialPassword should be equal"));
    }


    @Transactional
    @Test
    public void givenNonExistentIdOfSecretary_whenGetSecretaryEntityById_thenThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> secretaryService.getEntityById(-1L));
    }

    @Transactional
    @Test
    public void givenCredentialOfSecretary_whenFindSecretaryByCredential_thenCredentialIsReturned() {
        long id = secretaryRepository.findAll().get(0).getSecretaryId();
        SecretaryDto toBeFoundSecretary = new SecretaryDto(id, "Secretary",
            "One", "secretary1@email.com", "OnePassword", false, true);
        SecretaryDto foundSecretary = secretaryService.findSecretaryByCredential(secretaryRepository.findAll().get(0).getCredential());
        assertAll("Grouped Assertions of Secretary",
            () -> assertEquals(toBeFoundSecretary.id(), foundSecretary.id(), "ID should be equal"),
            () -> assertEquals(toBeFoundSecretary.email(), foundSecretary.email(), "Email should be equal"),
            () -> assertEquals(toBeFoundSecretary.firstname(), foundSecretary.firstname(), "Firstname should be equal"),
            () -> assertEquals(toBeFoundSecretary.lastname(), foundSecretary.lastname(), "Lastname should be equal"),
            () -> assertEquals(toBeFoundSecretary.password(), foundSecretary.password(), "Password should be equal"),
            () -> assertEquals(toBeFoundSecretary.active(), foundSecretary.active(), "Active should be equal"),
            () -> assertEquals(toBeFoundSecretary.isInitialPassword(), foundSecretary.isInitialPassword(), "InitialPassword should be equal"));
    }

    @Transactional
    @Test
    public void givenNonExistentCredential_whenFindSecretaryByCredential_thenThrowsNotFoundException() {
        Credential credential = secretaryRepository.findAll().get(0).getCredential();
        secretaryRepository.deleteById(secretaryRepository.findAll().get(0).getSecretaryId());
        assertThrows(NotFoundException.class, () -> secretaryService.findSecretaryByCredential(credential));
    }

    @Transactional
    @Test
    public void givenUserDtoSearch_whenSearchSecretaries_thenReturnFoundSecretaries() {
        long id = secretaryRepository.findAll().get(0).getSecretaryId();
        List<SecretaryDto> toBeFoundSecretary = new ArrayList<>();
        toBeFoundSecretary.add(new SecretaryDto(id, "Secretary",
            "One", "secretary1@email.com", "OnePassword", false, true));

        UserDtoSearch search = new UserDtoSearch("secretary1@email.com", "Secretary", "One");
        List<SecretaryDtoSparse> foundSecretary = secretaryService.searchSecretaries(search);
        assertThat(foundSecretary).isNotNull().hasSize(1);
        assertAll("Grouped Assertions of Secretary",
            () -> assertEquals(toBeFoundSecretary.get(0).id(), foundSecretary.get(0).id(), "ID should be equal"),
            () -> assertEquals(toBeFoundSecretary.get(0).email(), foundSecretary.get(0).email(), "Email should be equal"),
            () -> assertEquals(toBeFoundSecretary.get(0).firstname(), foundSecretary.get(0).firstname(), "Firstname should be equal"),
            () -> assertEquals(toBeFoundSecretary.get(0).lastname(), foundSecretary.get(0).lastname(), "Lastname should be equal"),
            () -> assertEquals(toBeFoundSecretary.get(0).isInitialPassword(), foundSecretary.get(0).isInitialPassword(), "InitialPassword should be equal"));
    }

    @Transactional
    @Test
    public void givenUserDtoSearchWithNonExistentName_whenSearchSecretaries_thenReturnsEmptyList() {
        UserDtoSearch search = new UserDtoSearch("x.x@x.x", "x", "x");
        List<SecretaryDtoSparse> foundSecretary = secretaryService.searchSecretaries(search);
        assertThat(foundSecretary).isNotNull().hasSize(0);
    }

    @Transactional
    @Test
    @WithMockUser(username = "secretary1@email.com", authorities = {"SECRETARY"})
    public void givenRightUsername_whenIsOwnRequest_thenReturnsTrue() {
        long id = secretaryRepository.findAll().get(0).getSecretaryId();
        boolean isOwnRequest = secretaryService.isOwnRequest(id);
        assertThat(isOwnRequest).isTrue();
    }

    @Transactional
    @Test
    @WithMockUser(username = "Secretary", authorities = {"SECRETARY"})
    public void givenWrongUsername_whenIsOwnRequest_thenReturnsFalse() {
        long id = secretaryRepository.findAll().get(0).getSecretaryId();
        boolean isOwnRequest = secretaryService.isOwnRequest(id);
        assertThat(isOwnRequest).isFalse();
    }
}
