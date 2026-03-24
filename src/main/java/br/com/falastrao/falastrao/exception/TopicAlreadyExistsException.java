package br.com.falastrao.falastrao.exception;

public class TopicAlreadyExistsException extends RuntimeException {
    public TopicAlreadyExistsException(String message) {
        super(message);
    }
}
