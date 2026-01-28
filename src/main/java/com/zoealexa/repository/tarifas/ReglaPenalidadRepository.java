package com.zoealexa.repository.tarifas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.zoealexa.entity.enums.TipoPenalidad;
import com.zoealexa.entity.tarifas.ReglaPenalidad;

import java.util.List;
import java.util.Optional;

/**
 * Repository de Reglas de Penalidad
 */
@Repository
public interface ReglaPenalidadRepository extends JpaRepository<ReglaPenalidad, Integer> {

    /**
     * Lista reglas de penalidad activas
     */
    List<ReglaPenalidad> findByActivaTrue();

    /**
     * Lista reglas ordenadas por fecha de creación
     */
    @Query("SELECT r FROM ReglaPenalidad r ORDER BY r.fechaCreacion DESC")
    List<ReglaPenalidad> findAllOrderByFechaCreacionDesc();

    /**
     * Busca regla por tipo de penalidad
     */
    List<ReglaPenalidad> findByTipoPenalidad(TipoPenalidad tipoPenalidad);

    /**
     * Busca regla activa por tipo de penalidad
     */
    @Query("SELECT r FROM ReglaPenalidad r WHERE r.activa = true " +
            "AND r.tipoPenalidad = :tipoPenalidad")
    List<ReglaPenalidad> findByTipoPenalidadActiva(@Param("tipoPenalidad") TipoPenalidad tipoPenalidad);

    List<ReglaPenalidad> findByTipoPenalidadAndActiva(String tipoPenalidad, Boolean activa);


    /**
     * Busca la primera regla activa de un tipo
     * (útil para tipos que deben tener solo una regla)
     */
    @Query("SELECT r FROM ReglaPenalidad r WHERE r.activa = true " +
            "AND r.tipoPenalidad = :tipoPenalidad ORDER BY r.fechaCreacion DESC")
    Optional<ReglaPenalidad> findPrimeraReglaPorTipo(@Param("tipoPenalidad") TipoPenalidad tipoPenalidad);

    /**
     * Busca regla de equipaje activa
     */
    @Query("SELECT r FROM ReglaPenalidad r WHERE r.activa = true " +
            "AND r.tipoPenalidad = 'EQUIPAJE'")
    Optional<ReglaPenalidad> findReglaEquipajeActiva();

    /**
     * Busca reglas de cancelación activas
     */
    @Query("SELECT r FROM ReglaPenalidad r WHERE r.activa = true " +
            "AND r.tipoPenalidad = 'CANCELACION' " +
            "ORDER BY r.valor DESC")
    List<ReglaPenalidad> findReglasCancelacionActivas();

    /**
     * Busca reglas de reprogramación activas
     */
    @Query("SELECT r FROM ReglaPenalidad r WHERE r.activa = true " +
            "AND r.tipoPenalidad = 'REPROGRAMACION' " +
            "ORDER BY r.valor DESC")
    List<ReglaPenalidad> findReglasReprogramacionActivas();

    /**
     * Cuenta reglas activas
     */
    @Query("SELECT COUNT(r) FROM ReglaPenalidad r WHERE r.activa = true")
    Long contarActivas();

    /**
     * Cuenta reglas activas por tipo
     */
    @Query("SELECT COUNT(r) FROM ReglaPenalidad r WHERE r.activa = true " +
            "AND r.tipoPenalidad = :tipo")
    Long contarActivasPorTipo(@Param("tipo") TipoPenalidad tipoPenalidad);
}