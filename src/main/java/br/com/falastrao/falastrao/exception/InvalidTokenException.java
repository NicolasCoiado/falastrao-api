package br.com.falastrao.falastrao.exception;

public class InvalidTokenException extends AuthException {
    public InvalidTokenException(String message) { super(message); }
}