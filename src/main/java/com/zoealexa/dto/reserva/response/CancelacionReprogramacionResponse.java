package com.zoealexa.dto.reserva.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zoealexa.entity.enums.TipoOperacionCancelacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelacionReprogramacionResponse {

    private Long idOperacion;
    private String codigoReserva;
    private TipoOperacionCancelacion tipoOperacion;

    // Viajes involucrados
    private String viajeOriginal;
    private String viajeNuevo; // Solo para reprogramación

    // Montos
    private BigDecimal montoOriginal;
    private BigDecimal porcentajePenalidad;
    private BigDecimal montoPenalidad;
    private BigDecimal montoDevolver;

    private String motivo;
    private String nombreUsuario;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaOperacion;
}
