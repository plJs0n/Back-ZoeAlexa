package com.zoealexa.exception;

/**
 * Excepción cuando falla una operación de negocio
 * HTTP Status: 500 INTERNAL SERVER ERROR o 422 UNPROCESSABLE ENTITY
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
