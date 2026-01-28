package com.zoealexa.dto.transporte;

import lombok.*;
import com.zoealexa.entity.enums.*;
import java.time.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViajeResponseDTO {
    private Integer idViaje;
    private RutaResponseDTO ruta;
    private EmbarcacionResponseDTO embarcacion;
    private LocalDate fechaViaje;
    private LocalTime horaEmbarque;
    private Integer cuposDisponibles;
    private Integer cuposOcupados;
    private EstadoViaje estado;
    private LocalDateTime fechaCreacion;
}
