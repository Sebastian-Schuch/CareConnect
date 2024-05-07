package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpeningHoursDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpeningHoursDtoCreate;

public interface OpeningHoursService {
    /**
     * Creates a new opening hours
     *
     * @param openingHoursDto the opening hours to create
     * @return the created opening hours
     */
    OpeningHoursDto createOpeningHours(OpeningHoursDtoCreate openingHoursDto);

    /**
     * Gets specific opening hours by id
     *
     * @return a specific opening hours dto
     */
    OpeningHoursDto getOpeningHoursById(Long id);
}
