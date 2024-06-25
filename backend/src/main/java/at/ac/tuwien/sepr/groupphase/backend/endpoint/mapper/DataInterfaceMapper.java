package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorWorkingHoursDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationAmountDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInterfaceMapper {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public List<DoctorWorkingHoursDto> doctorHoursObjectToDoctorWorkingHoursDto(List<Object[]> summedHours) {
        LOG.trace("doctorHoursObjectToDoctorWorkingHoursDto({})", summedHours);
        List<DoctorWorkingHoursDto> doctorWorkingHoursDtos = new ArrayList<>();
        for (Object[] obj : summedHours) {
            DoctorDtoSparse doctorDtoSparse = new DoctorDtoSparse(
                (long) obj[0],
                (String) obj[1],
                (String) obj[2],
                (String) obj[3],
                (boolean) obj[4]
            );
            doctorWorkingHoursDtos.add(
                new DoctorWorkingHoursDto(doctorDtoSparse, (((BigDecimal) obj[5]).divide(new BigDecimal(60), 2, RoundingMode.HALF_UP)).doubleValue()));
        }
        return doctorWorkingHoursDtos;
    }

    public List<MedicationAmountDto> medicationAmountObjectToMedicationAmountDto(List<Object[]> summedAmounts) {
        LOG.trace("medicationAmountObjectToMedicationAmountDto({})", summedAmounts);
        List<MedicationAmountDto> medicationAmountDtos = new ArrayList<>();
        for (Object[] obj : summedAmounts) {
            medicationAmountDtos.add(new MedicationAmountDto((long) obj[0], (String) obj[1], (long) obj[2], (String) obj[3]));
        }
        return medicationAmountDtos;
    }
}
