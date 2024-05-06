package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredentialReposetory extends CrudRepository<Credential, Long> {
}
