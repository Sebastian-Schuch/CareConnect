package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StayDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StayDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StayDtoPage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Stay;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StayMapper {

    private final InpatientDepartmentMapper inpatientDepartmentMapper;

    public StayMapper(InpatientDepartmentMapper inpatientDepartmentMapper) {
        this.inpatientDepartmentMapper = inpatientDepartmentMapper;
    }

    public StayDto stayEntityToStayDto(Stay stay) {
        return new StayDto(stay.getId(), inpatientDepartmentMapper.inpatientDepartmentToDto(stay.getInpatientDepartment()), stay.getArrival(), stay.getDeparture());
    }

    public Stay stayDtoArrivalToStayEntity(StayDtoCreate stayDtoCreate) {
        //return new Stay(null, inpatientDepartmentMapper.inpatientDepartmentDtoToInpatientDepartmentEntity(stayDtoArrival.inpatientDepartment()), stayDtoArrival.arrival(), null);
        return null;
    }

    public StayDtoPage stayEntityListToStayDtoList(Page<Stay> stays) {
        List<StayDto> stayDtos = new ArrayList<>();
        for (Stay stay : stays.getContent()) {
            stayDtos.add(stayEntityToStayDto(stay));
        }
        return new StayDtoPage(stayDtos, (int) stays.getTotalElements());
    }
}
