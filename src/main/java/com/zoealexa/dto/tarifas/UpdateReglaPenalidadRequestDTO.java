package com.zoealexa.dto.tarifas;

import jakarta.validation.constraints.*;
import lombok.Data;
import com.zoealexa.entity.enums.TipoValor;

import java.math.BigDecimal;

/**
 * DTO para actualización parcial de regla de penalidad
 * Todos los campos son opcionales
 */
@Data
public class UpdateReglaPenalidadRequestDTO {

    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    private String descripcion;

    private TipoValor tipoValor;

    @DecimalMin(value = "0.00", message = "El valor no puede ser negativo")
    private BigDecimal valor;

    @Min(value = 0, message = "Los kilos permitidos no pueden ser negativos")
    private Integer kilosPermitidos;

    @DecimalMin(value = "0.00", message = "El precio por kilo no puede ser negativo")
    private BigDecimal precioPorKilo;

    private Boolean activa;
}