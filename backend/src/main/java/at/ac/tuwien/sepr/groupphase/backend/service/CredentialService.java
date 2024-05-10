package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CredentialCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;

public interface CredentialService {
    /**
     * Creates the Credential Entity with the data given.
     *
     * @param toCreate the data for the Credential Entity
     * @param role     the role of the credential
     * @return the credential entity
     */
    Credential createCredentialEntity(CredentialCreateDto toCreate, Role role);
}
