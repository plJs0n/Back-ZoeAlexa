package com.zoealexa.repository.reservas;

import com.zoealexa.entity.equipaje.Equipaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipajeRepository extends JpaRepository<Equipaje, Long> {

    List<Equipaje> findByReservaIdReserva(Long idReserva);

    Optional<Equipaje> findByReservaIdReservaAndPasajeroIdPasajero(Long idReserva, Long idPasajero);

    @Query("SELECT e FROM Equipaje e WHERE e.pesoExcedenteKg > 0")
    List<Equipaje> findEquipajesConExceso();

    @Query("SELECT e FROM Equipaje e WHERE e.pesoExcedenteKg > 0 AND e.comprobanteExceso IS NULL")
    List<Equipaje> findEquipajesConExcesoSinComprobante();
}
