package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDtoDetail;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SecretaryMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Secretary;
import at.ac.tuwien.sepr.groupphase.backend.repository.SecretaryRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.CredentialService;
import at.ac.tuwien.sepr.groupphase.backend.service.SecretaryService;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

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
        return insertSecretaryData(toCreate);
    }

    /**
     * Write the secretary given into the repository.
     *
     * @param toCreate the secretary to write into the repository
     * @return the secretary that has been written into the repository
     */
    public SecretaryDtoDetail insertSecretaryData(SecretaryDtoCreate toCreate) {
        LOG.debug("insertSecretaryData{}", toCreate);
        Secretary secretary = new Secretary();
        secretary.setCredential(credentialService.createCredentialEntity(toCreate.toCredentialDtoCreate(), Role.SECRETARY));
        return secretaryMapper.secretaryEntityToSecretaryDtoDetail(secretaryRepository.save(secretary));
    }
}

