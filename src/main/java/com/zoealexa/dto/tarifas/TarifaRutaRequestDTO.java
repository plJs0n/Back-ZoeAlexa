package com.zoealexa.dto.tarifas;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO para crear una nueva TarifaRuta
 *
 * El sistema:
 * - asigna fechas automáticamente
 * - cierra la tarifa vigente anterior
 */
@Data
public class TarifaRutaRequestDTO {

    @NotNull(message = "El ID de la ruta es obligatorio")
    private Integer idRuta;

    @NotNull(message = "El precio base es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precioBase;
}
