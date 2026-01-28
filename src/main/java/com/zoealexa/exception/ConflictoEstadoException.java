package com.zoealexa.exception;

import org.springframework.http.HttpStatus;

public class ConflictoEstadoException extends ReservaException {
    public ConflictoEstadoException(String estadoActual, String operacion) {
        super(
                String.format("No se puede realizar '%s' en el estado actual: %s",
                        operacion, estadoActual),
                HttpStatus.CONFLICT,
                "CONFLICTO_ESTADO"
        );
    }
}