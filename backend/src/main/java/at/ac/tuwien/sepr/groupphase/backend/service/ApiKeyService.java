package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApiKeyDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApiKeyDtoFirst;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApiKeyPageDto;
import org.springframework.data.domain.Pageable;

public interface ApiKeyService {
    /**
     * Get a list of API keys.
     *
     * @param pageable the page information
     * @return a list of API keys
     */
    ApiKeyPageDto getApiKeys(Pageable pageable);

    /**
     * Create a new API key.
     *
     * @param apiKeyDtoCreate the API key to create
     * @return the created API key
     */
    ApiKeyDtoFirst createApiKey(ApiKeyDtoCreate apiKeyDtoCreate);

    /**
     * Delete an API key.
     *
     * @param id the API key id to delete
     */
    void deleteApiKey(long id);
}
