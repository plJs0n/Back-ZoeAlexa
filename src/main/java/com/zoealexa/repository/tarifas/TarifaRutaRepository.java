package com.zoealexa.repository.tarifas;

import com.zoealexa.entity.transporte.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.zoealexa.entity.tarifas.TarifaRuta;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository de TarifaRuta
 *
 * Nota: Una ruta solo puede tener UNA tarifa (OneToOne con unique constraint)
 */
@Repository
public interface TarifaRutaRepository extends JpaRepository<TarifaRuta, Integer> {

    Optional<TarifaRuta> findByRutaIdRutaAndFechaFinIsNull(Integer rutaId);

    /**
     * Busca tarifa activa por ruta
     * CRÍTICO: Usado en ReservaService para obtener el precio base
     */
    @Query("SELECT t FROM TarifaRuta t WHERE t.ruta = :ruta AND t.fechaFin IS NULL")
    Optional<TarifaRuta> findByRutaAndActivaTrue(@Param("ruta") Ruta ruta);

    Optional<TarifaRuta> findByRutaAndFechaFinIsNull(Ruta ruta);


}