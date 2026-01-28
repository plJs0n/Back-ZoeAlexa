package com.zoealexa.dto.transporte;

import com.zoealexa.entity.enums.EstadoEmbarcacion;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmbarcacionRequestDTO {

    @NotBlank(message = "El nombre de la embarcación es obligatorio")
    @Size(max = 100)
    private String nombreEmbarcacion;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    private Integer capacidad;

    @NotNull(message = "El estado es obligatorio")
    private EstadoEmbarcacion estado;
}
