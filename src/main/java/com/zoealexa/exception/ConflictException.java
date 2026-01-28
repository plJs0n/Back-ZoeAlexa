package com.zoealexa.exception;

/**
 * Excepción cuando hay un conflicto con datos existentes
 * HTTP Status: 409 CONFLICT
 *
 * Ejemplos:
 * - Email ya registrado
 * - Documento ya existe
 * - Código duplicado
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
