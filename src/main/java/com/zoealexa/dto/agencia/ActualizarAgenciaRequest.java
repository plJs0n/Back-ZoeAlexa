package com.zoealexa.dto.agencia;

import com.zoealexa.entity.enums.TipoComision;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarAgenciaRequest {

    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombreAgencia;

    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String direccion;

    @Pattern(regexp = "^[0-9+\\-() ]*$", message = "Formato de teléfono inválido")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;

    private TipoComision tipoComision;

    @DecimalMin(value = "0.01", message = "El valor de comisión debe ser mayor a 0")
    @DecimalMax(value = "100.00", message = "El valor de comisión no puede exceder 100")
    @Digits(integer = 8, fraction = 2, message = "El valor debe tener máximo 8 enteros y 2 decimales")
    private BigDecimal valorComision;

    // Nota: El RUC NO se puede cambiar una vez creada la agencia
    // Nota: El estado se cambia con un endpoint específico
}
