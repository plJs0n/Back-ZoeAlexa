package com.zoealexa.dto.reserva.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zoealexa.entity.enums.EstadoViaje;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViajeInfoResponse {

    private Integer idViaje;
    private String nombreRuta;
    private String nombreEmbarcacion;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaViaje;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaEmbarque;

    private Integer cuposDisponibles;
    private Integer cuposOcupados;
    private EstadoViaje estado;
}
