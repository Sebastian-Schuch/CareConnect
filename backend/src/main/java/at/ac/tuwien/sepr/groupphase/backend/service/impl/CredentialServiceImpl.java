package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CredentialCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CredentialDto;
import at.ac.tuwien.sepr.groupphase.backend.service.CredentialService;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.springframework.stereotype.Component;

@Component
public class CredentialServiceImpl implements CredentialService {

    @Override
    public CredentialDto createCredential(CredentialCreateDto toCreate, Role role) {
        return null;
    }
}
