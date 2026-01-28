package com.zoealexa.dto.tarifas;

import jakarta.validation.constraints.*;
import lombok.Data;
import com.zoealexa.entity.enums.TipoValor;

import java.math.BigDecimal;

/**
 * DTO para actualización parcial de regla de descuento
 * Todos los campos son opcionales
 */
@Data
public class UpdateReglaDescuentoRequestDTO {

    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    private String descripcion;

    @Min(value = 0, message = "La edad mínima no puede ser negativa")
    private Integer edadMinima;

    @Min(value = 0, message = "La edad máxima no puede ser negativa")
    private Integer edadMaxima;

    private TipoValor tipoValor;

    @DecimalMin(value = "0.00", message = "El valor no puede ser negativo")
    private BigDecimal valor;

    private Boolean activa;
}