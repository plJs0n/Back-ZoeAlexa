package com.zoealexa.dto.transporte;

import com.zoealexa.entity.enums.EstadoViaje;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO para actualizar información de un viaje
 * Todos los campos son opcionales para permitir actualizaciones parciales
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViajeUpdateDTO {

    /**
     * ID de la ruta (opcional)
     */
    private Integer idRuta;

    /**
     * ID de la embarcación (opcional)
     */
    private Integer idEmbarcacion;

    /**
     * Fecha del viaje (opcional)
     */
    private LocalDate fechaViaje;

    /**
     * Hora de embarque (opcional)
     */
    private LocalTime horaEmbarque;

    /**
     * Cantidad de cupos disponibles (opcional)
     * Debe ser positivo si se proporciona
     */
    @Min(value = 1, message = "Los cupos disponibles deben ser al menos 1")
    private Integer cuposDisponibles;

    /**
     * Estado del viaje (opcional)
     */
    private EstadoViaje estado;

    // Métodos de utilidad para verificar si un campo está presente

    public boolean hasIdRuta() {
        return idRuta != null;
    }

    public boolean hasIdEmbarcacion() {
        return idEmbarcacion != null;
    }

    public boolean hasFechaViaje() {
        return fechaViaje != null;
    }

    public boolean hasHoraEmbarque() {
        return horaEmbarque != null;
    }

    public boolean hasCuposDisponibles() {
        return cuposDisponibles != null;
    }

    public boolean hasEstado() {
        return estado != null;
    }
}