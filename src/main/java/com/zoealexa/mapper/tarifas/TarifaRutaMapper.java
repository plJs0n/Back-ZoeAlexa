package com.zoealexa.mapper.tarifas;

import com.zoealexa.dto.tarifas.TarifaRutaResponseDTO;
import com.zoealexa.entity.tarifas.TarifaRuta;

/**
 * Mapper para conversión entre TarifaRuta entity y DTOs
 *
 * IMPORTANTE:
 * - El mapper NO crea entidades
 * - Las fechas se asignan en el Service
 */
public class TarifaRutaMapper {

    /**
     * Convierte Entity a DTO de respuesta
     */
    public static TarifaRutaResponseDTO toResponseDTO(TarifaRuta entity) {
        if (entity == null) return null;

        String nombreRuta = entity.getRuta().getPuertoOrigen().getCiudad()
                + " → "
                + entity.getRuta().getPuertoDestino().getCiudad();

        boolean vigente = entity.estaVigente();

        String descripcionVigencia = vigente
                ? "Vigente desde " + entity.getFechaInicio()
                : "Vigente desde " + entity.getFechaInicio() +
                " hasta " + entity.getFechaFin();

        return TarifaRutaResponseDTO.builder()
                .idTarifa(entity.getIdTarifa())
                .idRuta(entity.getRuta().getIdRuta())
                .nombreRuta(nombreRuta)
                .ciudadOrigen(entity.getRuta().getPuertoOrigen().getCiudad())
                .ciudadDestino(entity.getRuta().getPuertoDestino().getCiudad())
                .precioBase(entity.getPrecioBase())
                .fechaInicio(entity.getFechaInicio())
                .fechaFin(entity.getFechaFin())
                .vigente(vigente)
                .descripcionVigencia(descripcionVigencia)
                .build();
    }
}
