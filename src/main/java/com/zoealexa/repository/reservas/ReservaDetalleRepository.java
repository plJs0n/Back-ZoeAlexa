package com.zoealexa.repository.reservas;

import com.zoealexa.entity.reservas.ReservaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaDetalleRepository extends JpaRepository<ReservaDetalle, Long> {

    List<ReservaDetalle> findByReservaIdReserva(Long idReserva);

    List<ReservaDetalle> findByPasajeroIdPasajero(Long idPasajero);

    boolean existsByReservaIdReservaAndPasajeroIdPasajero(Long idReserva, Long idPasajero);

    Optional<ReservaDetalle> findByReservaIdReservaAndPasajeroIdPasajero(Long idReserva, Long idPasajero);
}
