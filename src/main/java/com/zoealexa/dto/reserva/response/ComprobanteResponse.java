package com.zoealexa.dto.reserva.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zoealexa.entity.enums.EstadoSunat;
import com.zoealexa.entity.enums.TipoComprobante;
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
public class ComprobanteResponse {

    private Long idComprobante;
    private TipoComprobante tipo;
    private String serie;
    private String numero;
    private String numeroCompleto; // Ej: "B001-00000123"
    private String concepto;

    // Datos cliente (si es factura)
    private String rucCliente;
    private String razonSocial;

    // Montos
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;

    // Estado SUNAT
    private EstadoSunat estadoSunat;
    private String mensajeRespuesta;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaEmision;

    // Información adicional
    private boolean esComprobanteEquipaje;
}
