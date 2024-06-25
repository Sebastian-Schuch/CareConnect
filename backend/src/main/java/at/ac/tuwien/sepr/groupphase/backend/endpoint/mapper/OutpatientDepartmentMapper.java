package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpeningHoursDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentPageDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.OpeningHours;
import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OutpatientDepartmentMapper {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private OpeningHoursMapper openingHoursMapper;

    public OutpatientDepartmentMapper(OpeningHoursMapper openingHoursMapper) {
        this.openingHoursMapper = openingHoursMapper;
    }

    /**
     * Maps a OutpatientDepartmentDto to an OutpatientDepartment entity.
     *
     * @param dto          the dto
     * @param openingHours the opening hours entity
     * @return the Entity
     */
    public OutpatientDepartment dtoToEntity(OutpatientDepartmentDto dto, OpeningHours openingHours) {
        LOG.trace("DtoToEntity(OutpatientDepartmentDto)");
        return new OutpatientDepartment().setId(dto.id())
            .setName(dto.name())
            .setDescription(dto.description())
            .setCapacity(dto.capacity())
            .setOpeningHours(openingHours)
            .setActive(dto.active());
    }

    /**
     * Maps a OutpatientDepartmentDtoCreate to an OutpatientDepartment entity.
     *
     * @param dto          the dto
     * @param openingHours the opening hours entity
     * @return the Entity
     */
    public OutpatientDepartment dtoToEntity(OutpatientDepartmentDtoCreate dto, OpeningHours openingHours) {
        LOG.trace("DtoToEntity(OutpatientDepartmentDtoCreate)");
        return new OutpatientDepartment().setId(null)
            .setName(dto.name())
            .setDescription(dto.description())
            .setCapacity(dto.capacity())
            .setOpeningHours(openingHours)
            .setActive(true);
    }

    /**
     * Maps an entity to a DTO.
     *
     * @param entity          the entity
     * @param openingHoursDto the opening hours DTO
     * @return the DTO
     */
    public OutpatientDepartmentDto entityToDto(OutpatientDepartment entity, OpeningHoursDto openingHoursDto) {
        LOG.trace("entityToDto()");
        return new OutpatientDepartmentDto(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getCapacity(),
            openingHoursDto,
            entity.isActive());
    }

    /**
     * Maps a list of OutpatientDepartment entities to a list of OutpatientDepartmentDto.
     *
     * @param entities the list of entities
     * @return the list of DTOs
     */
    public List<OutpatientDepartmentDto> entitiesToDtos(List<OutpatientDepartment> entities) {
        LOG.trace("entitiesToDtos()");
        return entities.stream()
            .map(entity -> {
                return entityToDto(entity, openingHoursMapper.entityToDto(entity.getOpeningHours()));
            })
            .collect(Collectors.toList());
    }

    public OutpatientDepartmentPageDto toOutpatientDepartmentPageDto(Page<OutpatientDepartment> outpatientDepartmentPage) {
        return new OutpatientDepartmentPageDto(entitiesToDtos(outpatientDepartmentPage.getContent()), (int) outpatientDepartmentPage.getTotalElements());
    }
}
