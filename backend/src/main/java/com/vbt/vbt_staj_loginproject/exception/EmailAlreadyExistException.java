package com.vbt.vbt_staj_loginproject.exception;

public class EmailAlreadyExistException extends RuntimeException {

    public EmailAlreadyExistException(String email) {
        super("Bu email adresi zaten kayıtlı: " + email);
    }
}
