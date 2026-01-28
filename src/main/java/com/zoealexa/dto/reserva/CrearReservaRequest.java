package com.zoealexa.dto.reserva;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearReservaRequest {

    @NotNull(message = "El ID del viaje es obligatorio")
    @Positive(message = "El ID del viaje debe ser positivo")
    private Integer idViaje;

    @Positive(message = "El ID de agencia debe ser positivo")
    private Integer idAgencia; // Opcional (null si es venta directa)

    @NotNull(message = "Debe incluir al menos un pasajero")
    @Size(min = 1, message = "Debe incluir al menos un pasajero")
    @Valid
    private List<PasajeroReservaRequest> pasajeros;

    @Valid
    private PagoInicialRequest pagoInicial; // Opcional (puede pagarse después)

    @Valid
    private DatosFacturaRequest datosFactura; // Opcional (solo si requiere factura)
}