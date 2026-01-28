package com.zoealexa.dto.transporte;

import lombok.*;
import com.zoealexa.entity.enums.*;
import java.time.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmbarcacionResponseDTO {
    private Integer idEmbarcacion;
    private String nombreEmbarcacion;
    private Integer capacidad;
    private EstadoEmbarcacion estado;
    private LocalDateTime fechaCreacion;
}
