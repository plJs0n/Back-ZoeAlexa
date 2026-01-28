package com.zoealexa.mapper.transporte;

import org.springframework.stereotype.Component;
import com.zoealexa.dto.transporte.*;
import com.zoealexa.entity.transporte.*;

@Component
public class PuertoMapper {

    public static PuertoResponseDTO toResponseDTO(Puerto puerto) {
        if (puerto == null) return null;

        return PuertoResponseDTO.builder()
                .idPuerto(puerto.getIdPuerto())
                .ciudad(puerto.getCiudad())
                .nombrePuerto(puerto.getNombrePuerto())
                .tipoOperacion(puerto.getTipoOperacion())
                .direccion(puerto.getDireccion())
                .estado(puerto.getEstado())
                .build();
    }

    public static Puerto toEntity(PuertoRequestDTO dto) {
        if (dto == null) return null;

        return Puerto.builder()
                .ciudad(dto.getCiudad())
                .nombrePuerto(dto.getNombrePuerto())
                .tipoOperacion(dto.getTipoOperacion())
                .direccion(dto.getDireccion())
                .estado(dto.getEstado())
                .build();
    }
}
