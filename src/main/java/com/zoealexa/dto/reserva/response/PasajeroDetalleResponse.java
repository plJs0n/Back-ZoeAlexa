package com.zoealexa.dto.reserva.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasajeroDetalleResponse {

    private Long idPasajero;
    private String nombreCompleto;
    private String tipoDocumento;
    private String numeroDocumento;
    private Integer edad;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    // Detalles de tarifa
    private String tipoTarifa;
    private BigDecimal precioBase;
    private BigDecimal porcentajeDescuento;
    private BigDecimal montoDescuento;
    private BigDecimal precioFinal;

    // Equipaje (si tiene)
    private EquipajeResponse equipaje;
}
