package com.zoealexa.mapper.agencia;

import com.zoealexa.dto.agencia.AgenciaResponse;
import com.zoealexa.dto.agencia.AgenciaSimpleResponse;
import com.zoealexa.entity.enums.TipoComision;
import com.zoealexa.entity.seguridad.Agencia;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entidades Agencia a DTOs
 */
@Component
public class AgenciaMapper {

    /**
     * Convierte Agencia a AgenciaResponse completa
     */
    public AgenciaResponse toResponse(Agencia agencia) {
        return AgenciaResponse.builder()
                .idAgencia(agencia.getIdAgencia())
                .nombreAgencia(agencia.getNombreAgencia())
                .ruc(agencia.getRuc())
                .direccion(agencia.getDireccion())
                .telefono(agencia.getTelefono())
                .tipoComision(agencia.getTipoComision())
                .valorComision(agencia.getValorComision())
                .descripcionComision(generarDescripcionComision(agencia))
                .estado(agencia.getEstado())
                .cantidadUsuarios(agencia.getUsuarios() != null ? agencia.getUsuarios().size() : 0)
                .fechaCreacion(agencia.getFechaCreacion())
                .fechaActualizacion(agencia.getFechaActualizacion())
                .build();
    }

    /**
     * Convierte lista de Agencia a lista de AgenciaResponse
     */
    public List<AgenciaResponse> toResponseList(List<Agencia> agencias) {
        return agencias.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convierte Agencia a AgenciaSimpleResponse (para listados)
     */
    public AgenciaSimpleResponse toSimpleResponse(Agencia agencia) {
        return AgenciaSimpleResponse.builder()
                .idAgencia(agencia.getIdAgencia())
                .nombreAgencia(agencia.getNombreAgencia())
                .ruc(agencia.getRuc())
                .telefono(agencia.getTelefono())
                .tipoComision(agencia.getTipoComision())
                .valorComision(agencia.getValorComision())
                .estado(agencia.getEstado())
                .fechaCreacion(agencia.getFechaCreacion())
                .build();
    }

    /**
     * Convierte lista de Agencia a lista de AgenciaSimpleResponse
     */
    public List<AgenciaSimpleResponse> toSimpleResponseList(List<Agencia> agencias) {
        return agencias.stream()
                .map(this::toSimpleResponse)
                .collect(Collectors.toList());
    }

    /**
     * Genera descripción legible de la comisión
     */
    private String generarDescripcionComision(Agencia agencia) {
        if (agencia.getTipoComision() == TipoComision.PORCENTAJE) {
            return String.format("%.2f%% de comisión sobre cada venta", agencia.getValorComision());
        } else {
            return String.format("S/ %.2f por pasaje vendido", agencia.getValorComision());
        }
    }
}