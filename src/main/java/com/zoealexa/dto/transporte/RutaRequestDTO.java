package com.zoealexa.dto.transporte;

import jakarta.validation.constraints.*;
import lombok.*;
import com.zoealexa.entity.enums.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RutaRequestDTO {

    @NotNull(message = "El puerto origen es obligatorio")
    private Integer idPuertoOrigen;

    @NotNull(message = "El puerto destino es obligatorio")
    private Integer idPuertoDestino;

    @NotBlank(message = "Los días de operación son obligatorios")
    @Size(max = 50)
    private String diasOperacion; // "LUN,MIE,VIE"

    @NotNull(message = "El estado es obligatorio")
    private EstadoRuta estado; //ACTIVA, INACTIVA
}
