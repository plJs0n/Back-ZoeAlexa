package com.zoealexa.exception;

/**
 * Excepción lanzada cuando un token de recuperación es inválido o ha expirado
 */
public class TokenInvalidException extends RuntimeException {

  public TokenInvalidException(String message) {
    super(message);
  }

  public TokenInvalidException(String message, Throwable cause) {
    super(message, cause);
  }
}