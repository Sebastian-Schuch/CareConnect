package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CredentialCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CredentialDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.repository.CredentialReposetory;
import at.ac.tuwien.sepr.groupphase.backend.service.CredentialService;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.springframework.stereotype.Service;

@Service
public class CredentialServiceImpl implements CredentialService {
    CredentialReposetory credentialReposetory;

    public CredentialServiceImpl(CredentialReposetory credentialReposetory) {
        this.credentialReposetory = credentialReposetory;
    }

    @Override
    public CredentialDto createCredential(CredentialCreateDto toCreate, Role role) {
        insertCredentialData();
        return null;
    }

    public void insertCredentialData() {
        Credential credential = new Credential();
        credential.setId(-1L);
        credential.setEmail("a@a.a");
        credential.setFirstName("a");
        credential.setLastName("b");
        credential.setPassword("password");
        credential.setActive(true);

        credentialReposetory.save(credential);
    }
}
