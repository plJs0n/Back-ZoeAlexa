package com.zoealexa.dto.reserva.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zoealexa.entity.enums.EstadoPago;
import com.zoealexa.entity.enums.MetodoPago;
import com.zoealexa.entity.enums.TipoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponse {

    private Long idPago;
    private TipoPago tipoPago;
    private MetodoPago metodoPago;
    private BigDecimal monto;
    private String referenciaTransaccion;
    private EstadoPago estado;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaPago;

    // Comprobantes generados
    private List<ComprobanteResponse> comprobantes;
}
