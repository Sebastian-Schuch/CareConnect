package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdministratorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdministratorDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdministratorDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdministratorDtoUpdate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.AdministratorMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Administrator;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.AdministratorRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.AdministratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class AdministratorServiceImpl implements AdministratorService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final AdministratorRepository administratorRepository;
    private final AdministratorMapper administratorMapper;

    public AdministratorServiceImpl(AdministratorRepository administratorRepository, AdministratorMapper administratorMapper) {
        this.administratorRepository = administratorRepository;
        this.administratorMapper = administratorMapper;
    }

    @Override
    public AdministratorDto createAdministrator(AdministratorDtoCreate toCreate, Credential credentials) {
        LOG.trace("createAdministrator({})", toCreate);
        Administrator administrator = new Administrator();
        administrator.setCredential(credentials);
        return administratorMapper.administratorToAdministratorDto(administratorRepository.save(administrator));
    }

    @Override
    public AdministratorDtoSparse getAdministratorById(Long id) {
        LOG.trace("getAdministratorById({})", id);
        Administrator administrator = administratorRepository.findById(id).orElse(null);
        if (administrator == null) {
            LOG.warn("administrator with id {} not found", id);
            throw new NotFoundException("Administrator not found");
        }
        return administratorMapper.administratorToAdministratorDtoSparse(administrator);
    }

    @Override
    public Administrator getAdministratorEntityById(Long id) {
        LOG.trace("getAdministratorEntityById({})", id);
        Administrator administrator = administratorRepository.findById(id).orElse(null);
        if (administrator == null) {
            LOG.warn("administrator with id {} not found", id);
            throw new NotFoundException("Administrator not found");
        }
        return administrator;
    }

    @Override
    public AdministratorDtoSparse updateAdministrator(Long id, AdministratorDtoUpdate toUpdate) {
        LOG.trace("updateAdministrator({}, {})", id, toUpdate);
        Administrator administrator = administratorRepository.findById(id).orElse(null);
        if (administrator == null) {
            LOG.warn("administrator with id {} not found", id);
            throw new NotFoundException("Administrator not found");
        }
        return administratorMapper.administratorToAdministratorDtoSparse(administratorRepository.save(administratorMapper.updateDtoToEntity(toUpdate, administrator)));
    }

    @Override
    public List<AdministratorDtoSparse> getAllAdministrators() {
        return administratorMapper.administratorsToAdministratorDtosSparse(administratorRepository.findAll());
    }

    @Override
    public AdministratorDto getAdministratorByEmail(String email) {
        return administratorMapper.administratorToAdministratorDto(administratorRepository.findByCredential_Email(email));
    }

    @Override
    public AdministratorDto findAdministratorByCredential(Credential credential) {
        LOG.debug("Find application user by email");
        Administrator administrator = administratorRepository.findByCredential(credential);
        if (administrator != null) {
            return administratorMapper.administratorToAdministratorDto(administrator);
        }
        throw new NotFoundException(String.format("Could not find the user with the credential %s", credential));
    }

    @Override
    public List<AdministratorDtoSparse> searchAdministrators(UserDtoSearch search) {
        LOG.trace("searchAdministrators({})", search);
        return administratorMapper.administratorsToAdministratorDtosSparse(
            administratorRepository.searchAdministrator(this.makeStringSearchable(search.email()), this.makeStringSearchable(search.firstName()), this.makeStringSearchable(search.lastName())));
    }

    @Override
    public boolean isOwnRequest(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"))) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String email = principal.toString();
            if (principal instanceof UserDetails) {
                email = ((UserDetails) principal).getUsername();
            }
            try {
                Administrator administrator = this.getAdministratorEntityById(userId);
                return email.equals(administrator.getCredential().getEmail());
            } catch (NotFoundException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Make a string searchable by converting it to upper case.
     *
     * @param input the string to make searchable
     * @return the searchable string
     */
    private String makeStringSearchable(String input) {
        String search = null;
        if (input != null) {
            search = input.toUpperCase();
        }
        return search;
    }
}
