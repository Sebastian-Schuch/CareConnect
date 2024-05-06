package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CredentialCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.CredentialMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.repository.CredentialRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.CredentialService;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.springframework.stereotype.Service;

@Service
public class CredentialServiceImpl implements CredentialService {
    private CredentialRepository credentialRepository;
    private CredentialMapper credentialMapper;

    public CredentialServiceImpl(CredentialRepository credentialReposetory, CredentialMapper credentialMapper) {
        this.credentialRepository = credentialReposetory;
        this.credentialMapper = credentialMapper;
    }

    @Override
    public Credential createCredential(CredentialCreateDto toCreate, Role role) {
        return insertCredentialData(toCreate, role);
    }

    public Credential insertCredentialData(CredentialCreateDto toCreate, Role role) {
        Credential credential = new Credential();
        credential.setEmail(toCreate.getEmail());
        credential.setFirstName(toCreate.getFirstname());
        credential.setLastName(toCreate.getLastname());
        credential.setPassword(new java.math.BigInteger(130, new java.security.SecureRandom()).toString(32));
        credential.setActive(true);
        credential.setRole(role);
        return credentialRepository.save(credential);
    }
}
