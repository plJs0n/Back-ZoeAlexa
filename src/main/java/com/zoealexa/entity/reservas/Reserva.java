package com.zoealexa.entity.reservas;

import com.zoealexa.entity.enums.EstadoReserva;
import com.zoealexa.entity.pagos.Pago;
import com.zoealexa.entity.seguridad.Agencia;
import com.zoealexa.entity.seguridad.Usuario;
import com.zoealexa.entity.transporte.Viaje;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reserva")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long idReserva;

    @Column(name = "codigo_reserva", nullable = false, unique = true, length = 50)
    private String codigoReserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_viaje", nullable = false)
    private Viaje viaje;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agencia")
    private Agencia agencia;

    // Información denormalizada del viaje
    @Column(name = "origen", nullable = false, length = 100)
    private String origen;

    @Column(name = "destino", nullable = false, length = 100)
    private String destino;

    // Montos
    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "monto_pagado", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal montoPagado = BigDecimal.ZERO;

    @Column(name = "saldo_pendiente", nullable = false, precision = 10, scale = 2)
    private BigDecimal saldoPendiente;

    @Column(name = "penalidad_aplicada", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal penalidadAplicada = BigDecimal.ZERO;

    @Column(name = "comision_agencia", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal comisionAgencia = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoReserva estado = EstadoReserva.PENDIENTE;

    @Column(name = "fecha_reserva", nullable = false)
    private LocalDateTime fechaReserva;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    // Relaciones
    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReservaDetalle> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Pago> pagos = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaReserva = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    // Métodos de utilidad

    /**
     * Agrega un detalle a la reserva
     */
    public void agregarDetalle(ReservaDetalle detalle) {
        detalles.add(detalle);
        detalle.setReserva(this);
    }

    /**
     * Agrega un pago a la reserva
     */
    public void agregarPago(Pago pago) {
        pagos.add(pago);
        pago.setReserva(this);
    }

    /**
     * Calcula el saldo pendiente
     */
    public void calcularSaldoPendiente() {
        this.saldoPendiente = this.total.subtract(this.montoPagado);
    }

    /**
     * Verifica si la reserva fue hecha el mismo día que el viaje
     */
    public boolean esReservaMismoDia() {
        return fechaReserva.toLocalDate().equals(viaje.getFechaViaje());
    }

    /**
     * Verifica si se puede cancelar/reprogramar sin penalidad
     * (mismo día de la reserva)
     */
    public boolean puedeCambiarSinPenalidad() {
        return fechaReserva.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }

    /**
     * Verifica si hay saldo pendiente
     */
    public boolean tieneSaldoPendiente() {
        return saldoPendiente.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Verifica si el adelanto es suficiente (mínimo 50%)
     */
    public boolean tieneAdelantoSuficiente() {
        BigDecimal adelantoMinimo = total.multiply(new BigDecimal("0.50"));
        return montoPagado.compareTo(adelantoMinimo) >= 0;
    }
}