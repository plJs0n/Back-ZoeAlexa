package com.zoealexa.dto.agencia;

import com.zoealexa.entity.enums.EstadoAgencia;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CambiarEstadoAgenciaRequest {

    @NotNull(message = "El estado es obligatorio")
    private EstadoAgencia estado;

    @Size(max = 200, message = "El motivo no puede exceder 200 caracteres")
    private String motivo; // Opcional, para auditoría
}
