package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpeningHoursDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.entity.OpeningHours;
import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import org.mapstruct.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

@Mapper
public class OutpatientDepartmentMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Maps a OutpatientDepartmentDto to an OutpatientDepartment entity.
     *
     * @param dto          the dto
     * @param openingHours the opening hours entity
     * @return the Entity
     */
    public OutpatientDepartment dtoToEntity(OutpatientDepartmentDto dto, OpeningHours openingHours) {
        LOGGER.trace("DtoToEntity(OutpatientDepartmentDto)");
        return new OutpatientDepartment().setId(dto.id())
            .setName(dto.name())
            .setDescription(dto.description())
            .setCapacity(dto.capacity())
            .setOpeningHours(openingHours);
    }

    /**
     * Maps a OutpatientDepartmentDtoCreate to an OutpatientDepartment entity.
     *
     * @param dto          the dto
     * @param openingHours the opening hours entity
     * @return the Entity
     */
    public OutpatientDepartment dtoToEntity(OutpatientDepartmentDtoCreate dto, OpeningHours openingHours) {
        LOGGER.trace("DtoToEntity(OutpatientDepartmentDtoCreate)");
        return new OutpatientDepartment().setId(null)
            .setName(dto.name())
            .setDescription(dto.description())
            .setCapacity(dto.capacity())
            .setOpeningHours(openingHours);
    }

    /**
     * Maps an entity to a DTO.
     *
     * @param entity          the entity
     * @param openingHoursDto the opening hours DTO
     * @return the DTO
     */
    public OutpatientDepartmentDto entityToDto(OutpatientDepartment entity, OpeningHoursDto openingHoursDto) {
        LOGGER.trace("entityToDto()");
        return new OutpatientDepartmentDto(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getCapacity(),
            openingHoursDto);
    }
}
