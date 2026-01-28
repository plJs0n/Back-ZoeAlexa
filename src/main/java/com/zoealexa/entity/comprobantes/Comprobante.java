package com.zoealexa.entity.comprobantes;

import com.zoealexa.entity.enums.EstadoSunat;
import com.zoealexa.entity.enums.TipoComprobante;
import com.zoealexa.entity.equipaje.Equipaje;
import com.zoealexa.entity.pagos.Pago;
import com.zoealexa.entity.reservas.Reserva;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "comprobante",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"serie", "numero"})
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comprobante")
    private Long idComprobante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pago", nullable = false)
    private Pago pago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reserva", nullable = false)
    private Reserva reserva;

    // Relación opcional con equipaje (solo si es comprobante por exceso)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipaje")
    private Equipaje equipaje;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoComprobante tipo;

    @Column(name = "concepto", length = 100)
    @Builder.Default
    private String concepto = "Venta de pasaje de transporte fluvial";

    @Column(name = "serie", nullable = false, length = 10)
    private String serie;

    @Column(name = "numero", nullable = false, length = 20)
    private String numero;

    // Datos del cliente (obligatorio para facturas)
    @Column(name = "ruc_cliente", length = 11)
    private String rucCliente;

    @Column(name = "razon_social", length = 200)
    private String razonSocial;

    @Column(name = "direccion_cliente", columnDefinition = "TEXT")
    private String direccionCliente;

    // Montos
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "igv", nullable = false, precision = 10, scale = 2)
    private BigDecimal igv;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    // Integración SUNAT
    @Column(name = "cpe_xml", columnDefinition = "TEXT")
    private String cpeXml;

    @Column(name = "cdr_xml", columnDefinition = "TEXT")
    private String cdrXml;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_sunat", nullable = false, length = 30)
    @Builder.Default
    private EstadoSunat estadoSunat = EstadoSunat.PENDIENTE;

    @Column(name = "codigo_respuesta", length = 10)
    private String codigoRespuesta;

    @Column(name = "mensaje_respuesta", columnDefinition = "TEXT")
    private String mensajeRespuesta;

    @Column(name = "hash_cpe")
    private String hashCpe;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @Column(name = "fecha_envio_sunat")
    private LocalDateTime fechaEnvioSunat;

    @PrePersist
    protected void onCreate() {
        fechaEmision = LocalDateTime.now();
        calcularMontos();
    }

    /**
     * Calcula subtotal, IGV y total
     */
    public void calcularMontos() {
        if (this.subtotal != null) {
            // IGV = 18% del subtotal
            this.igv = this.subtotal
                    .multiply(new BigDecimal("0.18"))
                    .setScale(2, java.math.RoundingMode.HALF_UP);

            // Total = subtotal + IGV
            this.total = this.subtotal.add(this.igv);
        }
    }

    /**
     * Calcula el subtotal a partir del total (descomponer el IGV)
     */
    public void calcularSubtotalDesdeTotal(BigDecimal totalConIgv) {
        // Subtotal = Total / 1.18
        this.subtotal = totalConIgv
                .divide(new BigDecimal("1.18"), 2, java.math.RoundingMode.HALF_UP);
        calcularMontos();
    }

    /**
     * Verifica si el comprobante está aceptado por SUNAT
     */
    public boolean esAceptadoPorSunat() {
        return EstadoSunat.ACEPTADO.equals(this.estadoSunat);
    }

    /**
     * Verifica si es un comprobante por exceso de equipaje
     */
    public boolean esComprobanteEquipaje() {
        return equipaje != null;
    }
}