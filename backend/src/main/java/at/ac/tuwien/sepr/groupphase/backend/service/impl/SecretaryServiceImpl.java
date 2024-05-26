package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDetailDto;
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
    public SecretaryDetailDto create(SecretaryCreateDto toCreate, Credential credentials) {
        LOG.trace("create{}", toCreate);
        Secretary secretary = new Secretary();
        secretary.setCredential(credentials);
        return secretaryMapper.secretaryEntityToSecretaryDtoDetail(secretaryRepository.save(secretary));
    }

    @Override
    public SecretaryDetailDto getById(Long id) {
        LOG.trace("getById({})", id);
        Secretary secretary = secretaryRepository.findById(id).orElse(null);
        if (secretary == null) {
            LOG.warn("secretary with id {} not found", id);
            throw new NotFoundException("Secretary not found");
        }
        return secretaryMapper.secretaryEntityToSecretaryDtoDetail(secretary);
    }

    @Override
    public List<SecretaryDetailDto> getAllSecretaries() {
        LOG.trace("getAllSecretaries()");
        return secretaryMapper.secretaryEntitiesToListOfSecretaryDtoDetail(secretaryRepository.findAll());
    }

    @Override
    public boolean isValidSecretaryRequest(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("SECRETARY"));
    }
}

