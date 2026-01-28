package com.zoealexa.exception;

import org.springframework.http.HttpStatus;

public class ValidacionNegocioException extends ReservaException {
    public ValidacionNegocioException(String mensaje) {
        super(mensaje, HttpStatus.BAD_REQUEST, "VALIDACION_NEGOCIO");
    }
}