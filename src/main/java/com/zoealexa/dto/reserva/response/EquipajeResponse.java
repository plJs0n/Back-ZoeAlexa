package com.zoealexa.dto.reserva.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipajeResponse {

    private Long idEquipaje;
    private BigDecimal pesoKg;
    private BigDecimal limiteIncluido;
    private BigDecimal pesoExcedenteKg;
    private BigDecimal volumenM3;
    private BigDecimal precioPorKilo;
    private BigDecimal costoExceso;
    private String descripcion;
    private boolean tieneExceso;
    private boolean tieneComprobanteGenerado;
}