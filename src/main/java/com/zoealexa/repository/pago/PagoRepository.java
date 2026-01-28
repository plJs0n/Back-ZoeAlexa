package com.zoealexa.repository.pago;

import com.zoealexa.entity.enums.EstadoPago;
import com.zoealexa.entity.enums.MetodoPago;
import com.zoealexa.entity.pagos.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByReservaIdReserva(Long idReserva);

    List<Pago> findByEstado(EstadoPago estado);

    List<Pago> findByMetodoPago(MetodoPago metodoPago);

    @Query("SELECT SUM(p.monto) FROM Pago p WHERE p.reserva.idReserva = :idReserva AND p.estado = 'CONFIRMADO'")
    Optional<BigDecimal> sumMontosPagadosByReserva(@Param("idReserva") Long idReserva);
}
