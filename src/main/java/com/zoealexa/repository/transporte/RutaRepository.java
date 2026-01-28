package com.zoealexa.repository.transporte;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.zoealexa.entity.enums.EstadoRuta;
import com.zoealexa.entity.transporte.Ruta;

import java.util.List;
import java.util.Optional;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Integer> {

    /**
     * Busca rutas por estado
     */
    List<Ruta> findByEstado(EstadoRuta estado);

    /**
     * Busca rutas activas
     */
    List<Ruta> findByEstadoOrderByNombreRutaAsc(EstadoRuta estado);

    /**
     * Busca ruta por origen y destino
     */
    @Query("SELECT r FROM Ruta r WHERE r.puertoOrigen.idPuerto = :origenId " +
            "AND r.puertoDestino.idPuerto = :destinoId AND r.estado = 'ACTIVA'")
    Optional<Ruta> findRutaActiva(@Param("origenId") Integer origenId,
                                  @Param("destinoId") Integer destinoId);

    /**
     * Busca rutas que parten de un puerto
     */
    @Query("SELECT r FROM Ruta r WHERE r.puertoOrigen.idPuerto = :puertoId AND r.estado = 'ACTIVA'")
    List<Ruta> findRutasPorOrigen(@Param("puertoId") Integer puertoId);

    /**
     * Busca rutas que llegan a un puerto
     */
    @Query("SELECT r FROM Ruta r WHERE r.puertoDestino.idPuerto = :puertoId AND r.estado = 'ACTIVA'")
    List<Ruta> findRutasPorDestino(@Param("puertoId") Integer puertoId);
}
