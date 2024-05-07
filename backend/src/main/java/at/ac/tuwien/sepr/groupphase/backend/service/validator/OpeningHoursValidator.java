package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpeningHoursDtoCreate;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Component
public class OpeningHoursValidator {
    public void validateOpeningHours(OpeningHoursDtoCreate openingHoursDtoCreate) throws MethodArgumentNotValidException {
        BindingResult bindingResult = new BeanPropertyBindingResult(openingHoursDtoCreate, "openingHours");

        if (openingHoursDtoCreate.mondayStart() != null && openingHoursDtoCreate.mondayStart().isAfter(openingHoursDtoCreate.mondayEnd())) {
            bindingResult.rejectValue("mondayStart", null, "must be before mondayEnd");
        }
        if (openingHoursDtoCreate.tuesdayStart() != null && openingHoursDtoCreate.tuesdayStart().isAfter(openingHoursDtoCreate.tuesdayEnd())) {
            bindingResult.rejectValue("tuesdayStart", null, "must be before tuesdayEnd");
            ;
        }
        if (openingHoursDtoCreate.wednesdayStart() != null && openingHoursDtoCreate.wednesdayStart().isAfter(openingHoursDtoCreate.wednesdayEnd())) {
            bindingResult.rejectValue("wednesdayStart", null, "must be before wednesdayEnd");
        }
        if (openingHoursDtoCreate.thursdayStart() != null && openingHoursDtoCreate.thursdayStart().isAfter(openingHoursDtoCreate.thursdayEnd())) {
            bindingResult.rejectValue("thursdayStart", null, "must be before thursdayEnd");
        }
        if (openingHoursDtoCreate.fridayStart() != null && openingHoursDtoCreate.fridayStart().isAfter(openingHoursDtoCreate.fridayEnd())) {
            bindingResult.rejectValue("fridayStart", null, "must be before fridayEnd");
        }
        if (openingHoursDtoCreate.saturdayStart() != null && openingHoursDtoCreate.saturdayStart().isAfter(openingHoursDtoCreate.saturdayEnd())) {
            bindingResult.rejectValue("saturdayStart", null, "must be before saturdayEnd");
        }
        if (openingHoursDtoCreate.sundayStart() != null && openingHoursDtoCreate.sundayStart().isAfter(openingHoursDtoCreate.sundayEnd())) {
            bindingResult.rejectValue("sundayStart", null, "must be before sundayEnd");
        }
        if (bindingResult.hasErrors()) {
            throw new MethodArgumentNotValidException(null, bindingResult);
        }
    }
}
