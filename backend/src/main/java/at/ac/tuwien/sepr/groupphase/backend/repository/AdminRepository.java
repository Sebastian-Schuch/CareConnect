package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Admin;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Admin findByCredential_EmailAndCredential_ActiveTrue(String email);

    @Query("SELECT d.credential FROM Admin d WHERE d.credential.active = TRUE")
    List<Credential> findAllAdministratorCredentials();

    Admin findByCredentialAndCredential_ActiveTrue(Credential credential);

    @Query("SELECT d FROM Admin d WHERE (?1 IS NULL OR UPPER(d.credential.email) LIKE %?1% ) AND ( ?2 IS NULL OR UPPER(d.credential.firstName) LIKE %?2% ) AND "
        + "( ?3 IS NULL OR UPPER(d.credential.lastName) LIKE %?3% ) AND d.credential.active ORDER BY d.credential.lastName ASC")
    List<Admin> searchAdministrator(String email, String firstName, String lastName);

    List<Admin> findByCredential_ActiveTrue();

    Admin findByAdminIdAndCredential_ActiveTrue(Long id);
}
