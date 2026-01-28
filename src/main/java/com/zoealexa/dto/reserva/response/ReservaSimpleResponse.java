package com.zoealexa.dto.reserva.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zoealexa.entity.enums.EstadoReserva;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaSimpleResponse {

    private Long idReserva;
    private String codigoReserva;
    private String origen;
    private String destino;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaViaje;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaEmbarque;

    private Integer cantidadPasajeros;
    private BigDecimal total;
    private BigDecimal saldoPendiente;
    private EstadoReserva estado;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaReserva;
}
