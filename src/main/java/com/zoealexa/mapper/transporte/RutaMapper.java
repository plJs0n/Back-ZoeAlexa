package com.zoealexa.mapper.transporte;

import org.springframework.stereotype.Component;
import com.zoealexa.dto.transporte.*;
import com.zoealexa.entity.transporte.*;

@Component
public class RutaMapper {

    public static RutaResponseDTO toResponseDTO(Ruta ruta) {
        if (ruta == null) return null;

        return RutaResponseDTO.builder()
                .idRuta(ruta.getIdRuta())
                .nombreRuta(ruta.getNombreRuta())
                .puertoOrigen(PuertoMapper.toResponseDTO(ruta.getPuertoOrigen()))
                .puertoDestino(PuertoMapper.toResponseDTO(ruta.getPuertoDestino()))
                .diasOperacion(ruta.getDiasOperacion())
                .estado(ruta.getEstado())
                .fechaCreacion(ruta.getFechaCreacion())
                .build();
    }

    public static Ruta toEntity(RutaRequestDTO dto) {
        if (dto == null) return null;

        return Ruta.builder()
                .diasOperacion(dto.getDiasOperacion())
                .estado(dto.getEstado())
                .build();
    }
}
