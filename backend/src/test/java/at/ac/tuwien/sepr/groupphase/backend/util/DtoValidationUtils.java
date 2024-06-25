package at.ac.tuwien.sepr.groupphase.backend.util;

import at.ac.tuwien.sepr.groupphase.backend.exception.DtoValidationException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class DtoValidationUtils {

    private final Validator validator;

    public DtoValidationUtils(Validator validator) {
        this.validator = validator;
    }

    public void validate(Object target, String objectName) {
        Errors errors = new BeanPropertyBindingResult(target, objectName);
        validator.validate(target, errors);
        if (!errors.getAllErrors().isEmpty()) {
            throw new DtoValidationException("Validation failed for object: " + objectName + ", errors: " + errors.getAllErrors());
        }
    }
}

