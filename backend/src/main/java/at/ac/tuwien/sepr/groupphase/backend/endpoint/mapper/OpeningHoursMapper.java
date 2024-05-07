package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpeningHoursDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpeningHoursDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.entity.OpeningHours;
import org.mapstruct.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Mapper
public class OpeningHoursMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Maps an entity to a DTO.
     *
     * @param openingHours the entity
     * @return the DTO
     */
    public OpeningHoursDto entityToDto(OpeningHours openingHours) {
        LOGGER.trace("entityToDto()");

        return new OpeningHoursDto(
            openingHours.getId(),
            extractOpeningHours(openingHours.getMonday()),
            extractClosingHours(openingHours.getMonday()),
            extractOpeningHours(openingHours.getTuesday()),
            extractClosingHours(openingHours.getTuesday()),
            extractOpeningHours(openingHours.getWednesday()),
            extractClosingHours(openingHours.getWednesday()),
            extractOpeningHours(openingHours.getThursday()),
            extractClosingHours(openingHours.getThursday()),
            extractOpeningHours(openingHours.getFriday()),
            extractClosingHours(openingHours.getFriday()),
            extractOpeningHours(openingHours.getSaturday()),
            extractClosingHours(openingHours.getSaturday()),
            extractOpeningHours(openingHours.getSunday()),
            extractClosingHours(openingHours.getSunday())
        );
    }

    /**
     * Maps a DTO to an entity.
     *
     * @param dto the DTO
     * @return the entity
     */
    public OpeningHours dtoToEntity(OpeningHoursDto dto) {
        LOGGER.trace("dtoToEntity()");
        return new OpeningHours().setId(dto.id())
            .setMonday(formatDayOpeningHours(dto.mondayStart(), dto.mondayEnd()))
            .setTuesday(formatDayOpeningHours(dto.tuesdayStart(), dto.tuesdayEnd()))
            .setWednesday(formatDayOpeningHours(dto.wednesdayStart(), dto.wednesdayEnd()))
            .setThursday(formatDayOpeningHours(dto.thursdayStart(), dto.thursdayEnd()))
            .setFriday(formatDayOpeningHours(dto.fridayStart(), dto.fridayEnd()))
            .setSaturday(formatDayOpeningHours(dto.saturdayStart(), dto.saturdayEnd()))
            .setSunday(formatDayOpeningHours(dto.sundayStart(), dto.sundayEnd()));
    }

    public OpeningHours dtoToEntity(OpeningHoursDtoCreate dto) {
        LOGGER.trace("dtoToEntity()");
        return new OpeningHours().setId(null)
            .setMonday(formatDayOpeningHours(dto.mondayStart(), dto.mondayEnd()))
            .setTuesday(formatDayOpeningHours(dto.tuesdayStart(), dto.tuesdayEnd()))
            .setWednesday(formatDayOpeningHours(dto.wednesdayStart(), dto.wednesdayEnd()))
            .setThursday(formatDayOpeningHours(dto.thursdayStart(), dto.thursdayEnd()))
            .setFriday(formatDayOpeningHours(dto.fridayStart(), dto.fridayEnd()))
            .setSaturday(formatDayOpeningHours(dto.saturdayStart(), dto.saturdayEnd()))
            .setSunday(formatDayOpeningHours(dto.sundayStart(), dto.sundayEnd()));
    }

    /**
     * Formats the opening hours of a day to a string.
     *
     * @param openingHours the opening hours of the day
     * @param closingHours the closing hours of the day
     * @return the formatted opening hours
     */
    private String formatDayOpeningHours(LocalTime openingHours, LocalTime closingHours) {
        LOGGER.trace("formatDayOpeningHours()");
        if (openingHours == null || closingHours == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return openingHours.format(formatter) + "-" + closingHours.format(formatter);
    }

    /**
     * Extracts the opening hours from a string.
     *
     * @param openingHours the opening hours string
     * @return the opening hours
     */
    private LocalTime extractOpeningHours(String openingHours) {
        LOGGER.trace("extractOpeningHours()");
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
        LOGGER.trace("extractClosingHours()");
        if (openingHours == null || openingHours.isEmpty() || openingHours.isBlank() || openingHours.equals("closed")) {
            return null;
        }
        return LocalTime.parse(openingHours.substring(openingHours.indexOf("-") + 1));
    }
}
