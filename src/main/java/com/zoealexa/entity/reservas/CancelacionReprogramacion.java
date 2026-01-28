package com.zoealexa.entity.reservas;

import com.zoealexa.entity.enums.TipoOperacionCancelacion;
import com.zoealexa.entity.seguridad.Usuario;
import com.zoealexa.entity.transporte.Viaje;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cancelacion_reprogramacion")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelacionReprogramacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_operacion")
    private Long idOperacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reserva", nullable = false)
    private Reserva reserva;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_operacion", nullable = false, length = 30)
    private TipoOperacionCancelacion tipoOperacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_viaje_original", nullable = false)
    private Viaje viajeOriginal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_viaje_nuevo")
    private Viaje viajeNuevo;

    @Column(name = "monto_original", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoOriginal;

    @Column(name = "porcentaje_penalidad", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajePenalidad;

    @Column(name = "monto_penalidad", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoPenalidad;

    @Column(name = "monto_devolver", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoDevolver;

    @Column(name = "motivo", columnDefinition = "TEXT")
    private String motivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_operacion", nullable = false)
    private LocalDateTime fechaOperacion;

    @PrePersist
    protected void onCreate() {
        fechaOperacion = LocalDateTime.now();
    }

    /**
     * Calcula el monto de penalidad basado en el porcentaje
     */
    public void calcularPenalidad() {
        this.montoPenalidad = this.montoOriginal
                .multiply(this.porcentajePenalidad)
                .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);

        this.montoDevolver = this.montoOriginal.subtract(this.montoPenalidad);

        // Asegurar que el monto a devolver no sea negativo
        if (this.montoDevolver.compareTo(BigDecimal.ZERO) < 0) {
            this.montoDevolver = BigDecimal.ZERO;
        }
    }

    /**
     * Verifica si es una cancelación
     */
    public boolean esCancelacion() {
        return TipoOperacionCancelacion.CANCELACION.equals(this.tipoOperacion);
    }

    /**
     * Verifica si es una reprogramación
     */
    public boolean esReprogramacion() {
        return TipoOperacionCancelacion.REPROGRAMACION.equals(this.tipoOperacion);
    }
}