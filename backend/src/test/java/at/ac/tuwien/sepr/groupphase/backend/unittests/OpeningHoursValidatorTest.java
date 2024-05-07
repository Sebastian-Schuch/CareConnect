package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpeningHoursDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.OpeningHoursValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class OpeningHoursValidatorTest {


    private final OpeningHoursDtoCreate openingHoursDtoCreateInvalid = new OpeningHoursDtoCreate(
        LocalTime.of(18, 0),
        LocalTime.of(14, 0),
        LocalTime.of(8, 0),
        LocalTime.of(14, 0),
        LocalTime.of(8, 0),
        LocalTime.of(14, 0),
        LocalTime.of(8, 0),
        LocalTime.of(14, 0),
        LocalTime.of(8, 0),
        LocalTime.of(14, 0),
        LocalTime.of(8, 0),
        LocalTime.of(14, 0),
        LocalTime.of(8, 0),
        LocalTime.of(14, 0)
    );

    private final OpeningHoursDtoCreate openingHoursDtoCreateValid = new OpeningHoursDtoCreate(
        LocalTime.of(8, 0),
        LocalTime.of(14, 0),
        LocalTime.of(8, 0),
        LocalTime.of(14, 0),
        LocalTime.of(8, 0),
        LocalTime.of(14, 0),
        LocalTime.of(8, 0),
        LocalTime.of(14, 0),
        LocalTime.of(8, 0),
        LocalTime.of(14, 0),
        LocalTime.of(8, 0),
        LocalTime.of(14, 0),
        LocalTime.of(8, 0),
        LocalTime.of(14, 0)
    );

    @Test
    public void givenAInvalidOpeningHours_whenValidate_thenThrowMethodArgumentNotValidException() {
        OpeningHoursValidator openingHoursValidator = new OpeningHoursValidator();
        assertThrows(MethodArgumentNotValidException.class, () -> openingHoursValidator.validateOpeningHours(openingHoursDtoCreateInvalid));
    }

    @Test
    public void givenAValidOpeningHours_whenValidate_thenDoNotThrowMethodArgumentNotValidException() {
        OpeningHoursValidator openingHoursValidator = new OpeningHoursValidator();
        assertDoesNotThrow(() -> openingHoursValidator.validateOpeningHours(openingHoursDtoCreateValid));
    }
}
