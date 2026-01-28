package com.zoealexa.dto.tarifas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.zoealexa.entity.enums.TipoValor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para ReglaDescuento
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReglaDescuentoResponseDTO {

    private Integer idDescuento;
    private String descripcion;
    private Integer edadMinima;
    private Integer edadMaxima;
    private TipoValor tipoValor;
    private BigDecimal valor;
    private Boolean activa;
    private LocalDateTime fechaCreacion;
}