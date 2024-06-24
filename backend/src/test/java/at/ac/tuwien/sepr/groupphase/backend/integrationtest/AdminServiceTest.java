package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.TestBase;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdminDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdminDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdminDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdminDtoUpdate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.entity.Admin;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.AdminRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.AdminService;
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
public class AdminServiceTest extends TestBase {
    @Autowired
    private AdminService adminService;

    @Autowired
    AdminRepository adminRepository;

    public AdminServiceTest() {
        super("administrator");
    }


    @Transactional
    @Test
    public void givenValidAdministratorCreateDto_whenCreateAdministrator_thenCreatedAdministratorIsReturned() {
        AdminDtoCreate toCreate = new AdminDtoCreate("a@a.a", "a", "b");
        Credential credential = new Credential();
        credential.setEmail("a@a.a");
        credential.setActive(true);
        credential.setFirstName("a");
        credential.setLastName("b");
        credential.setPassword("password");
        credential.setInitialPassword(false);
        credential.setRole(Role.ADMIN);
        AdminDto createdAdministrator = adminService.createAdministrator(toCreate, credential);
        assertAll("Grouped Assertions of Administrator",
            () -> assertEquals(toCreate.email(), createdAdministrator.email(), "email should be equal"),
            () -> assertEquals(toCreate.firstname(), createdAdministrator.firstname(), "firstname should be equal"),
            () -> assertEquals(toCreate.lastname(), createdAdministrator.lastname(), "lastname should be equal"));
    }

    @Transactional
    @Test
    public void givenIdOfAdministrator_whenGetAdministratorById_thenAdministratorIsReturned() {
        Admin Admin = adminRepository.findAll().get(0);
        AdminDto toBeFoundAdministrator = new AdminDto(Admin.getAdminId(), Admin.getCredential().getFirstName(),
            Admin.getCredential().getLastName(), Admin.getCredential().getEmail(), Admin.getCredential().getPassword(), Admin.getCredential().isInitialPassword(), Admin.getCredential().getActive());
        AdminDtoSparse foundAdministrator = adminService.getAdministratorById(toBeFoundAdministrator.id());
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
        long id = adminRepository.findAll().get(0).getAdminId();
        AdminDtoUpdate updateAdministrator = new AdminDtoUpdate("x", "y", "a@a.a", true, false);

        AdminDtoSparse foundAdministrator = adminService.getAdministratorById(id);
        AdminDtoSparse finalFoundAdministrator1 = foundAdministrator;

        assertAll("Grouped Assertions of Administrator",
            () -> assertNotEquals(updateAdministrator.email(), finalFoundAdministrator1.email(), "Email shouldn't be equal"),
            () -> assertNotEquals(updateAdministrator.firstname(), finalFoundAdministrator1.firstname(), "Firstname shouldn't be equal"),
            () -> assertNotEquals(updateAdministrator.lastname(), finalFoundAdministrator1.lastname(), "Lastname shouldn't be equal"));

        AdminDtoSparse updatedAdministrator = adminService.updateAdministrator(id, updateAdministrator);
        assertAll("Grouped Assertions of Administrator",
            () -> assertEquals(updateAdministrator.email(), updatedAdministrator.email(), "Email should be equal"),
            () -> assertEquals(updateAdministrator.firstname(), updatedAdministrator.firstname(), "Firstname should be equal"),
            () -> assertEquals(updateAdministrator.lastname(), updatedAdministrator.lastname(), "Lastname should be equal"));

        foundAdministrator = adminService.getAdministratorById(id);
        AdminDtoSparse finalFoundAdministrator = foundAdministrator;
        assertAll("Grouped Assertions of Administrator",
            () -> assertEquals(updateAdministrator.email(), finalFoundAdministrator.email(), "Email should be equal"),
            () -> assertEquals(updateAdministrator.firstname(), finalFoundAdministrator.firstname(), "Firstname should be equal"),
            () -> assertEquals(updateAdministrator.lastname(), finalFoundAdministrator.lastname(), "Lastname should be equal"));
    }

    @Transactional
    @Test
    public void givenAdministratorDtoUpdateWithNonExistentId_whenUpdateAdministrator_thenThrowsNotFoundAndAdministratorDoesntGetUpdated() {
        long id = adminRepository.findAll().get(0).getAdminId();
        AdminDtoUpdate updateAdministrator = new AdminDtoUpdate("x", "y", "a@a.a", true, false);

        AdminDtoSparse foundAdministrator = adminService.getAdministratorById(id);
        AdminDtoSparse finalFoundAdministrator1 = foundAdministrator;

        assertAll("Grouped Assertions of Administrator",
            () -> assertNotEquals(updateAdministrator.email(), finalFoundAdministrator1.email(), "Email shouldn't be equal"),
            () -> assertNotEquals(updateAdministrator.firstname(), finalFoundAdministrator1.firstname(), "Firstname shouldn't be equal"),
            () -> assertNotEquals(updateAdministrator.lastname(), finalFoundAdministrator1.lastname(), "Lastname shouldn't be equal"));

        assertThrows(NotFoundException.class, () -> adminService.updateAdministrator(-1L, updateAdministrator));

        foundAdministrator = adminService.getAdministratorById(id);
        AdminDtoSparse finalFoundAdministrator = foundAdministrator;
        assertAll("Grouped Assertions of Administrator",
            () -> assertNotEquals(updateAdministrator.email(), finalFoundAdministrator.email(), "Email should be equal"),
            () -> assertNotEquals(updateAdministrator.firstname(), finalFoundAdministrator.firstname(), "Firstname should be equal"),
            () -> assertNotEquals(updateAdministrator.lastname(), finalFoundAdministrator.lastname(), "Lastname should be equal"));
    }

    @Transactional
    @Test
    public void givenIdOfNonExistentAdministrator_whenGetAdministratorById_thenThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> adminService.getAdministratorById(-100L));
    }

    @Transactional
    @Test
    public void givenEmailOfAdministrator_whenGetAdministratorByEmail_thenAdministratorIsReturned() {
        long id = adminRepository.findAll().get(0).getAdminId();
        AdminDto toBeFoundAdministrator = new AdminDto(id, "Administrator",
            "One", "administrator1@email.com", "OnePassword", false, true);
        AdminDto foundAdministrator = adminService.getAdministratorByEmail(toBeFoundAdministrator.email());
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
        adminRepository.deleteAll();
        AdminDtoCreate toCreate1 = new AdminDtoCreate("a@a.a", "a", "b");
        Credential credential1 = new Credential();
        credential1.setEmail("a@a.a");
        credential1.setActive(true);
        credential1.setFirstName("a");
        credential1.setLastName("b");
        credential1.setPassword("password");
        credential1.setInitialPassword(false);
        credential1.setRole(Role.ADMIN);
        AdminDto createdAdministrator1 = adminService.createAdministrator(toCreate1, credential1);
        AdminDtoCreate toCreate2 = new AdminDtoCreate("b@b.b", "a", "b");
        Credential credential2 = new Credential();
        credential2.setEmail("b@b.b");
        credential2.setActive(true);
        credential2.setFirstName("a");
        credential2.setLastName("b");
        credential2.setPassword("password");
        credential2.setInitialPassword(false);
        credential2.setRole(Role.ADMIN);
        AdminDto createdAdministrator2 = adminService.createAdministrator(toCreate2, credential2);
        AdminDtoCreate toCreate3 = new AdminDtoCreate("c@c.c", "a", "b");
        Credential credential3 = new Credential();
        credential3.setEmail("c@c.c");
        credential3.setActive(true);
        credential3.setFirstName("a");
        credential3.setLastName("b");
        credential3.setPassword("password");
        credential3.setInitialPassword(false);
        credential3.setRole(Role.ADMIN);
        AdminDto createdAdministrator3 = adminService.createAdministrator(toCreate3, credential3);

        List<AdminDtoSparse> allSecretaries = adminService.getAllAdministrators();
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
        adminRepository.deleteAll();
        List<AdminDtoSparse> allSecretaries = adminService.getAllAdministrators();
        assertThat(allSecretaries).isEmpty();
    }

    @Transactional
    @Test
    public void givenIdOfAdministrator_whenGetAdministratorEntityById_thenAdministratorEntityIsReturned() {
        Admin toBeFoundAdmin = adminRepository.findAll().get(0);
        Admin foundAdmin = adminService.getAdministratorEntityById(toBeFoundAdmin.getAdminId());
        assertAll("Grouped Assertions of Administrator",
            () -> assertEquals(toBeFoundAdmin.getAdminId(), foundAdmin.getAdminId(), "ID should be equal"),
            () -> assertEquals(toBeFoundAdmin.getCredential().getEmail(), foundAdmin.getCredential().getEmail(), "Email should be equal"),
            () -> assertEquals(toBeFoundAdmin.getCredential().getFirstName(), foundAdmin.getCredential().getFirstName(), "Firstname should be equal"),
            () -> assertEquals(toBeFoundAdmin.getCredential().getLastName(), foundAdmin.getCredential().getLastName(), "Lastname should be equal"),
            () -> assertEquals(toBeFoundAdmin.getCredential().getPassword(), foundAdmin.getCredential().getPassword(), "Password should be equal"),
            () -> assertEquals(toBeFoundAdmin.getCredential().getActive(), foundAdmin.getCredential().getActive(), "Active should be equal"),
            () -> assertEquals(toBeFoundAdmin.getCredential().isInitialPassword(), foundAdmin.getCredential().isInitialPassword(), "InitialPassword should be equal"));
    }


    @Transactional
    @Test
    public void givenNonExistentIdOfAdministrator_whenGetAdministratorEntityById_thenThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> adminService.getAdministratorEntityById(-1L));
    }

    @Transactional
    @Test
    public void givenCredentialOfAdministrator_whenFindAdministratorByCredential_thenCredentialIsReturned() {
        long id = adminRepository.findAll().get(0).getAdminId();
        AdminDto toBeFoundAdministrator = new AdminDto(id, "Administrator",
            "One", "administrator1@email.com", "OnePassword", false, true);
        AdminDto foundAdministrator = adminService.findAdministratorByCredential(adminRepository.findAll().get(0).getCredential());
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
        Credential credential = adminRepository.findAll().get(0).getCredential();
        adminRepository.deleteById(adminRepository.findAll().get(0).getAdminId());
        assertThrows(NotFoundException.class, () -> adminService.findAdministratorByCredential(credential));
    }

    @Transactional
    @Test
    public void givenUserDtoSearch_whenSearchSecretaries_thenReturnFoundSecretaries() {
        long id = adminRepository.findAll().get(0).getAdminId();
        List<AdminDto> toBeFoundAdministrator = new ArrayList<>();
        toBeFoundAdministrator.add(new AdminDto(id, "Administrator",
            "One", "administrator1@email.com", "OnePassword", false, true));

        UserDtoSearch search = new UserDtoSearch("administrator1@email.com", "Administrator", "One");
        List<AdminDtoSparse> foundAdministrator = adminService.searchAdministrators(search);
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
        List<AdminDtoSparse> foundAdministrator = adminService.searchAdministrators(search);
        assertThat(foundAdministrator).isNotNull().hasSize(0);
    }

    @Transactional
    @Test
    @WithMockUser(username = "administrator1@email.com", authorities = {"ADMIN"})
    public void givenRightUsername_whenIsOwnRequest_thenReturnsTrue() {
        long id = adminRepository.findAll().get(0).getAdminId();
        boolean isOwnRequest = adminService.isOwnRequest(id);
        assertThat(isOwnRequest).isTrue();
    }

    @Transactional
    @Test
    @WithMockUser(username = "Administrator", authorities = {"ADMIN"})
    public void givenWrongUsername_whenIsOwnRequest_thenReturnsFalse() {
        long id = adminRepository.findAll().get(0).getAdminId();
        boolean isOwnRequest = adminService.isOwnRequest(id);
        assertThat(isOwnRequest).isFalse();
    }
}
