package com.zoealexa.dto.seguridad;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para solicitar código de recuperación
 */
@Data
public class SolicitarCodigoRequestDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;
}