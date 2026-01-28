package com.zoealexa.repository.pago;

import com.zoealexa.entity.comprobantes.Comprobante;
import com.zoealexa.entity.enums.EstadoSunat;
import com.zoealexa.entity.enums.TipoComprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {

    Optional<Comprobante> findBySerieAndNumero(String serie, String numero);

    List<Comprobante> findByReservaIdReserva(Long idReserva);

    List<Comprobante> findByPagoIdPago(Long idPago);

    List<Comprobante> findByEstadoSunat(EstadoSunat estadoSunat);

    List<Comprobante> findByTipo(TipoComprobante tipo);

    /**
     * Obtiene el último número de comprobante por serie
     */
    @Query(value = "SELECT MAX(CAST(numero AS INTEGER)) " +
            "FROM comprobante WHERE serie = :serie",
            nativeQuery = true)
    Integer findUltimoNumeroBySerie(@Param("serie") String serie);

//    /**
//     * Encuentra comprobantes pendientes de envío a SUNAT
//     */
//    @Query("SELECT c FROM Comprobante c WHERE c.estadoSunat = 'PENDIENTE' " +
//            "AND c.fechaEmision >= CURRENT_DATE - 7")
//    List<Comprobante> findComprobantesPendientesSunat();
}
