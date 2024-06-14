package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.InpatientDepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.InpatientDepartmentPageDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.InpatientDepartment;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InpatientDepartmentMapper {
    @IterableMapping(qualifiedByName = "inpatientDepartment")
    InpatientDepartmentDto inpatientDepartmentToDto(InpatientDepartment inpatientDepartment);

    List<InpatientDepartmentDto> inpatientDepartmentToDto(List<InpatientDepartment> inpatientDepartment);

    InpatientDepartment dtoToInpatientDepartment(InpatientDepartmentDto inpatientDepartmentDto);

    default InpatientDepartmentPageDto toInpatientDepartmentPageDto(Page<InpatientDepartment> inpatientDepartmentPage) {
        return new InpatientDepartmentPageDto(inpatientDepartmentToDto(inpatientDepartmentPage.getContent()), (int) inpatientDepartmentPage.getTotalElements());
    }
}
