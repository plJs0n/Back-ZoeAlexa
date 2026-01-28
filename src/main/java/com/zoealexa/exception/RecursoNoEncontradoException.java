package com.zoealexa.exception;

import org.springframework.http.HttpStatus;

public class RecursoNoEncontradoException extends ReservaException {
    public RecursoNoEncontradoException(String recurso, String identificador) {
        super(
                String.format("%s con identificador '%s' no encontrado", recurso, identificador),
                HttpStatus.NOT_FOUND,
                "RECURSO_NO_ENCONTRADO"
        );
    }

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje, HttpStatus.NOT_FOUND, "RECURSO_NO_ENCONTRADO");
    }
}
