package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDtoUpdate;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Secretary;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.mapstruct.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Mapper
public class SecretaryMapper {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Converts the secretary entity to the SecretaryDtoDetail.
     *
     * @param secretary the secretary to convert
     * @return the SecretaryDtoDetail
     */
    public SecretaryDto secretaryEntityToSecretaryDtoDetail(Secretary secretary) {
        LOG.trace("secretaryEntityToSecretaryDtoDetail({})", secretary);
        return new SecretaryDto(secretary.getSecretaryId(), secretary.getCredential().getFirstName(), secretary.getCredential().getLastName(), secretary.getCredential().getEmail(), secretary.getCredential().getPassword(),
            secretary.getCredential().getActive());
    }

    /**
     * Converts the secretary entity list into a SecretaryDtoDetail list.
     *
     * @param secretaries the secretaries to convert
     * @return the converted secretaries
     */
    public List<SecretaryDto> secretaryEntitiesToListOfSecretaryDtoDetail(List<Secretary> secretaries) {
        LOG.trace("secretaryEntitiesToListOfSecretaryDtoDetail({})", secretaries);
        List<SecretaryDto> secretariesDto = new ArrayList<>();
        for (Secretary secretary : secretaries) {
            secretariesDto.add(secretaryEntityToSecretaryDtoDetail(secretary));
        }
        return secretariesDto;
    }

    public Secretary updateDtoToEntity(SecretaryDtoUpdate toUpdate, Secretary secretary) {
        LOG.trace("dtoToEntity({})", toUpdate);
        Secretary secretaryUpdate = new Secretary();
        secretaryUpdate.setSecretaryId(secretary.getSecretaryId());
        Credential credential = new Credential();
        credential.setRole(Role.SECRETARY);
        credential.setPassword(secretary.getCredential().getPassword());
        credential.setId(secretary.getCredential().getId());
        credential.setFirstName(toUpdate.firstname());
        credential.setLastName(toUpdate.lastname());
        credential.setEmail(toUpdate.email());
        credential.setActive(secretary.getCredential().getActive());
        secretaryUpdate.setCredential(credential);
        return secretaryUpdate;
    }
}
