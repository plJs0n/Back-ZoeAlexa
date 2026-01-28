package com.zoealexa.dto.tarifas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta de TarifaRuta
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TarifaRutaResponseDTO {

    private Integer idTarifa;

    // Información de la ruta
    private Integer idRuta;
    private String nombreRuta;      // "Iquitos → Pucallpa"
    private String ciudadOrigen;
    private String ciudadDestino;

    // Información de la tarifa
    private BigDecimal precioBase;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;   // null = vigente

    // Información derivada
    private Boolean vigente;
    private String descripcionVigencia;
}
