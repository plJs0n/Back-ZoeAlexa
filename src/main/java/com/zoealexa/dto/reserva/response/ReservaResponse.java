package com.zoealexa.dto.reserva.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zoealexa.entity.enums.EstadoReserva;
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
public class ReservaResponse {

    private Long idReserva;
    private String codigoReserva;

    // Información del viaje
    private ViajeInfoResponse viaje;

    // Información de usuario y agencia
    private String nombreUsuario;
    private String nombreAgencia;

    // Ubicaciones
    private String origen;
    private String destino;

    // Montos
    private BigDecimal total;
    private BigDecimal montoPagado;
    private BigDecimal saldoPendiente;
    private BigDecimal penalidadAplicada;
    private BigDecimal comisionAgencia;

    // Estado
    private EstadoReserva estado;

    // Pasajeros
    private List<PasajeroDetalleResponse> pasajeros;

    // Pagos
    private List<PagoResponse> pagos;

    // Fechas
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaReserva;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaActualizacion;

    // Información adicional
    private boolean puedeCancelar;
    private boolean puedeReprogramar;
    private boolean requierePagoSaldo;
}
