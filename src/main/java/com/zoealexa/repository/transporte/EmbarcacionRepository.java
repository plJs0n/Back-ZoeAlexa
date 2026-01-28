package com.zoealexa.repository.transporte;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.zoealexa.entity.enums.EstadoEmbarcacion;
import com.zoealexa.entity.transporte.Embarcacion;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmbarcacionRepository extends JpaRepository<Embarcacion, Integer> {

    boolean existsByNombreEmbarcacion(String nombreEmbarcacion);

    // Para validar duplicados en Update excluyendo el ID actual
    boolean existsByNombreEmbarcacionAndIdEmbarcacionNot(String nombre, Integer id);

    /**
     * Busca embarcación por nombre
     */
    Optional<Embarcacion> findByNombreEmbarcacion(String nombre);

    /**
     * Busca embarcaciones por estado
     */
    List<Embarcacion> findByEstado(EstadoEmbarcacion estado);

    /**
     * Busca embarcaciones disponibles (EN_SERVICIO)
     */
    List<Embarcacion> findByEstadoOrderByNombreEmbarcacionAsc(EstadoEmbarcacion estado);

    /**
     * Busca embarcaciones con capacidad mínima
     */
    List<Embarcacion> findByCapacidadGreaterThanEqualAndEstado(Integer capacidadMinima,
                                                               EstadoEmbarcacion estado);
}