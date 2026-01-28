package com.zoealexa.mapper.transporte;


import org.springframework.stereotype.Component;
import com.zoealexa.dto.transporte.*;
import com.zoealexa.entity.transporte.*;

@Component
public class EmbarcacionMapper {

    public static EmbarcacionResponseDTO toResponseDTO(Embarcacion embarcacion) {
        if (embarcacion == null) return null;

        return EmbarcacionResponseDTO.builder()
                .idEmbarcacion(embarcacion.getIdEmbarcacion())
                .nombreEmbarcacion(embarcacion.getNombreEmbarcacion())
                .capacidad(embarcacion.getCapacidad())
                .estado(embarcacion.getEstado())
                .fechaCreacion(embarcacion.getFechaCreacion())
                .build();
    }

    public static Embarcacion toEntity(EmbarcacionRequestDTO dto) {
        if (dto == null) return null;

        return Embarcacion.builder()
                .nombreEmbarcacion(dto.getNombreEmbarcacion())
                .capacidad(dto.getCapacidad())
                .estado(dto.getEstado())
                .build();
    }
}
