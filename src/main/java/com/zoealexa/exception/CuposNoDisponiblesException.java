package com.zoealexa.exception;

import org.springframework.http.HttpStatus;

public class CuposNoDisponiblesException extends ReservaException {
    public CuposNoDisponiblesException(int cuposRequeridos, int cuposDisponibles) {
        super(
                String.format("No hay cupos suficientes. Requiere: %d, Disponibles: %d",
                        cuposRequeridos, cuposDisponibles),
                HttpStatus.CONFLICT,
                "CUPOS_NO_DISPONIBLES"
        );
    }
}
