package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDtoUpdate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SecretaryMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Secretary;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.SecretaryRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.SecretaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class SecretaryServiceImpl implements SecretaryService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final SecretaryRepository secretaryRepository;
    private final SecretaryMapper secretaryMapper;

    public SecretaryServiceImpl(SecretaryRepository secretaryRepository, SecretaryMapper secretaryMapper) {
        this.secretaryRepository = secretaryRepository;
        this.secretaryMapper = secretaryMapper;
    }

    @Override
    public SecretaryDto create(SecretaryDtoCreate toCreate, Credential credentials) {
        LOG.trace("create{}", toCreate);
        Secretary secretary = new Secretary();
        secretary.setCredential(credentials);
        return secretaryMapper.secretaryEntityToSecretaryDtoDetail(secretaryRepository.save(secretary));
    }

    @Override
    public SecretaryDtoSparse getById(Long id) {
        LOG.trace("getById({})", id);
        Secretary secretary = secretaryRepository.findBySecretaryIdAndCredential_ActiveTrue(id);
        if (secretary == null) {
            LOG.warn("secretary with id {} not found", id);
            throw new NotFoundException("Secretary not found");
        }
        return secretaryMapper.secretaryEntityToSecretaryDtoSparse(secretary);
    }

    @Override
    public Secretary getEntityById(Long id) {
        LOG.trace("getEntityById({})", id);
        Secretary secretary = secretaryRepository.findBySecretaryIdAndCredential_ActiveTrue(id);
        if (secretary == null) {
            LOG.warn("secretary with id {} not found", id);
            throw new NotFoundException("Secretary not found");
        }
        return secretary;
    }

    @Override
    public List<SecretaryDtoSparse> searchSecretaries(UserDtoSearch search) {
        LOG.trace("searchSecretaries({})", search);
        return secretaryMapper.secretaryEntitiesToListOfSecretaryDtoSparse(
            secretaryRepository.searchSecretary(this.makeStringSearchable(search.email()), this.makeStringSearchable(search.firstName()), this.makeStringSearchable(search.lastName())));
    }

    @Override
    public SecretaryDtoSparse updateSecretary(Long id, SecretaryDtoUpdate toUpdate) {
        LOG.trace("updateSecretary({}, {})", id, toUpdate);
        Secretary secretary = secretaryRepository.findBySecretaryIdAndCredential_ActiveTrue(id);
        if (secretary == null) {
            LOG.warn("secretary with id {} not found", id);
            throw new NotFoundException("Secretary not found");
        }
        return secretaryMapper.secretaryEntityToSecretaryDtoSparse(secretaryRepository.save(secretaryMapper.updateDtoToEntity(toUpdate, secretary)));
    }

    @Override
    public List<SecretaryDtoSparse> getAllSecretaries() {
        LOG.trace("getAllSecretaries()");
        return secretaryMapper.secretaryEntitiesToListOfSecretaryDtoSparse(secretaryRepository.findByCredential_ActiveTrue());
    }

    @Override
    public boolean isOwnRequest(Long userId) {
        LOG.trace("isOwnRequest({})", userId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("SECRETARY"))) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String email = principal.toString();
            if (principal instanceof UserDetails) {
                email = ((UserDetails) principal).getUsername();
            }
            try {
                Secretary secretary = this.getEntityById(userId);
                return email.equals(secretary.getCredential().getEmail());
            } catch (NotFoundException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public SecretaryDtoSparse findSecretaryByCredential(Credential credential) {
        LOG.debug("Find application user by email");
        Secretary secretary = secretaryRepository.findByCredentialAndCredential_ActiveTrue(credential);
        if (secretary != null) {
            return secretaryMapper.secretaryEntityToSecretaryDtoSparse(secretary);
        }
        throw new NotFoundException(String.format("Could not find the user with the credential %s", credential));
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

