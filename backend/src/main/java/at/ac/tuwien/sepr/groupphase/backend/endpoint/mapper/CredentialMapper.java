package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CredentialDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import jdk.jfr.Name;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface CredentialMapper {
    @Name("credential")
    CredentialDto credentialToCredentialDto(Credential credential);

    @IterableMapping(qualifiedByName = "credential")
    CredentialDto credentialToDto(Credential credential);


}
