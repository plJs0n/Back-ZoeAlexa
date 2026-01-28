package com.zoealexa.repository.transporte;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.zoealexa.entity.enums.EstadoViaje;
import com.zoealexa.entity.transporte.Viaje;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ViajeRepository extends JpaRepository<Viaje, Integer> {

    /**
     * Busca viajes por ruta y fecha
     */
    List<Viaje> findByRutaIdRutaAndFechaViaje(Integer rutaId, LocalDate fecha);

    /**
     * Busca viajes por estado
     */
    List<Viaje> findByEstadoOrderByFechaViajeDesc(EstadoViaje estado);

    /**
     * Busca viajes con cupos disponibles en un rango de fechas
     */
    @Query("SELECT v FROM Viaje v WHERE v.ruta.idRuta = :rutaId " +
            "AND v.fechaViaje BETWEEN :inicio AND :fin " +
            "AND v.cuposDisponibles > 0 AND v.estado = 'PROGRAMADO' " +
            "ORDER BY v.fechaViaje ASC")
    List<Viaje> findViajesDisponibles(@Param("rutaId") Integer rutaId,
                                      @Param("inicio") LocalDate inicio,
                                      @Param("fin") LocalDate fin);

    /**
     * Busca viajes próximos (siguientes 7 días)
     */
    @Query("SELECT v FROM Viaje v WHERE v.fechaViaje BETWEEN :hoy AND :limite " +
            "AND v.estado = 'PROGRAMADO' ORDER BY v.fechaViaje ASC")
    List<Viaje> findViajesProximos(@Param("hoy") LocalDate hoy,
                                   @Param("limite") LocalDate limite);

    /**
     * Busca viajes de una embarcación en una fecha
     */
    List<Viaje> findByEmbarcacionIdEmbarcacionAndFechaViaje(Integer embarcacionId, LocalDate fecha);

    /**
     * Cuenta viajes por estado en un periodo
     */
    @Query("SELECT COUNT(v) FROM Viaje v WHERE v.estado = :estado " +
            "AND v.fechaViaje BETWEEN :inicio AND :fin")
    Long contarViajesPorEstado(@Param("estado") EstadoViaje estado,
                               @Param("inicio") LocalDate inicio,
                               @Param("fin") LocalDate fin);
}