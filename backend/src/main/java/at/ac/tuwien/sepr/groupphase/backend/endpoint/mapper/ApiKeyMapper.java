package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApiKeyDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApiKeyDtoFirst;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApiKey;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ApiKeyMapper {
    ApiKeyDto apiKeyToApiKeyDto(ApiKey apiKey);

    List<ApiKeyDto> apiKeyListToApiKeyDtoList(List<ApiKey> apiKeyList);

    ApiKeyDtoFirst apiKeyToApiKeyDtoFirst(ApiKey apiKey);
}
