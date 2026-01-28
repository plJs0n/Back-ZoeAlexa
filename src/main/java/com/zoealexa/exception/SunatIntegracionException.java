package com.zoealexa.exception;

import org.springframework.http.HttpStatus;

public class SunatIntegracionException extends ReservaException {
    public SunatIntegracionException(String mensaje) {
        super(mensaje, HttpStatus.SERVICE_UNAVAILABLE, "ERROR_SUNAT");
    }

    public SunatIntegracionException(String mensaje, Throwable causa) {
        super(mensaje, HttpStatus.SERVICE_UNAVAILABLE, "ERROR_SUNAT");
        initCause(causa);
    }
}
