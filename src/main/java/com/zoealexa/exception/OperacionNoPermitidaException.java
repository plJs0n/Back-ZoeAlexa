package com.zoealexa.exception;

import org.springframework.http.HttpStatus;

public class OperacionNoPermitidaException extends ReservaException {
    public OperacionNoPermitidaException(String mensaje) {
        super(mensaje, HttpStatus.FORBIDDEN, "OPERACION_NO_PERMITIDA");
    }
}
