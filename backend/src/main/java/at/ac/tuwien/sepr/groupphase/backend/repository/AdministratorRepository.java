package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Administrator;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
    @Transactional
    @Query(value = "SELECT * FROM Administrator WHERE id= ?1", nativeQuery = true)
    Administrator findAdministratorById(Long id);

    Administrator findByCredential_Email(String email);

    @Query("SELECT d.credential FROM Administrator d")
    List<Credential> findAllAdministratorCredentials();

    Administrator findByCredential(Credential credential);

    @Query("SELECT d FROM Administrator d WHERE (?1 IS NULL OR UPPER(d.credential.email) LIKE %?1% ) AND ( ?2 IS NULL OR UPPER(d.credential.firstName) LIKE %?2% ) AND "
        + "( ?3 IS NULL OR UPPER(d.credential.lastName) LIKE %?3% ) AND d.credential.active ORDER BY d.credential.lastName ASC")
    List<Administrator> searchAdministrator(String email, String firstName, String lastName);
}
