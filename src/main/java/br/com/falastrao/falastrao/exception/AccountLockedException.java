package br.com.falastrao.falastrao.exception;

public class AccountLockedException extends AccountException {
    public AccountLockedException(String message) { super(message); }
}