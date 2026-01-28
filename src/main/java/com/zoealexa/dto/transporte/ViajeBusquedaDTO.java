package com.zoealexa.dto.transporte;

import lombok.*;
import java.time.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViajeBusquedaDTO {
    private Integer idViaje;
    private String nombreRuta;
    private String nombreEmbarcacion;
    private LocalDate fechaViaje;
    private LocalTime horaEmbarque;
    private Integer cuposDisponibles;
    private String ciudadOrigen;
    private String ciudadDestino;
}
