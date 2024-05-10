package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CredentialCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.service.CredentialService;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.springframework.stereotype.Service;

@Service
public class CredentialServiceImpl implements CredentialService {
    public Credential createCredentialEntity(CredentialCreateDto toCreate, Role role) {
        Credential credential = new Credential();
        credential.setEmail(toCreate.email());
        credential.setFirstName(toCreate.firstname());
        credential.setLastName(toCreate.lastname());
        credential.setPassword(new java.math.BigInteger(130, new java.security.SecureRandom()).toString(32));
        credential.setActive(true);
        credential.setRole(role);
        credential.setInitialPassword(true);
        return credential;
    }
}
