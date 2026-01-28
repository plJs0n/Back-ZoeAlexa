package com.zoealexa.dto.tarifas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.zoealexa.entity.enums.TipoPenalidad;
import com.zoealexa.entity.enums.TipoValor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para ReglaPenalidad
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReglaPenalidadResponseDTO {

    private Integer idPenalidad;
    private TipoPenalidad tipoPenalidad;
    private String descripcion;

    // Para CANCELACION, REPROGRAMACION, GENERICA
    private TipoValor tipoValor;
    private BigDecimal valor;

    // Solo para EQUIPAJE
    private Integer kilosPermitidos;
    private BigDecimal precioPorKilo;

    // Estado
    private Boolean activa;
    private LocalDateTime fechaCreacion;
}