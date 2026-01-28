package com.zoealexa.mapper.transporte;

import com.zoealexa.entity.enums.EstadoViaje;
import org.springframework.stereotype.Component;
import com.zoealexa.dto.transporte.*;
import com.zoealexa.entity.transporte.*;

@Component
public class ViajeMapper {

    /**
     * Convierte ViajeRequestDTO a Entidad Viaje
     */
    public static Viaje toEntity(ViajeRequestDTO dto) {
        if (dto == null) return null;

        return Viaje.builder()
                .fechaViaje(dto.getFechaViaje())
                .horaEmbarque(dto.getHoraEmbarque())
                .cuposDisponibles(dto.getCuposDisponibles())
                .cuposOcupados(0) // Inicialmente 0 cupos ocupados
                .estado(dto.getEstado() != null ? dto.getEstado() : EstadoViaje.PROGRAMADO)
                .build();
        // Nota: ruta y embarcación se establecen en el Service después de buscarlas
    }

    public static ViajeResponseDTO toResponseDTO(Viaje viaje) {
        if (viaje == null) return null;

        return ViajeResponseDTO.builder()
                .idViaje(viaje.getIdViaje())
                .ruta(RutaMapper.toResponseDTO(viaje.getRuta()))
                .embarcacion(EmbarcacionMapper.toResponseDTO(viaje.getEmbarcacion()))
                .fechaViaje(viaje.getFechaViaje())
                .horaEmbarque(viaje.getHoraEmbarque())
                .cuposDisponibles(viaje.getCuposDisponibles())
                .cuposOcupados(viaje.getCuposOcupados())
                .estado(viaje.getEstado())
                .build();
    }

    public static ViajeBusquedaDTO toBusquedaDTO(Viaje viaje) {
        if (viaje == null) return null;

        return ViajeBusquedaDTO.builder()
                .idViaje(viaje.getIdViaje())
                .nombreRuta(viaje.getRuta().getNombreRuta())
                .nombreEmbarcacion(viaje.getEmbarcacion().getNombreEmbarcacion())
                .fechaViaje(viaje.getFechaViaje())
                .horaEmbarque(viaje.getHoraEmbarque())
                .cuposDisponibles(viaje.getCuposDisponibles())
                .ciudadOrigen(viaje.getRuta().getPuertoOrigen().getCiudad())
                .ciudadDestino(viaje.getRuta().getPuertoDestino().getCiudad())
                .build();
    }
}
