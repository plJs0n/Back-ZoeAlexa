package com.zoealexa.mapper.tarifas;

import com.zoealexa.dto.tarifas.ReglaPenalidadRequestDTO;
import com.zoealexa.dto.tarifas.ReglaPenalidadResponseDTO;
import com.zoealexa.entity.tarifas.ReglaPenalidad;

/**
 * Mapper para conversión entre ReglaPenalidad y DTOs
 */
public class ReglaPenalidadMapper {

    /**
     * Convierte ReglaPenalidadRequestDTO a Entity
     */
    public static ReglaPenalidad toEntity(ReglaPenalidadRequestDTO dto) {
        if (dto == null) return null;

        return ReglaPenalidad.builder()
                .tipoPenalidad(dto.getTipoPenalidad())
                .descripcion(dto.getDescripcion())
                .tipoValor(dto.getTipoValor())
                .valor(dto.getValor())
                .kilosPermitidos(dto.getKilosPermitidos())
                .precioPorKilo(dto.getPrecioPorKilo())
                .activa(dto.getActiva() != null ? dto.getActiva() : true)
                .build();
    }

    /**
     * Convierte Entity a ReglaPenalidadResponseDTO
     */
    public static ReglaPenalidadResponseDTO toResponseDTO(ReglaPenalidad entity) {
        if (entity == null) return null;

        return ReglaPenalidadResponseDTO.builder()
                .idPenalidad(entity.getIdPenalidad())
                .tipoPenalidad(entity.getTipoPenalidad())
                .descripcion(entity.getDescripcion())
                .tipoValor(entity.getTipoValor())
                .valor(entity.getValor())
                .kilosPermitidos(entity.getKilosPermitidos())
                .precioPorKilo(entity.getPrecioPorKilo())
                .activa(entity.getActiva())
                .fechaCreacion(entity.getFechaCreacion())
                .build();
    }
}