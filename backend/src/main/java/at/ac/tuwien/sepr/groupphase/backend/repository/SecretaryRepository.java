package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Secretary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecretaryRepository extends JpaRepository<Secretary, Long> {
    @Query("SELECT s.credential FROM Secretary s")
    List<Credential> findAllSecretariesCredentials();

    Secretary findByCredential(Credential credential);
}
