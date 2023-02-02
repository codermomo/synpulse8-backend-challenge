package hk.ust.connect.klmoaa.s8challenge.exceptions;

public class KafkaException extends Exception {

    private static final String defaultMessage = "Exception occurred during connection with Apache Kafka";

    public KafkaException(Throwable cause) {
        super(defaultMessage, cause);
    }

    public KafkaException(String cause) {
        super(cause);
    }

    public KafkaException() {
        super(defaultMessage);
    }
}