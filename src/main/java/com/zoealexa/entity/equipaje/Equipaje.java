package com.zoealexa.entity.equipaje;

import com.zoealexa.entity.comprobantes.Comprobante;
import com.zoealexa.entity.reservas.Pasajero;
import com.zoealexa.entity.reservas.Reserva;
import com.zoealexa.entity.reservas.ReservaDetalle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipaje")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Equipaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipaje")
    private Long idEquipaje;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reserva", nullable = false)
    private Reserva reserva;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_detalle", unique = true)
    private ReservaDetalle reservaDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pasajero", nullable = false)
    private Pasajero pasajero;

    @Column(name = "peso_kg", nullable = false, precision = 5, scale = 2)
    private BigDecimal pesoKg;

    @Column(name = "limite_incluido", nullable = false, precision = 5, scale = 2)
    private BigDecimal limiteIncluido;

    @Column(name = "peso_excedente_kg", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal pesoExcedenteKg = BigDecimal.ZERO;

    @Column(name = "volumen_m3", precision = 5, scale = 2)
    private BigDecimal volumenM3;

    @Column(name = "precio_por_kilo", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioPorKilo;

    @Column(name = "costo_exceso", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal costoExceso = BigDecimal.ZERO;

    @Column(name = "descripcion", length = 200)
    private String descripcion;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    // Relación con comprobante (si se genera boleta por exceso)
    @OneToOne(mappedBy = "equipaje", cascade = CascadeType.ALL)
    private Comprobante comprobanteExceso;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
        calcularExceso();
    }

    /**
     * Calcula el peso excedente y el costo adicional
     */
    public void calcularExceso() {
        // Calcular peso excedente
        BigDecimal exceso = pesoKg.subtract(limiteIncluido);
        this.pesoExcedenteKg = exceso.max(BigDecimal.ZERO);

        // Calcular costo del exceso
        this.costoExceso = this.pesoExcedenteKg.multiply(this.precioPorKilo);
    }

    /**
     * Verifica si hay exceso de equipaje
     */
    public boolean tieneExceso() {
        return pesoExcedenteKg.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Verifica si ya se generó comprobante por el exceso
     */
    public boolean tieneComprobanteExceso() {
        return comprobanteExceso != null;
    }
}