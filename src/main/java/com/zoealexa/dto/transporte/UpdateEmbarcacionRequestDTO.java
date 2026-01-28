package com.zoealexa.dto.transporte;

import lombok.Data;
import com.zoealexa.entity.enums.EstadoEmbarcacion;

/**
 * DTO para actualización parcial de Embarcación
 * Todos los campos son opcionales
 */
@Data
public class UpdateEmbarcacionRequestDTO {

    private String nombreEmbarcacion;
    private Integer capacidad;
    private EstadoEmbarcacion estado;
}