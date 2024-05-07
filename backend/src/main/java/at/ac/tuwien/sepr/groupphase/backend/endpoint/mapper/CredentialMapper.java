package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CredentialDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import jdk.jfr.Name;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;

@Mapper
public interface CredentialMapper {
    @Name("credential")
    CredentialDto credentialToCredentialDto(Credential credential);

    @IterableMapping(qualifiedByName = "credential")
    CredentialDto credentialToDto(Credential credential);
}
