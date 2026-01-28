package com.zoealexa.dto.tarifas;

import jakarta.validation.constraints.*;
import lombok.Data;
import com.zoealexa.entity.enums.TipoValor;

import java.math.BigDecimal;

/**
 * DTO para crear una nueva regla de descuento
 */
@Data
public class ReglaDescuentoRequestDTO {

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    private String descripcion;

    @Min(value = 0, message = "La edad mínima no puede ser negativa")
    private Integer edadMinima;

    @Min(value = 0, message = "La edad máxima no puede ser negativa")
    private Integer edadMaxima;

    @NotNull(message = "El tipo de valor es obligatorio")
    private TipoValor tipoValor;

    @NotNull(message = "El valor es obligatorio")
    @DecimalMin(value = "0.00", message = "El valor no puede ser negativo")
    private BigDecimal valor;

    private Boolean activa;
}