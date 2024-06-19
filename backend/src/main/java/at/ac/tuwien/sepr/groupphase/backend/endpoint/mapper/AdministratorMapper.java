package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdministratorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdministratorDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdministratorDtoUpdate;
import at.ac.tuwien.sepr.groupphase.backend.entity.Administrator;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.mapstruct.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Mapper
public class AdministratorMapper {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Converts the administrator entity to the administratorDto.
     *
     * @param administrator the administrator to convert
     * @return the administratorDto
     */
    public AdministratorDto administratorToAdministratorDto(Administrator administrator) {
        LOG.trace("administratorToAdministratorDto({})", administrator);
        return new AdministratorDto(administrator.getAdministratorId(), administrator.getCredential().getFirstName(), administrator.getCredential().getLastName(), administrator.getCredential().getEmail(),
            administrator.getCredential().getPassword(),
            administrator.getCredential().isInitialPassword(),
            administrator.getCredential().getActive());
    }

    /**
     * Converts the administrator entity to the administratorDtoSparse without the password.
     *
     * @param administrator the administrator to convert
     * @return the administratorDto
     */
    public AdministratorDtoSparse administratorToAdministratorDtoSparse(Administrator administrator) {
        LOG.trace("administratorToAdministratorDtoSparse({})", administrator);
        return new AdministratorDtoSparse(administrator.getAdministratorId(), administrator.getCredential().getFirstName(), administrator.getCredential().getLastName(), administrator.getCredential().getEmail(),
            administrator.getCredential().isInitialPassword());
    }

    /**
     * Converts all the administrator entities to administratorDtos.
     *
     * @param administrators the administrator entities to convert
     * @return the converted administratorDtos
     */
    public List<AdministratorDtoSparse> administratorsToAdministratorDtosSparse(List<Administrator> administrators) {
        LOG.trace("administratorToAdministratorDtos({})", administrators);
        List<AdministratorDtoSparse> administratorDtos = new ArrayList<>();
        for (Administrator administrator : administrators) {
            administratorDtos.add(administratorToAdministratorDtoSparse(administrator));
        }
        return administratorDtos;
    }

    public Administrator updateDtoToEntity(AdministratorDtoUpdate toUpdate, Administrator administrator) {
        LOG.trace("dtoToEntity({})", toUpdate);
        Administrator administratorUpdate = new Administrator();
        administratorUpdate.setAdministratorId(administrator.getAdministratorId());
        Credential credential = new Credential();
        credential.setRole(Role.ADMIN);
        credential.setPassword(administrator.getCredential().getPassword());
        credential.setId(administrator.getCredential().getId());
        credential.setFirstName(toUpdate.firstname());
        credential.setLastName(toUpdate.lastname());
        credential.setEmail(toUpdate.email());
        credential.setActive(administrator.getCredential().getActive());
        administratorUpdate.setCredential(credential);
        return administratorUpdate;
    }
}
