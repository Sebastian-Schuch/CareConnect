package at.ac.tuwien.sepr.groupphase.backend.exception;

import java.util.Collections;
import java.util.List;

/**
 * Common super class for exceptions that report a list of errors
 * back to the user, when the given data did not pass a certain kind of checks.
 */
public abstract class ErrorListException extends Exception {
    private final List<String> errors;
    private final String messageSummary;
    private final String errorListDescriptor;

    /**
     * Constructs a new {@code ErrorListException} with the specified parameters.
     *
     * @param errorListDescriptor a descriptor for the type of error list being reported
     * @param messageSummary      a summary message describing the error
     * @param errors              a list of error messages
     */
    public ErrorListException(String errorListDescriptor, String messageSummary, List<String> errors) {
        super(messageSummary);
        this.errorListDescriptor = errorListDescriptor;
        this.messageSummary = messageSummary;
        this.errors = errors;
    }

    /**
     * See {@link Throwable#getMessage()} for general information about this method.
     *
     * <p>Note: this implementation produces the message
     * from the {@link #summary} and the list of {@link #errors}
     */
    @Override
    public String getMessage() {
        return "%s. %s: %s."
            .formatted(messageSummary, errorListDescriptor, String.join(", ", errors));
    }

    public String summary() {
        return messageSummary;
    }

    public List<String> errors() {
        return Collections.unmodifiableList(errors);
    }
}
