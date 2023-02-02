package hk.ust.connect.klmoaa.s8challenge.exceptions;

public class ExternalAPIException extends Exception {

    private static final String defaultMessage = "Exception occurred during connection with external API";

    public ExternalAPIException(Throwable cause) {
        super(defaultMessage, cause);
    }

    public ExternalAPIException(String errorMessage) {
        super(errorMessage);
    }

    public ExternalAPIException() {
        super(defaultMessage);
    }
}
