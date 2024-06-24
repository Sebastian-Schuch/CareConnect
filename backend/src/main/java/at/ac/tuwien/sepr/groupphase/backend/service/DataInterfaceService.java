package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DataInterfaceDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorWorkingHoursDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationAmountDto;

import java.util.List;

public interface DataInterfaceService {

    /**
     * Get the working hours of the doctors.
     *
     * @param search DataInterfaceDtoSearch object with start and end data
     * @return List of DoctorWorkingHoursDto
     */
    List<DoctorWorkingHoursDto> getDoctorWorkingHours(DataInterfaceDtoSearch search);

    /**
     * Get the amount of medication.
     *
     * @param search DataInterfaceDtoSearch object with start and end data
     * @return List of MedicationAmountDto
     */
    List<MedicationAmountDto> getMedicationAmounts(DataInterfaceDtoSearch search);
}
