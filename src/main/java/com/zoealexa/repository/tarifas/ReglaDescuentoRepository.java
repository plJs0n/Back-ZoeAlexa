package com.zoealexa.repository.tarifas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.zoealexa.entity.tarifas.ReglaDescuento;

import java.util.List;
import java.util.Optional;

/**
 * Repository de Reglas de Descuento
 */
@Repository
public interface ReglaDescuentoRepository extends JpaRepository<ReglaDescuento, Integer> {

    @Query("SELECT r FROM ReglaDescuento r WHERE r.activa = :activa " +
            "AND (r.edadMinima IS NULL OR :edad >= r.edadMinima) " +
            "AND (r.edadMaxima IS NULL OR :edad <= r.edadMaxima)")
    List<ReglaDescuento> findByEdadAndActiva(@Param("edad") Integer edad, @Param("activa") Boolean activa);

    /**
     * Lista reglas de descuento activas
     */
    List<ReglaDescuento> findByActivaTrue();

    /**
     * Lista reglas ordenadas por fecha de creación
     */
    @Query("SELECT r FROM ReglaDescuento r ORDER BY r.fechaCreacion DESC")
    List<ReglaDescuento> findAllOrderByFechaCreacionDesc();

    /**
     * Lista reglas activas ordenadas por valor descendente
     */
    @Query("SELECT r FROM ReglaDescuento r WHERE r.activa = true ORDER BY r.valor DESC")
    List<ReglaDescuento> findActivasOrderByValorDesc();

    /**
     * Busca reglas aplicables para una edad específica
     *
     * @param edad Edad del pasajero
     * @return Lista de reglas que aplican para esa edad
     */
    @Query("SELECT r FROM ReglaDescuento r WHERE r.activa = true " +
            "AND (r.edadMinima IS NULL OR :edad >= r.edadMinima) " +
            "AND (r.edadMaxima IS NULL OR :edad <= r.edadMaxima)")
    List<ReglaDescuento> findReglasAplicablesParaEdad(@Param("edad") int edad);

    /**
     * Busca la mejor regla (mayor descuento) para una edad
     *
     * @param edad Edad del pasajero
     * @return Regla con mayor descuento
     */
    @Query("SELECT r FROM ReglaDescuento r WHERE r.activa = true " +
            "AND (r.edadMinima IS NULL OR :edad >= r.edadMinima) " +
            "AND (r.edadMaxima IS NULL OR :edad <= r.edadMaxima) " +
            "ORDER BY r.valor DESC")
    List<ReglaDescuento> findMejorReglaParaEdad(@Param("edad") int edad);

    /**
     * Busca reglas por rango de edad
     */
    @Query("SELECT r FROM ReglaDescuento r WHERE r.activa = true " +
            "AND r.edadMinima = :edadMin AND r.edadMaxima = :edadMax")
    Optional<ReglaDescuento> findByRangoEdad(
            @Param("edadMin") Integer edadMinima,
            @Param("edadMax") Integer edadMaxima
    );

    /**
     * Cuenta reglas activas
     */
    @Query("SELECT COUNT(r) FROM ReglaDescuento r WHERE r.activa = true")
    Long contarActivas();

    /**
     * Busca reglas por tipo de valor
     */
    @Query("SELECT r FROM ReglaDescuento r WHERE r.activa = true " +
            "AND r.tipoValor = com.zoealexa.entity.enums.TipoValor.PORCENTAJE")
    List<ReglaDescuento> findPorcentajes();

    @Query("SELECT r FROM ReglaDescuento r WHERE r.activa = true " +
            "AND r.tipoValor = com.zoealexa.entity.enums.TipoValor.MONTO_FIJO")
    List<ReglaDescuento> findMontosFijos();
}