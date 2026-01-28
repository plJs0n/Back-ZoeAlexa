package com.zoealexa.dto.transporte;

import jakarta.validation.constraints.*;
import lombok.*;
import com.zoealexa.entity.enums.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PuertoRequestDTO {

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 100)
    private String ciudad;

    @NotBlank(message = "El nombre del puerto es obligatorio")
    @Size(max = 100)
    private String nombrePuerto;

    @NotNull(message = "El tipo de operación es obligatorio")
    private TipoOperacion tipoOperacion;

    @Size(max = 200)
    private String direccion;

    @NotNull(message = "El estado es obligatorio")
    private EstadoPuerto estado;
}