package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorWorkingHoursDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInterfaceMapper {

    public List<DoctorWorkingHoursDto> doctorHoursObjectToDoctorWorkingHoursDto(List<Object[]> summedHours) {
        List<DoctorWorkingHoursDto> doctorWorkingHoursDtos = new ArrayList<>();
        for (Object[] obj : summedHours) {
            DoctorDtoSparse doctorDtoSparse = new DoctorDtoSparse(
                (long) obj[0],
                (String) obj[1],
                (String) obj[2],
                (String) obj[3],
                (boolean) obj[4]
            );
            doctorWorkingHoursDtos.add(new DoctorWorkingHoursDto(doctorDtoSparse, (((BigDecimal) obj[5]).divide(new BigDecimal(60), 2, RoundingMode.HALF_UP)).doubleValue()));
        }
        return doctorWorkingHoursDtos;
    }
}
