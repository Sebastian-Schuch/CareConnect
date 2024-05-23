package at.ac.tuwien.sepr.groupphase.backend.exception;

/**
 * Exception to throw when server failed to creat pdf with account data.
 */
public class PdfCouldNotBeCreatedException extends RuntimeException {
    public PdfCouldNotBeCreatedException(String message) {
        super(message);
    }
}
