package com.zoealexa.repository.pago;

import com.zoealexa.entity.enums.TipoOperacionCancelacion;
import com.zoealexa.entity.reservas.CancelacionReprogramacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CancelacionReprogramacionRepository extends JpaRepository<CancelacionReprogramacion, Long> {

    List<CancelacionReprogramacion> findByReservaIdReserva(Long idReserva);

    List<CancelacionReprogramacion> findByTipoOperacion(
            TipoOperacionCancelacion tipoOperacion
    );

    List<CancelacionReprogramacion> findByViajeOriginalIdViaje(Long idViajeOriginal);

    List<CancelacionReprogramacion> findByViajeNuevoIdViaje(Long idViajeNuevo);

    @Query("SELECT COUNT(c) FROM CancelacionReprogramacion c " +
            "WHERE c.reserva.idReserva = :idReserva")
    Long countByReserva(@Param("idReserva") Long idReserva);
}
