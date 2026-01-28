package com.zoealexa.mapper.tarifas;

import com.zoealexa.dto.tarifas.ReglaDescuentoRequestDTO;
import com.zoealexa.dto.tarifas.ReglaDescuentoResponseDTO;
import com.zoealexa.entity.tarifas.ReglaDescuento;

/**
 * Mapper para conversión entre ReglaDescuento y DTOs
 */
public class ReglaDescuentoMapper {

    /**
     * Convierte ReglaDescuentoRequestDTO a Entity
     */
    public static ReglaDescuento toEntity(ReglaDescuentoRequestDTO dto) {
        if (dto == null) return null;

        return ReglaDescuento.builder()
                .descripcion(dto.getDescripcion())
                .edadMinima(dto.getEdadMinima())
                .edadMaxima(dto.getEdadMaxima())
                .tipoValor(dto.getTipoValor())
                .valor(dto.getValor())
                .activa(dto.getActiva() != null ? dto.getActiva() : true)
                .build();
    }

    /**
     * Convierte Entity a ReglaDescuentoResponseDTO
     */
    public static ReglaDescuentoResponseDTO toResponseDTO(ReglaDescuento entity) {
        if (entity == null) return null;

        return ReglaDescuentoResponseDTO.builder()
                .idDescuento(entity.getIdDescuento())
                .descripcion(entity.getDescripcion())
                .edadMinima(entity.getEdadMinima())
                .edadMaxima(entity.getEdadMaxima())
                .tipoValor(entity.getTipoValor())
                .valor(entity.getValor())
                .activa(entity.getActiva())
                .fechaCreacion(entity.getFechaCreacion())
                .build();
    }
}