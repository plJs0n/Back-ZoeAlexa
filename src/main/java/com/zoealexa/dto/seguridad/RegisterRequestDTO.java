package com.zoealexa.dto.seguridad;

import jakarta.validation.constraints.*;
import lombok.Data;
import com.zoealexa.entity.enums.Rol;

/**
 * DTO para registro de usuarios - ACTUALIZADO CON AGENCIA
 */
@Data
public class RegisterRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombresUsuario;

    @NotNull(message = "El rol es obligatorio")
    private Rol rol;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;

    /**
     * ID de la agencia (solo para rol AGENCIA)
     * Obligatorio si rol = AGENCIA
     * Debe ser NULL para otros roles
     */
    private Integer idAgencia;
}