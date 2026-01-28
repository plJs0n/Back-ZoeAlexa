package com.zoealexa.dto.seguridad;

import com.zoealexa.entity.enums.EstadoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUsuarioRequestDTO {

    @Size(max = 100)
    private String nombresUsuario;

    @Email(message = "El email debe ser válido")
    private String email;

    @Pattern(regexp = "^\\d{9}$", message = "El teléfono debe tener 9 dígitos")
    private String telefono;

    private EstadoUsuario estado;
}
