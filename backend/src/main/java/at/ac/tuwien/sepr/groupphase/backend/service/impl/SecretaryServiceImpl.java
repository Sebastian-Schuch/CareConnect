package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDtoDetail;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SecretaryMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Secretary;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.SecretaryRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.CredentialService;
import at.ac.tuwien.sepr.groupphase.backend.service.SecretaryService;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class SecretaryServiceImpl implements SecretaryService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final CredentialService credentialService;
    private final SecretaryRepository secretaryRepository;
    private final SecretaryMapper secretaryMapper;

    public SecretaryServiceImpl(CredentialService credentialService, SecretaryRepository secretaryRepository, SecretaryMapper secretaryMapper) {

        this.credentialService = credentialService;
        this.secretaryRepository = secretaryRepository;
        this.secretaryMapper = secretaryMapper;
    }

    @Override
    public SecretaryDtoDetail create(SecretaryDtoCreate toCreate) {
        LOG.trace("create{}", toCreate);
        Secretary secretary = new Secretary();
        secretary.setCredential(credentialService.createCredentialEntity(toCreate.toCredentialDtoCreate(), Role.SECRETARY));
        return secretaryMapper.secretaryEntityToSecretaryDtoDetail(secretaryRepository.save(secretary));
    }

    @Override
    public SecretaryDtoDetail getById(Long id) {
        LOG.trace("getById({})", id);
        Secretary secretary = secretaryRepository.findSecretaryById(id);
        if (secretary == null) {
            throw new NotFoundException("Secretary not found");
        }
        return secretaryMapper.secretaryEntityToSecretaryDtoDetail(secretary);
    }

    @Override
    public List<SecretaryDtoDetail> getAllSecretaries() {
        return secretaryMapper.secretaryEntitiesToListOfSecretaryDtoDetail(secretaryRepository.findAll());
    }
}

