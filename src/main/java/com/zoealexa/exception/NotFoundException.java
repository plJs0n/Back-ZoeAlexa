package com.zoealexa.exception;

/**
 * Excepción cuando un recurso no es encontrado
 * HTTP Status: 404 NOT FOUND
 *
 * Ejemplos:
 * - Usuario no encontrado
 * - Reserva no encontrada
 * - Viaje no encontrado
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}