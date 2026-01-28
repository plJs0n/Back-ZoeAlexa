package com.zoealexa.repository.reservas;

import com.zoealexa.entity.enums.EstadoReserva;
import com.zoealexa.entity.reservas.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    Optional<Reserva> findByCodigoReserva(String codigoReserva);

    boolean existsByCodigoReserva(String codigoReserva);

    List<Reserva> findByEstado(EstadoReserva estado);

    List<Reserva> findByViajeIdViaje(Long idViaje);

    List<Reserva> findByUsuarioIdUsuario(Long idUsuario);

    List<Reserva> findByAgenciaIdAgencia(Integer idAgencia);

    @Query("SELECT r FROM Reserva r WHERE r.viaje.fechaViaje = :fecha")
    List<Reserva> findByFechaViaje(@Param("fecha") LocalDate fecha);

    @Query("SELECT r FROM Reserva r WHERE r.viaje.fechaViaje BETWEEN :inicio AND :fin")
    List<Reserva> findByRangoFechas(
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin
    );

    /**
     * Cuenta el número de reservas para un viaje específico
     */
    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.viaje.idViaje = :idViaje AND r.estado != 'CANCELADA'")
    Long countReservasActivasByViaje(@Param("idViaje") Long idViaje);

    /**
     * Obtiene el último número de reserva del año actual para generar código
     */
    @Query(value = "SELECT MAX(CAST(SUBSTRING(codigo_reserva, 9) AS INTEGER)) " +
            "FROM reserva WHERE codigo_reserva LIKE :patron",
            nativeQuery = true)
    Integer findUltimoNumeroReservaDelAnio(@Param("patron") String patron);
}