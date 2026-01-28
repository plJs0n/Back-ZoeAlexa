package com.zoealexa.dto.seguridad;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO para verificar código y resetear contraseña
 */
@Data
public class VerificarCodigoResetPasswordDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "El código es obligatorio")
    @Pattern(regexp = "\\d{6}", message = "El código debe tener 6 dígitos")
    private String codigo;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String nuevaPassword;
}