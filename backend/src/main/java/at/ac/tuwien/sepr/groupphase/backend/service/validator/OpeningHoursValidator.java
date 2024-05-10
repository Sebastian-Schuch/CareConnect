package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpeningHoursDtoCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.invoke.MethodHandles;

@Component
public class OpeningHoursValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Validates the opening hours.
     *
     * @param openingHoursDtoCreate the opening hours DTO
     * @throws MethodArgumentNotValidException if the opening hours are not valid
     */
    public void validateOpeningHours(OpeningHoursDtoCreate openingHoursDtoCreate) throws MethodArgumentNotValidException {
        LOGGER.trace("validateOpeningHours()");
        BindingResult bindingResult = new BeanPropertyBindingResult(openingHoursDtoCreate, "openingHours");

        if (openingHoursDtoCreate.monday() != null && !openingHoursDtoCreate.monday().isValid()) {
            bindingResult.rejectValue("monday", null, "opening time must be before closing time");
        }
        if (openingHoursDtoCreate.tuesday() != null && !openingHoursDtoCreate.tuesday().isValid()) {
            bindingResult.rejectValue("tuesday", null, "opening time must be before closing time");
        }
        if (openingHoursDtoCreate.wednesday() != null && !openingHoursDtoCreate.wednesday().isValid()) {
            bindingResult.rejectValue("wednesday", null, "opening time must be before closing time");
        }
        if (openingHoursDtoCreate.thursday() != null && !openingHoursDtoCreate.thursday().isValid()) {
            bindingResult.rejectValue("thursday", null, "opening time must be before closing time");
        }
        if (openingHoursDtoCreate.friday() != null && !openingHoursDtoCreate.friday().isValid()) {
            bindingResult.rejectValue("friday", null, "opening time must be before closing time");
        }
        if (openingHoursDtoCreate.saturday() != null && !openingHoursDtoCreate.saturday().isValid()) {
            bindingResult.rejectValue("saturday", null, "opening time must be before closing time");
        }
        if (openingHoursDtoCreate.sunday() != null && !openingHoursDtoCreate.sunday().isValid()) {
            bindingResult.rejectValue("sunday", null, "opening time must be before closing time");
        }
        if (bindingResult.hasErrors()) {
            LOGGER.warn("OpeningHoursDtoCreate is not valid: " + bindingResult.getAllErrors());
            throw new MethodArgumentNotValidException(null, bindingResult);
        }
    }
}
