package com.zoealexa.exception;

import org.springframework.http.HttpStatus;

public class AdelantoInsuficienteException extends ReservaException {
    public AdelantoInsuficienteException(String mensaje) {
        super(mensaje, HttpStatus.BAD_REQUEST, "ADELANTO_INSUFICIENTE");
    }
}