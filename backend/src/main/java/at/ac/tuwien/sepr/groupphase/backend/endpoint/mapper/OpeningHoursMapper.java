package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpeningHoursDayDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpeningHoursDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpeningHoursDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.entity.OpeningHours;
import org.mapstruct.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.time.LocalTime;

@Mapper
public class OpeningHoursMapper {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Maps an entity to a DTO.
     *
     * @param openingHours the entity
     * @return the DTO
     */
    public OpeningHoursDto entityToDto(OpeningHours openingHours) {
        LOG.trace("entityToDto()");

        OpeningHoursDayDto monday = openingHours.getMonday() == null ? null : new OpeningHoursDayDto(extractOpeningHours(openingHours.getMonday()),
            extractClosingHours(openingHours.getMonday()));
        OpeningHoursDayDto tuesday = openingHours.getTuesday() == null ? null : new OpeningHoursDayDto(extractOpeningHours(openingHours.getTuesday()),
            extractClosingHours(openingHours.getTuesday()));
        OpeningHoursDayDto wednesday = openingHours.getWednesday() == null ? null : new OpeningHoursDayDto(extractOpeningHours(openingHours.getWednesday()),
            extractClosingHours(openingHours.getWednesday()));
        OpeningHoursDayDto thursday = openingHours.getThursday() == null ? null : new OpeningHoursDayDto(extractOpeningHours(openingHours.getThursday()),
            extractClosingHours(openingHours.getThursday()));
        OpeningHoursDayDto friday = openingHours.getFriday() == null ? null : new OpeningHoursDayDto(extractOpeningHours(openingHours.getFriday()),
            extractClosingHours(openingHours.getFriday()));
        OpeningHoursDayDto saturday = openingHours.getSaturday() == null ? null : new OpeningHoursDayDto(extractOpeningHours(openingHours.getSaturday()),
            extractClosingHours(openingHours.getSaturday()));
        OpeningHoursDayDto sunday = openingHours.getSunday() == null ? null : new OpeningHoursDayDto(extractOpeningHours(openingHours.getSunday()),
            extractClosingHours(openingHours.getSunday()));

        return new OpeningHoursDto(
            openingHours.getId(),
            monday,
            tuesday,
            wednesday,
            thursday,
            friday,
            saturday,
            sunday
        );
    }

    /**
     * Maps a DTO to an entity.
     *
     * @param dto the DTO
     * @return the entity
     */
    public OpeningHours dtoToEntity(OpeningHoursDto dto) {
        LOG.trace("dtoToEntity()");
        return new OpeningHours().setId(dto.id())
            .setMonday(dto.monday().isClosed() ? null : dto.monday().toString())
            .setTuesday(dto.tuesday().isClosed() ? null : dto.tuesday().toString())
            .setWednesday(dto.wednesday().isClosed() ? null : dto.wednesday().toString())
            .setThursday(dto.thursday().isClosed() ? null : dto.thursday().toString())
            .setFriday(dto.friday().isClosed() ? null : dto.friday().toString())
            .setSaturday(dto.saturday().isClosed() ? null : dto.saturday().toString())
            .setSunday(dto.sunday().isClosed() ? null : dto.sunday().toString());
    }

    public OpeningHours dtoToEntity(OpeningHoursDtoCreate dto) {
        LOG.trace("dtoToEntity()");
        return new OpeningHours().setId(null)
            .setMonday(dto.monday().isClosed() ? null : dto.monday().toString())
            .setTuesday(dto.tuesday().isClosed() ? null : dto.tuesday().toString())
            .setWednesday(dto.wednesday().isClosed() ? null : dto.wednesday().toString())
            .setThursday(dto.thursday().isClosed() ? null : dto.thursday().toString())
            .setFriday(dto.friday().isClosed() ? null : dto.friday().toString())
            .setSaturday(dto.saturday().isClosed() ? null : dto.saturday().toString())
            .setSunday(dto.sunday().isClosed() ? null : dto.sunday().toString());
    }

    /**
     * Extracts the opening hours from a string.
     *
     * @param openingHours the opening hours string
     * @return the opening hours
     */
    private LocalTime extractOpeningHours(String openingHours) {
        LOG.trace("extractOpeningHours()");
        if (openingHours == null || openingHours.isEmpty() || openingHours.isBlank() || openingHours.equals("closed")) {
            return null;
        }
        return LocalTime.parse(openingHours.substring(0, openingHours.indexOf("-")));
    }

    /**
     * Extracts the closing hours from a string.
     *
     * @param openingHours the opening hours string
     * @return the closing hours
     */
    private LocalTime extractClosingHours(String openingHours) {
        LOG.trace("extractClosingHours()");
        if (openingHours == null || openingHours.isEmpty() || openingHours.isBlank() || openingHours.equals("closed")) {
            return null;
        }
        return LocalTime.parse(openingHours.substring(openingHours.indexOf("-") + 1));
    }
}
