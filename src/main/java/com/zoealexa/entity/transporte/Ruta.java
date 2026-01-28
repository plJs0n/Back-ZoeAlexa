package com.zoealexa.entity.transporte;

import com.zoealexa.entity.auditoria.Auditable;
import com.zoealexa.entity.enums.EstadoRuta;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "ruta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ruta extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ruta")
    private Integer idRuta;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_puerto_origen", nullable = false)
    private Puerto puertoOrigen;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_puerto_destino", nullable = false)
    private Puerto puertoDestino;

    @Size(max = 150)
    @Column(name = "nombre_ruta", length = 150)
    private String nombreRuta;

    @Size(max = 50)
    @Column(name = "dias_operacion", length = 50)
    private String diasOperacion;

    @NotNull @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoRuta estado = EstadoRuta.ACTIVA;

    @PrePersist
    @PreUpdate
    private void generarNombre() {
        if (nombreRuta == null && puertoOrigen != null && puertoDestino != null) {
            this.nombreRuta = String.format("%s - %s",
                    puertoOrigen.getCiudad(),
                    puertoDestino.getCiudad());
        }
    }
}
