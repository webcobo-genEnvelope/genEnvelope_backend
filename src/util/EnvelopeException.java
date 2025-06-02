package util;

public class EnvelopeException extends RuntimeException {
    public EnvelopeException(String message, Throwable cause) {
        super(message, cause);
    }

    public EnvelopeException(String message) {
        super(message);
    }
}
