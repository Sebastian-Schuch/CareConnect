package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApiKeyDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApiKeyDtoFirst;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApiKeyPageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ApiKeyMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApiKey;
import at.ac.tuwien.sepr.groupphase.backend.repository.ApiKeyRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ApiKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

@Service
public class ApiKeyServiceImpl implements ApiKeyService {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    private final ApiKeyRepository apiKeyRepository;

    private final ApiKeyMapper apiKeyMapper;

    public ApiKeyServiceImpl(ApiKeyRepository apiKeyRepository,
                             ApiKeyMapper apiKeyMapper) {
        this.apiKeyRepository = apiKeyRepository;
        this.apiKeyMapper = apiKeyMapper;
    }

    @Override
    public ApiKeyPageDto getApiKeys(Pageable pageable) {
        LOG.trace("getApiKeys({})", pageable);
        Page<ApiKey> page = apiKeyRepository.findAll(pageable);

        return new ApiKeyPageDto(this.apiKeyMapper.apiKeyListToApiKeyDtoList(page.getContent()), page.getTotalElements());
    }

    @Override
    public ApiKeyDtoFirst createApiKey(ApiKeyDtoCreate apiKeyDtoCreate) {
        LOG.trace("createApiKey({})", apiKeyDtoCreate);
        ApiKey apiKey = new ApiKey().setApiKey(generateKey()).setCreated(new Date()).setDescription(apiKeyDtoCreate.description());
        apiKeyRepository.save(apiKey);
        return this.apiKeyMapper.apiKeyToApiKeyDtoFirst(apiKey);
    }

    @Override
    public void deleteApiKey(long id) {
        LOG.trace("deleteApiKey({})", id);
        apiKeyRepository.deleteById(id);
    }

    @Override
    public boolean checkApiKey(String apiKey) {
        LOG.trace("checkApiKey({})", apiKey);
        return this.apiKeyRepository.existsByApikey(apiKey);
    }

    /**
     * Generates a random key.
     *
     * @return the generated key
     */
    private String generateKey() {
        LOG.trace("generateKey()");
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }


}
