package com.zoealexa.exception;

import org.springframework.http.HttpStatus;

public class PasajeroDuplicadoException extends ReservaException {
    public PasajeroDuplicadoException(String nombrePasajero) {
        super(
                String.format("El pasajero '%s' ya está registrado en esta reserva", nombrePasajero),
                HttpStatus.CONFLICT,
                "PASAJERO_DUPLICADO"
        );
    }
}