package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDtoDetail;
import at.ac.tuwien.sepr.groupphase.backend.entity.Secretary;
import org.mapstruct.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

@Mapper
public class SecretaryMapper {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Converts the secretary entity to the SecretaryDtoDetail.
     *
     * @param secretary the secretary to convert
     * @return the SecretaryDtoDetail
     */
    public SecretaryDtoDetail secretaryEntityToSecretaryDtoDetail(Secretary secretary) {
        LOG.trace("secretaryEntityToSecretaryDtoDetail({})", secretary);
        return new SecretaryDtoDetail(secretary.getCredential().getId(), secretary.getCredential().getLastName(), secretary.getCredential().getLastName(), secretary.getCredential().getEmail(), secretary.getCredential().getPassword(),
            secretary.getCredential().getActive());
    }
}
