package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Secretary;
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
    public SecretaryDetailDto secretaryEntityToSecretaryDtoDetail(Secretary secretary) {
        LOG.trace("secretaryEntityToSecretaryDtoDetail({})", secretary);
        return new SecretaryDetailDto(secretary.getSecretaryId(), secretary.getCredential().getFirstName(), secretary.getCredential().getLastName(), secretary.getCredential().getEmail(), secretary.getCredential().getPassword(),
            secretary.getCredential().getActive());
    }

    /**
     * Converts the secretary entity list into a SecretaryDtoDetail list.
     *
     * @param secretaries the secretaries to convert
     * @return the converted secretaries
     */
    public List<SecretaryDetailDto> secretaryEntitiesToListOfSecretaryDtoDetail(List<Secretary> secretaries) {
        LOG.trace("secretaryEntitiesToListOfSecretaryDtoDetail({})", secretaries);
        List<SecretaryDetailDto> secretariesDto = new ArrayList<>();
        for (Secretary secretary : secretaries) {
            secretariesDto.add(secretaryEntityToSecretaryDtoDetail(secretary));
        }
        return secretariesDto;
    }

}
