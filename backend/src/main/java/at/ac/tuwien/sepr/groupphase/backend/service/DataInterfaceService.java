package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DataInterfaceDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorWorkingHoursDto;

import java.util.List;

public interface DataInterfaceService {

    /**
     * Get the working hours of the doctors.
     *
     * @return List of DoctorWorkingHoursDto
     */
    List<DoctorWorkingHoursDto> getDoctorWorkingHours(DataInterfaceDtoSearch search);
}
