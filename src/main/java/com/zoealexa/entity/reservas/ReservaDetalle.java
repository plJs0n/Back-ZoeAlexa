package com.zoealexa.entity.reservas;

import com.zoealexa.entity.equipaje.Equipaje;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "reserva_detalle",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id_reserva", "id_pasajero"})
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Long idDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reserva", nullable = false)
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pasajero", nullable = false)
    private Pasajero pasajero;

    @Column(name = "tipo_tarifa", length = 50)
    private String tipoTarifa;

    @Column(name = "precio_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBase;

    @Column(name = "porcentaje_descuento", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal porcentajeDescuento = BigDecimal.ZERO;

    @Column(name = "monto_descuento", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal montoDescuento = BigDecimal.ZERO;

    @Column(name = "precio_final", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioFinal;

    // Relación uno a uno con equipaje (opcional)
    @OneToOne(mappedBy = "reservaDetalle", cascade = CascadeType.ALL)
    private Equipaje equipaje;

    /**
     * Calcula el precio final aplicando el descuento
     */
    public void calcularPrecioFinal() {
        if (montoDescuento == null) {
            montoDescuento = BigDecimal.ZERO;
        }
        this.precioFinal = this.precioBase.subtract(this.montoDescuento);

        // Asegurar que el precio final no sea negativo
        if (this.precioFinal.compareTo(BigDecimal.ZERO) < 0) {
            this.precioFinal = BigDecimal.ZERO;
        }
    }

    /**
     * Establece el descuento basado en porcentaje
     */
    public void aplicarDescuentoPorcentaje(BigDecimal porcentaje) {
        this.porcentajeDescuento = porcentaje;
        this.montoDescuento = this.precioBase
                .multiply(porcentaje)
                .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
        calcularPrecioFinal();
    }

    /**
     * Establece el descuento basado en monto fijo
     */
    public void aplicarDescuentoMontoFijo(BigDecimal monto) {
        this.montoDescuento = monto;
        this.porcentajeDescuento = monto
                .multiply(new BigDecimal("100"))
                .divide(this.precioBase, 2, java.math.RoundingMode.HALF_UP);
        calcularPrecioFinal();
    }
}