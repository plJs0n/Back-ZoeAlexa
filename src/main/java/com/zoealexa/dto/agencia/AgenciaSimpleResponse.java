package com.zoealexa.dto.agencia;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zoealexa.entity.enums.EstadoAgencia;
import com.zoealexa.entity.enums.TipoComision;
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
public class AgenciaSimpleResponse {

    private Integer idAgencia;
    private String nombreAgencia;
    private String ruc;
    private String telefono;
    private TipoComision tipoComision;
    private BigDecimal valorComision;
    private EstadoAgencia estado;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCreacion;
}
