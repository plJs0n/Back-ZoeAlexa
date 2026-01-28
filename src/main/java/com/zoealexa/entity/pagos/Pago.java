package com.zoealexa.entity.pagos;

import com.zoealexa.entity.comprobantes.Comprobante;
import com.zoealexa.entity.enums.EstadoPago;
import com.zoealexa.entity.enums.MetodoPago;
import com.zoealexa.entity.enums.TipoPago;
import com.zoealexa.entity.reservas.Reserva;
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
@Table(name = "pago")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long idPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reserva", nullable = false)
    private Reserva reserva;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pago", nullable = false, length = 30)
    private TipoPago tipoPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 30)
    private MetodoPago metodoPago;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "referencia_transaccion", length = 100)
    private String referenciaTransaccion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoPago estado = EstadoPago.CONFIRMADO;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @OneToMany(mappedBy = "pago", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Comprobante> comprobantes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaPago = LocalDateTime.now();
    }

    /**
     * Agrega un comprobante al pago
     */
    public void agregarComprobante(Comprobante comprobante) {
        comprobantes.add(comprobante);
        comprobante.setPago(this);
    }
}