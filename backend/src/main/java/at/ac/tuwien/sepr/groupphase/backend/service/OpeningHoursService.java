package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpeningHoursDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpeningHoursDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.entity.OpeningHours;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;

public interface OpeningHoursService {
    /**
     * Creates a new opening hours.
     *
     * @param openingHoursDto the opening hours to create
     * @return the created opening hours
     */
    OpeningHoursDto createOpeningHours(OpeningHoursDtoCreate openingHoursDto) throws NotFoundException;

    /**
     * Gets specific opening hours by id.
     *
     * @return a specific opening hours dto
     */
    OpeningHoursDto getOpeningHoursById(Long id);

    /**
     * Returns an opening hours entity from a dto after validating it.
     *
     * @return opening hours entity
     */
    OpeningHours getOpeningHoursEntityFromDtoCreate(OpeningHoursDtoCreate openingHoursDto) throws MethodArgumentNotValidException;

}
