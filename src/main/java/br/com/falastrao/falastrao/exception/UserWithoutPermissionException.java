package br.com.falastrao.falastrao.exception;

public class UserWithoutPermissionException extends AuthException {
    public UserWithoutPermissionException(String message) { super(message); }
}