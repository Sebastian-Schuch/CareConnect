package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AllergyDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Allergy;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface AllergyMapper {
    @IterableMapping(qualifiedByName = "allergy")
    AllergyDto allergyToDto(Allergy allergy);

    List<AllergyDto> allergyToDto(List<Allergy> allergy);
}
