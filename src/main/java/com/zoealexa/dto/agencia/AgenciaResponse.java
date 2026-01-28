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
public class AgenciaResponse {

    private Integer idAgencia;
    private String nombreAgencia;
    private String ruc;
    private String direccion;
    private String telefono;

    private TipoComision tipoComision;
    private BigDecimal valorComision;
    private String descripcionComision; // "10% de comisión" o "S/ 15.00 por pasaje"

    private EstadoAgencia estado;
    private Integer cantidadUsuarios; // Usuarios asociados a la agencia

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCreacion;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaActualizacion;

//    private String creadoPor;
//    private String actualizadoPor;
}
