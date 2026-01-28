package com.zoealexa.dto.reserva;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelarReservaRequest {

    @NotBlank(message = "El código de reserva es obligatorio")
    private String codigoReserva;

    @Size(max = 500, message = "El motivo no puede exceder 500 caracteres")
    private String motivo;
}
