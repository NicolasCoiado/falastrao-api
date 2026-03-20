package br.com.falastrao.falastrao.exception;

public class ExpiredTokenException extends AuthException {
    public ExpiredTokenException(String message) { super(message); }
}