package com.zoealexa.dto.transporte;

import lombok.Data;
import com.zoealexa.entity.enums.EstadoRuta;

/**
 * DTO para actualización parcial de Ruta
 * Todos los campos son opcionales
 *
 * Solo se actualizan los campos que se envían (no null)
 */
@Data
public class UpdateRutaRequestDTO {

    private Integer idPuertoOrigen;
    private Integer idPuertoDestino;
    private String diasOperacion;
    private EstadoRuta estado;
}