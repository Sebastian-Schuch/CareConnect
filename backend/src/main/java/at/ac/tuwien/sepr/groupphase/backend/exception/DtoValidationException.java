package at.ac.tuwien.sepr.groupphase.backend.exception;

public class DtoValidationException extends RuntimeException {
    public DtoValidationException(String message) {
        super(message);
    }
}
