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
public class AgenciaEstadisticasResponse {

    private Integer idAgencia;
    private String nombreAgencia;
    private EstadoAgencia estado;

    // Estadísticas
    private Integer totalReservas;
    private Integer reservasDelMes;
    private BigDecimal comisionesTotales;
    private BigDecimal comisionesDelMes;
    private BigDecimal ventasTotales;
    private BigDecimal ventasDelMes;

    // Usuarios
    private Integer cantidadUsuarios;
    private Integer usuariosActivos;
}
