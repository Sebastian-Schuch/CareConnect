package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.TestBase;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdministratorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdministratorDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdministratorDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdministratorDtoUpdate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.entity.Administrator;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.AdministratorRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.AdministratorService;
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
public class AdministratorServiceTest extends TestBase {
    @Autowired
    private AdministratorService administratorService;

    @Autowired
    AdministratorRepository administratorRepository;

    public AdministratorServiceTest() {
        super("administrator");
    }


    @Transactional
    @Test
    public void givenValidAdministratorCreateDto_whenCreateAdministrator_thenCreatedAdministratorIsReturned() {
        AdministratorDtoCreate toCreate = new AdministratorDtoCreate("a@a.a", "a", "b");
        Credential credential = new Credential();
        credential.setEmail("a@a.a");
        credential.setActive(true);
        credential.setFirstName("a");
        credential.setLastName("b");
        credential.setPassword("password");
        credential.setInitialPassword(false);
        credential.setRole(Role.ADMIN);
        AdministratorDto createdAdministrator = administratorService.createAdministrator(toCreate, credential);
        assertAll("Grouped Assertions of Administrator",
            () -> assertEquals(toCreate.email(), createdAdministrator.email(), "email should be equal"),
            () -> assertEquals(toCreate.firstname(), createdAdministrator.firstname(), "firstname should be equal"),
            () -> assertEquals(toCreate.lastname(), createdAdministrator.lastname(), "lastname should be equal"));
    }

    @Transactional
    @Test
    public void givenIdOfAdministrator_whenGetAdministratorById_thenAdministratorIsReturned() {
        Administrator Administrator = administratorRepository.findAll().get(0);
        AdministratorDto toBeFoundAdministrator = new AdministratorDto(Administrator.getAdministratorId(), Administrator.getCredential().getFirstName(),
            Administrator.getCredential().getLastName(), Administrator.getCredential().getEmail(), Administrator.getCredential().getPassword(), Administrator.getCredential().isInitialPassword(), Administrator.getCredential().getActive());
        AdministratorDtoSparse foundAdministrator = administratorService.getAdministratorById(toBeFoundAdministrator.id());
        assertAll("Grouped Assertions of Administrator",
            () -> assertEquals(toBeFoundAdministrator.id(), foundAdministrator.id(), "ID should be equal"),
            () -> assertEquals(toBeFoundAdministrator.email(), foundAdministrator.email(), "Email should be equal"),
            () -> assertEquals(toBeFoundAdministrator.firstname(), foundAdministrator.firstname(), "Firstname should be equal"),
            () -> assertEquals(toBeFoundAdministrator.lastname(), foundAdministrator.lastname(), "Lastname should be equal"),
            () -> assertEquals(toBeFoundAdministrator.isInitialPassword(), foundAdministrator.isInitialPassword(), "InitialPassword should be equal"));
    }

    @Transactional
    @Test
    public void givenAdministratorDtoUpdate_whenUpdateAdministrator_thenAdministratorGetsUpdatedAndDataHasBeenUpdated() {
        long id = administratorRepository.findAll().get(0).getAdministratorId();
        AdministratorDtoUpdate updateAdministrator = new AdministratorDtoUpdate("x", "y", "a@a.a", true, false);

        AdministratorDtoSparse foundAdministrator = administratorService.getAdministratorById(id);
        AdministratorDtoSparse finalFoundAdministrator1 = foundAdministrator;

        assertAll("Grouped Assertions of Administrator",
            () -> assertNotEquals(updateAdministrator.email(), finalFoundAdministrator1.email(), "Email shouldn't be equal"),
            () -> assertNotEquals(updateAdministrator.firstname(), finalFoundAdministrator1.firstname(), "Firstname shouldn't be equal"),
            () -> assertNotEquals(updateAdministrator.lastname(), finalFoundAdministrator1.lastname(), "Lastname shouldn't be equal"));

        AdministratorDtoSparse updatedAdministrator = administratorService.updateAdministrator(id, updateAdministrator);
        assertAll("Grouped Assertions of Administrator",
            () -> assertEquals(updateAdministrator.email(), updatedAdministrator.email(), "Email should be equal"),
            () -> assertEquals(updateAdministrator.firstname(), updatedAdministrator.firstname(), "Firstname should be equal"),
            () -> assertEquals(updateAdministrator.lastname(), updatedAdministrator.lastname(), "Lastname should be equal"));

        foundAdministrator = administratorService.getAdministratorById(id);
        AdministratorDtoSparse finalFoundAdministrator = foundAdministrator;
        assertAll("Grouped Assertions of Administrator",
            () -> assertEquals(updateAdministrator.email(), finalFoundAdministrator.email(), "Email should be equal"),
            () -> assertEquals(updateAdministrator.firstname(), finalFoundAdministrator.firstname(), "Firstname should be equal"),
            () -> assertEquals(updateAdministrator.lastname(), finalFoundAdministrator.lastname(), "Lastname should be equal"));
    }

    @Transactional
    @Test
    public void givenAdministratorDtoUpdateWithNonExistentId_whenUpdateAdministrator_thenThrowsNotFoundAndAdministratorDoesntGetUpdated() {
        long id = administratorRepository.findAll().get(0).getAdministratorId();
        AdministratorDtoUpdate updateAdministrator = new AdministratorDtoUpdate("x", "y", "a@a.a", true, false);

        AdministratorDtoSparse foundAdministrator = administratorService.getAdministratorById(id);
        AdministratorDtoSparse finalFoundAdministrator1 = foundAdministrator;

        assertAll("Grouped Assertions of Administrator",
            () -> assertNotEquals(updateAdministrator.email(), finalFoundAdministrator1.email(), "Email shouldn't be equal"),
            () -> assertNotEquals(updateAdministrator.firstname(), finalFoundAdministrator1.firstname(), "Firstname shouldn't be equal"),
            () -> assertNotEquals(updateAdministrator.lastname(), finalFoundAdministrator1.lastname(), "Lastname shouldn't be equal"));

        assertThrows(NotFoundException.class, () -> administratorService.updateAdministrator(-1L, updateAdministrator));

        foundAdministrator = administratorService.getAdministratorById(id);
        AdministratorDtoSparse finalFoundAdministrator = foundAdministrator;
        assertAll("Grouped Assertions of Administrator",
            () -> assertNotEquals(updateAdministrator.email(), finalFoundAdministrator.email(), "Email should be equal"),
            () -> assertNotEquals(updateAdministrator.firstname(), finalFoundAdministrator.firstname(), "Firstname should be equal"),
            () -> assertNotEquals(updateAdministrator.lastname(), finalFoundAdministrator.lastname(), "Lastname should be equal"));
    }

    @Transactional
    @Test
    public void givenIdOfNonExistentAdministrator_whenGetAdministratorById_thenThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> administratorService.getAdministratorById(-100L));
    }

    @Transactional
    @Test
    public void givenEmailOfAdministrator_whenGetAdministratorByEmail_thenAdministratorIsReturned() {
        long id = administratorRepository.findAll().get(0).getAdministratorId();
        AdministratorDto toBeFoundAdministrator = new AdministratorDto(id, "Administrator",
            "One", "administrator1@email.com", "OnePassword", false, true);
        AdministratorDto foundAdministrator = administratorService.getAdministratorByEmail(toBeFoundAdministrator.email());
        assertAll("Grouped Assertions of Administrator",
            () -> assertEquals(toBeFoundAdministrator.id(), foundAdministrator.id(), "ID should be equal"),
            () -> assertEquals(toBeFoundAdministrator.email(), foundAdministrator.email(), "Email should be equal"),
            () -> assertEquals(toBeFoundAdministrator.firstname(), foundAdministrator.firstname(), "Firstname should be equal"),
            () -> assertEquals(toBeFoundAdministrator.lastname(), foundAdministrator.lastname(), "Lastname should be equal"),
            () -> assertEquals(toBeFoundAdministrator.password(), foundAdministrator.password(), "Password should be equal"),
            () -> assertEquals(toBeFoundAdministrator.active(), foundAdministrator.active(), "Active should be equal"),
            () -> assertEquals(toBeFoundAdministrator.isInitialPassword(), foundAdministrator.isInitialPassword(), "InitialPassword should be equal"));
    }

    @Transactional
    @Test
    public void givenThreeNewlyCreateSecretaries_whenGetAllSecretaries_thenThreeSecretariesAreReturned() {
        administratorRepository.deleteAll();
        AdministratorDtoCreate toCreate1 = new AdministratorDtoCreate("a@a.a", "a", "b");
        Credential credential1 = new Credential();
        credential1.setEmail("a@a.a");
        credential1.setActive(true);
        credential1.setFirstName("a");
        credential1.setLastName("b");
        credential1.setPassword("password");
        credential1.setInitialPassword(false);
        credential1.setRole(Role.ADMIN);
        AdministratorDto createdAdministrator1 = administratorService.createAdministrator(toCreate1, credential1);
        AdministratorDtoCreate toCreate2 = new AdministratorDtoCreate("b@b.b", "a", "b");
        Credential credential2 = new Credential();
        credential2.setEmail("b@b.b");
        credential2.setActive(true);
        credential2.setFirstName("a");
        credential2.setLastName("b");
        credential2.setPassword("password");
        credential2.setInitialPassword(false);
        credential2.setRole(Role.ADMIN);
        AdministratorDto createdAdministrator2 = administratorService.createAdministrator(toCreate2, credential2);
        AdministratorDtoCreate toCreate3 = new AdministratorDtoCreate("c@c.c", "a", "b");
        Credential credential3 = new Credential();
        credential3.setEmail("c@c.c");
        credential3.setActive(true);
        credential3.setFirstName("a");
        credential3.setLastName("b");
        credential3.setPassword("password");
        credential3.setInitialPassword(false);
        credential3.setRole(Role.ADMIN);
        AdministratorDto createdAdministrator3 = administratorService.createAdministrator(toCreate3, credential3);

        List<AdministratorDtoSparse> allSecretaries = administratorService.getAllAdministrators();
        assertAll("Grouped Assertions of all Secretaries",
            () -> assertEquals(3, allSecretaries.size()),
            () -> assertEquals(toCreate1.email(), createdAdministrator1.email(), "Email should be equal"),
            () -> assertEquals(toCreate2.email(), createdAdministrator2.email(), "Email should be equal"),
            () -> assertEquals(toCreate3.email(), createdAdministrator3.email(), "Email should be equal"),
            () -> assertEquals(toCreate1.firstname(), createdAdministrator1.firstname(), "Firstname should be equal"),
            () -> assertEquals(toCreate2.firstname(), createdAdministrator2.firstname(), "Firstname should be equal"),
            () -> assertEquals(toCreate3.firstname(), createdAdministrator3.firstname(), "Firstname should be equal"),
            () -> assertEquals(toCreate1.lastname(), createdAdministrator1.lastname(), "Lastname should be equal"),
            () -> assertEquals(toCreate2.lastname(), createdAdministrator2.lastname(), "Lastname should be equal"),
            () -> assertEquals(toCreate3.lastname(), createdAdministrator3.lastname(), "Lastname should be equal"));
    }

    @Transactional
    @Test
    public void givenNoSecretariesInDatabase_whenGetAllSecretaries_thenEmptyListIsReturned() {
        administratorRepository.deleteAll();
        List<AdministratorDtoSparse> allSecretaries = administratorService.getAllAdministrators();
        assertThat(allSecretaries).isEmpty();
    }

    @Transactional
    @Test
    public void givenIdOfAdministrator_whenGetAdministratorEntityById_thenAdministratorEntityIsReturned() {
        Administrator toBeFoundAdministrator = administratorRepository.findAll().get(0);
        Administrator foundAdministrator = administratorService.getAdministratorEntityById(toBeFoundAdministrator.getAdministratorId());
        assertAll("Grouped Assertions of Administrator",
            () -> assertEquals(toBeFoundAdministrator.getAdministratorId(), foundAdministrator.getAdministratorId(), "ID should be equal"),
            () -> assertEquals(toBeFoundAdministrator.getCredential().getEmail(), foundAdministrator.getCredential().getEmail(), "Email should be equal"),
            () -> assertEquals(toBeFoundAdministrator.getCredential().getFirstName(), foundAdministrator.getCredential().getFirstName(), "Firstname should be equal"),
            () -> assertEquals(toBeFoundAdministrator.getCredential().getLastName(), foundAdministrator.getCredential().getLastName(), "Lastname should be equal"),
            () -> assertEquals(toBeFoundAdministrator.getCredential().getPassword(), foundAdministrator.getCredential().getPassword(), "Password should be equal"),
            () -> assertEquals(toBeFoundAdministrator.getCredential().getActive(), foundAdministrator.getCredential().getActive(), "Active should be equal"),
            () -> assertEquals(toBeFoundAdministrator.getCredential().isInitialPassword(), foundAdministrator.getCredential().isInitialPassword(), "InitialPassword should be equal"));
    }


    @Transactional
    @Test
    public void givenNonExistentIdOfAdministrator_whenGetAdministratorEntityById_thenThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> administratorService.getAdministratorEntityById(-1L));
    }

    @Transactional
    @Test
    public void givenCredentialOfAdministrator_whenFindAdministratorByCredential_thenCredentialIsReturned() {
        long id = administratorRepository.findAll().get(0).getAdministratorId();
        AdministratorDto toBeFoundAdministrator = new AdministratorDto(id, "Administrator",
            "One", "administrator1@email.com", "OnePassword", false, true);
        AdministratorDto foundAdministrator = administratorService.findAdministratorByCredential(administratorRepository.findAll().get(0).getCredential());
        assertAll("Grouped Assertions of Administrator",
            () -> assertEquals(toBeFoundAdministrator.id(), foundAdministrator.id(), "ID should be equal"),
            () -> assertEquals(toBeFoundAdministrator.email(), foundAdministrator.email(), "Email should be equal"),
            () -> assertEquals(toBeFoundAdministrator.firstname(), foundAdministrator.firstname(), "Firstname should be equal"),
            () -> assertEquals(toBeFoundAdministrator.lastname(), foundAdministrator.lastname(), "Lastname should be equal"),
            () -> assertEquals(toBeFoundAdministrator.password(), foundAdministrator.password(), "Password should be equal"),
            () -> assertEquals(toBeFoundAdministrator.active(), foundAdministrator.active(), "Active should be equal"),
            () -> assertEquals(toBeFoundAdministrator.isInitialPassword(), foundAdministrator.isInitialPassword(), "InitialPassword should be equal"));
    }

    @Transactional
    @Test
    public void givenNonExistentCredential_whenFindAdministratorByCredential_thenThrowsNotFoundException() {
        Credential credential = administratorRepository.findAll().get(0).getCredential();
        administratorRepository.deleteById(administratorRepository.findAll().get(0).getAdministratorId());
        assertThrows(NotFoundException.class, () -> administratorService.findAdministratorByCredential(credential));
    }

    @Transactional
    @Test
    public void givenUserDtoSearch_whenSearchSecretaries_thenReturnFoundSecretaries() {
        long id = administratorRepository.findAll().get(0).getAdministratorId();
        List<AdministratorDto> toBeFoundAdministrator = new ArrayList<>();
        toBeFoundAdministrator.add(new AdministratorDto(id, "Administrator",
            "One", "administrator1@email.com", "OnePassword", false, true));

        UserDtoSearch search = new UserDtoSearch("administrator1@email.com", "Administrator", "One");
        List<AdministratorDtoSparse> foundAdministrator = administratorService.searchAdministrators(search);
        assertThat(foundAdministrator).isNotNull().hasSize(1);
        assertAll("Grouped Assertions of Administrator",
            () -> assertEquals(toBeFoundAdministrator.get(0).id(), foundAdministrator.get(0).id(), "ID should be equal"),
            () -> assertEquals(toBeFoundAdministrator.get(0).email(), foundAdministrator.get(0).email(), "Email should be equal"),
            () -> assertEquals(toBeFoundAdministrator.get(0).firstname(), foundAdministrator.get(0).firstname(), "Firstname should be equal"),
            () -> assertEquals(toBeFoundAdministrator.get(0).lastname(), foundAdministrator.get(0).lastname(), "Lastname should be equal"),
            () -> assertEquals(toBeFoundAdministrator.get(0).isInitialPassword(), foundAdministrator.get(0).isInitialPassword(), "InitialPassword should be equal"));
    }

    @Transactional
    @Test
    public void givenUserDtoSearchWithNonExistentName_whenSearchSecretaries_thenReturnsEmptyList() {
        UserDtoSearch search = new UserDtoSearch("x.x@x.x", "x", "x");
        List<AdministratorDtoSparse> foundAdministrator = administratorService.searchAdministrators(search);
        assertThat(foundAdministrator).isNotNull().hasSize(0);
    }

    @Transactional
    @Test
    @WithMockUser(username = "administrator1@email.com", authorities = {"ADMIN"})
    public void givenRightUsername_whenIsOwnRequest_thenReturnsTrue() {
        long id = administratorRepository.findAll().get(0).getAdministratorId();
        boolean isOwnRequest = administratorService.isOwnRequest(id);
        assertThat(isOwnRequest).isTrue();
    }

    @Transactional
    @Test
    @WithMockUser(username = "Administrator", authorities = {"ADMIN"})
    public void givenWrongUsername_whenIsOwnRequest_thenReturnsFalse() {
        long id = administratorRepository.findAll().get(0).getAdministratorId();
        boolean isOwnRequest = administratorService.isOwnRequest(id);
        assertThat(isOwnRequest).isFalse();
    }
}
