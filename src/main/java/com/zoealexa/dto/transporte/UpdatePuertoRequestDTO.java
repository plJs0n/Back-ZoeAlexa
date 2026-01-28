package com.zoealexa.dto.transporte;

import lombok.Data;
import com.zoealexa.entity.enums.EstadoPuerto;
import com.zoealexa.entity.enums.TipoOperacion;

/**
 * DTO para actualización parcial de Puerto
 * Todos los campos son opcionales
 */
@Data
public class UpdatePuertoRequestDTO {

    private String nombrePuerto;
    private String ciudad;
    private String direccion;
    private TipoOperacion tipoOperacion;
    private EstadoPuerto estado;
}