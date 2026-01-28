package com.zoealexa.dto.reserva.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculoPrecioResponse {

    private BigDecimal precioBase;
    private BigDecimal totalSinDescuento;
    private BigDecimal totalDescuentos;
    private BigDecimal totalAPagar;
    private BigDecimal adelantoMinimo; // 50%

    private List<DetallePrecioPasajero> detallesPasajeros;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetallePrecioPasajero {
        private String nombrePasajero;
        private Integer edad;
        private String tipoTarifa;
        private BigDecimal precioBase;
        private BigDecimal descuento;
        private BigDecimal precioFinal;
    }
}
