package com.example.LibrodeRegistro.exception;

public class RetiroExcedeAhorroException extends RuntimeException {
    public RetiroExcedeAhorroException(String mensaje) {
        super(mensaje);
    }
}
