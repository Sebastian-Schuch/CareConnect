package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdminDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdminDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdminDtoUpdate;
import at.ac.tuwien.sepr.groupphase.backend.entity.Admin;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.mapstruct.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Mapper
public class AdminMapper {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Converts the administrator entity to the administratorDto.
     *
     * @param admin the administrator to convert
     * @return the administratorDto
     */
    public AdminDto administratorToAdministratorDto(Admin admin) {
        LOG.trace("administratorToAdministratorDto({})", admin);
        return new AdminDto(admin.getAdminId(), admin.getCredential().getFirstName(), admin.getCredential().getLastName(), admin.getCredential().getEmail(),
            admin.getCredential().getPassword(),
            admin.getCredential().isInitialPassword(),
            admin.getCredential().getActive());
    }

    /**
     * Converts the administrator entity to the administratorDtoSparse without the password.
     *
     * @param admin the administrator to convert
     * @return the administratorDto
     */
    public AdminDtoSparse administratorToAdministratorDtoSparse(Admin admin) {
        LOG.trace("administratorToAdministratorDtoSparse({})", admin);
        return new AdminDtoSparse(admin.getAdminId(), admin.getCredential().getFirstName(), admin.getCredential().getLastName(), admin.getCredential().getEmail(),
            admin.getCredential().isInitialPassword());
    }

    /**
     * Converts all the administrator entities to administratorDtos.
     *
     * @param admins the administrator entities to convert
     * @return the converted administratorDtos
     */
    public List<AdminDtoSparse> administratorsToAdministratorDtosSparse(List<Admin> admins) {
        LOG.trace("administratorToAdministratorDtos({})", admins);
        List<AdminDtoSparse> administratorDtos = new ArrayList<>();
        for (Admin admin : admins) {
            administratorDtos.add(administratorToAdministratorDtoSparse(admin));
        }
        return administratorDtos;
    }

    public Admin updateDtoToEntity(AdminDtoUpdate toUpdate, Admin admin) {
        LOG.trace("dtoToEntity({})", toUpdate);
        Admin adminUpdate = new Admin();
        adminUpdate.setAdminId(admin.getAdminId());
        Credential credential = new Credential();
        credential.setRole(Role.ADMIN);
        credential.setPassword(admin.getCredential().getPassword());
        credential.setId(admin.getCredential().getId());
        credential.setFirstName(toUpdate.firstname());
        credential.setLastName(toUpdate.lastname());
        credential.setEmail(toUpdate.email());
        credential.setActive(admin.getCredential().getActive());
        adminUpdate.setCredential(credential);
        return adminUpdate;
    }
}
