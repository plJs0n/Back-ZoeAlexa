package com.zoealexa.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ReservaException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String codigo;

    public ReservaException(String mensaje, HttpStatus httpStatus, String codigo) {
        super(mensaje);
        this.httpStatus = httpStatus;
        this.codigo = codigo;
    }

    public ReservaException(String mensaje, HttpStatus httpStatus) {
        this(mensaje, httpStatus, httpStatus.name());
    }
}
