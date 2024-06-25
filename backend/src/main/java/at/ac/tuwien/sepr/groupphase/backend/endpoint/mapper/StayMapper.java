package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StayDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StayDtoPage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Stay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class StayMapper {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final InpatientDepartmentMapper inpatientDepartmentMapper;

    public StayMapper(InpatientDepartmentMapper inpatientDepartmentMapper) {
        LOG.trace("StayMapper(InpatientDepartmentMapper)");
        this.inpatientDepartmentMapper = inpatientDepartmentMapper;
    }

    public StayDto stayEntityToStayDto(Stay stay) {
        LOG.trace("stayEntityToStayDto({})", stay);
        return new StayDto(stay.getId(), inpatientDepartmentMapper.inpatientDepartmentToDto(stay.getInpatientDepartment()), stay.getArrival(), stay.getDeparture());
    }

    public StayDtoPage stayEntityListToStayDtoList(Page<Stay> stays) {
        LOG.trace("stayEntityListToStayDtoList({})", stays);
        List<StayDto> stayDtos = new ArrayList<>();
        for (Stay stay : stays.getContent()) {
            stayDtos.add(stayEntityToStayDto(stay));
        }
        return new StayDtoPage(stayDtos, (int) stays.getTotalElements());
    }
}
