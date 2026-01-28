package com.zoealexa.dto.transporte;

import lombok.*;
import com.zoealexa.entity.enums.*;
import java.time.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RutaResponseDTO {
    private Integer idRuta;
    private String nombreRuta; // "Iquitos → Pucallpa"
    private PuertoResponseDTO puertoOrigen;
    private PuertoResponseDTO puertoDestino;
    private String diasOperacion;
    private EstadoRuta estado;
    private LocalDateTime fechaCreacion;
}