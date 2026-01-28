package com.zoealexa.dto.transporte;

import lombok.*;
import com.zoealexa.entity.enums.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PuertoResponseDTO {
    private Integer idPuerto;
    private String ciudad;
    private String nombrePuerto;
    private String nombreCompleto; // "Puerto Iquitos - Iquitos"
    private TipoOperacion tipoOperacion;
    private String direccion;
    private EstadoPuerto estado;
}