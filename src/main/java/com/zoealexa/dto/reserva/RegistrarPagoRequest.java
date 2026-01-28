package com.zoealexa.dto.reserva;

import com.zoealexa.entity.enums.MetodoPago;
import com.zoealexa.entity.enums.TipoPago;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarPagoRequest {

    @NotBlank(message = "El código de reserva es obligatorio")
    private String codigoReserva;

    @NotNull(message = "El tipo de pago es obligatorio")
    private TipoPago tipoPago;

    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPago metodoPago;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    @Size(max = 100, message = "La referencia no puede exceder 100 caracteres")
    private String referenciaTransaccion;

    @Valid
    private DatosFacturaRequest datosFactura; // Opcional
}
