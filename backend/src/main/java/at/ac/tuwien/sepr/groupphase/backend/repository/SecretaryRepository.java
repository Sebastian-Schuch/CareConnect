package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Secretary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecretaryRepository extends JpaRepository<Secretary, Long>, JpaSpecificationExecutor<Secretary> {
    @Query("SELECT s.credential FROM Secretary s WHERE s.credential.active = TRUE")
    List<Credential> findAllSecretariesCredentials();

    Secretary findByCredentialAndCredential_ActiveTrue(Credential credential);

    @Query("SELECT s FROM Secretary s WHERE (?1 IS NULL OR UPPER(s.credential.email) LIKE %?1% ) AND ( ?2 IS NULL OR UPPER(s.credential.firstName) LIKE %?2% ) AND "
        + "( ?3 IS NULL OR UPPER(s.credential.lastName) LIKE %?3% ) AND s.credential.active ORDER BY s.credential.lastName ASC")
    List<Secretary> searchSecretary(String email, String firstName, String lastName);

    List<Secretary> findByCredential_ActiveTrue();

    Secretary findBySecretaryIdAndCredential_ActiveTrue(Long id);
}
