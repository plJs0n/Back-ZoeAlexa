package com.zoealexa.exception;

/**
 * Excepción personalizada para errores de validación y solicitudes incorrectas.
 * Devuelve HTTP 400 Bad Request.
 *
 * Usar cuando:
 * - Datos inválidos proporcionados por el usuario
 * - Validaciones de reglas de negocio fallan
 * - Parámetros faltantes o incorrectos
 * - Formato de datos incorrecto
 */
public class BadRequestException extends RuntimeException {

    /**
     * Constructor con mensaje
     *
     * @param message Mensaje de error descriptivo
     */
    public BadRequestException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa
     *
     * @param message Mensaje de error descriptivo
     * @param cause Causa raíz del error
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}