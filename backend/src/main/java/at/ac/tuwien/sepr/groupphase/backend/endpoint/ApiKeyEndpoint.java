package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApiKeyDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApiKeyDtoFirst;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApiKeyPageDto;
import at.ac.tuwien.sepr.groupphase.backend.service.ApiKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiKeyEndpoint.BASE_PATH)
public class ApiKeyEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(ApiKeyEndpoint.class);

    public static final String BASE_PATH = "/api/v1/api-keys";

    private final ApiKeyService apiKeyService;

    public ApiKeyEndpoint(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @Secured({"ADMIN"})
    @GetMapping
    public ApiKeyPageDto getApiKeys(@RequestParam(name = "page") int page, @RequestParam(name = "size") int size) {
        LOG.info("getApiKeys method called with page: {} and size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "created");
        return this.apiKeyService.getApiKeys(pageable);
    }

    @Secured({"ADMIN"})
    @PostMapping
    public ApiKeyDtoFirst createApiKey(@RequestBody ApiKeyDtoCreate createApiKeyDto) {
        LOG.info("createApiKey method called with createApiKeyDto: {}", createApiKeyDto);
        return this.apiKeyService.createApiKey(createApiKeyDto);
    }

    @Secured({"ADMIN"})
    @DeleteMapping("/{id}")
    public void deleteApiKey(@PathVariable("id") long id) {
        LOG.info("deleteApiKey method called with id: {}", id);
        this.apiKeyService.deleteApiKey(id);
    }

}
