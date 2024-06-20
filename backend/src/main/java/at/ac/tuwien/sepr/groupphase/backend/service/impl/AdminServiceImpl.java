package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdminDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdminDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdminDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdminDtoUpdate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.AdminMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Admin;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.AdminRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;

    public AdminServiceImpl(AdminRepository adminRepository, AdminMapper adminMapper) {
        this.adminRepository = adminRepository;
        this.adminMapper = adminMapper;
    }

    @Override
    public AdminDto createAdministrator(AdminDtoCreate toCreate, Credential credentials) {
        LOG.trace("createAdministrator({})", toCreate);
        Admin admin = new Admin();
        admin.setCredential(credentials);
        return adminMapper.administratorToAdministratorDto(adminRepository.save(admin));
    }

    @Override
    public AdminDtoSparse getAdministratorById(Long id) {
        LOG.trace("getAdministratorById({})", id);
        Admin admin = adminRepository.findById(id).orElse(null);
        if (admin == null) {
            LOG.warn("administrator with id {} not found", id);
            throw new NotFoundException("Administrator not found");
        }
        return adminMapper.administratorToAdministratorDtoSparse(admin);
    }

    @Override
    public Admin getAdministratorEntityById(Long id) {
        LOG.trace("getAdministratorEntityById({})", id);
        Admin admin = adminRepository.findById(id).orElse(null);
        if (admin == null) {
            LOG.warn("administrator with id {} not found", id);
            throw new NotFoundException("Administrator not found");
        }
        return admin;
    }

    @Override
    public AdminDtoSparse updateAdministrator(Long id, AdminDtoUpdate toUpdate) {
        LOG.trace("updateAdministrator({}, {})", id, toUpdate);
        Admin admin = adminRepository.findById(id).orElse(null);
        if (admin == null) {
            LOG.warn("administrator with id {} not found", id);
            throw new NotFoundException("Administrator not found");
        }
        return adminMapper.administratorToAdministratorDtoSparse(adminRepository.save(adminMapper.updateDtoToEntity(toUpdate, admin)));
    }

    @Override
    public List<AdminDtoSparse> getAllAdministrators() {
        return adminMapper.administratorsToAdministratorDtosSparse(adminRepository.findAll());
    }

    @Override
    public AdminDto getAdministratorByEmail(String email) {
        return adminMapper.administratorToAdministratorDto(adminRepository.findByCredential_Email(email));
    }

    @Override
    public AdminDto findAdministratorByCredential(Credential credential) {
        LOG.debug("Find application user by email");
        Admin admin = adminRepository.findByCredential(credential);
        if (admin != null) {
            return adminMapper.administratorToAdministratorDto(admin);
        }
        throw new NotFoundException(String.format("Could not find the user with the credential %s", credential));
    }

    @Override
    public List<AdminDtoSparse> searchAdministrators(UserDtoSearch search) {
        LOG.trace("searchAdministrators({})", search);
        return adminMapper.administratorsToAdministratorDtosSparse(
            adminRepository.searchAdministrator(this.makeStringSearchable(search.email()), this.makeStringSearchable(search.firstName()), this.makeStringSearchable(search.lastName())));
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
                Admin admin = this.getAdministratorEntityById(userId);
                return email.equals(admin.getCredential().getEmail());
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
