package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpeningHoursDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.entity.OpeningHours;
import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import org.mapstruct.Mapper;

@Mapper
public class OutpatientDepartmentMapper {
    public OutpatientDepartment DtoToEntity(OutpatientDepartmentDto dto, OpeningHours openingHours) {
        return new OutpatientDepartment().setId(dto.id())
            .setName(dto.name())
            .setDescription(dto.description())
            .setCapacity(dto.capacity())
            .setOpeningHours(openingHours);
    }

    public OutpatientDepartment DtoToEntity(OutpatientDepartmentDtoCreate dto, OpeningHours openingHours) {
        return new OutpatientDepartment().setId(null)
            .setName(dto.name())
            .setDescription(dto.description())
            .setCapacity(dto.capacity())
            .setOpeningHours(openingHours);
    }

    public OutpatientDepartmentDto entityToDto(OutpatientDepartment entity, OpeningHoursDto openingHoursDto) {
        return new OutpatientDepartmentDto(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getCapacity(),
            openingHoursDto);
    }
}
