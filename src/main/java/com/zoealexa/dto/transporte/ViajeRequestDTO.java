package com.zoealexa.dto.transporte;

import jakarta.validation.constraints.*;
import lombok.*;
import com.zoealexa.entity.enums.*;
import java.time.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViajeRequestDTO {

    @NotNull(message = "La ruta es obligatoria")
    private Integer idRuta;

    @NotNull(message = "La embarcación es obligatoria")
    private Integer idEmbarcacion;

    @NotNull(message = "La fecha del viaje es obligatoria")
    private LocalDate fechaViaje;

    @NotNull(message = "La hora de embarque es obligatoria")
    private LocalTime horaEmbarque;

    @Positive(message = "Los cupos disponibles deben ser positivos")
    private Integer cuposDisponibles;

    @NotNull(message = "El estado es obligatorio")
    private EstadoViaje estado; //PROGRAMADO, EN_CURSO, COMPLETADO, CANCELADO
}