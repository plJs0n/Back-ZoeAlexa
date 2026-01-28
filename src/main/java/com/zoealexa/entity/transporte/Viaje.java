package com.zoealexa.entity.transporte;

import com.zoealexa.entity.auditoria.Auditable;
import com.zoealexa.entity.enums.EstadoViaje;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "viaje", indexes = {
        @Index(name = "idx_viaje_ruta", columnList = "id_ruta"),
        @Index(name = "idx_viaje_fecha", columnList = "fecha_viaje"),
        @Index(name = "idx_viaje_estado", columnList = "estado"),
        @Index(name = "idx_viaje_ruta_fecha", columnList = "id_ruta, fecha_viaje")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "idViaje")
public class Viaje extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_viaje")
    private Integer idViaje;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ruta", nullable = false)
    private Ruta ruta;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_embarcacion", nullable = false)
    private Embarcacion embarcacion;

    @NotNull @Future
    @Column(name = "fecha_viaje", nullable = false)
    private LocalDate fechaViaje;

    @NotNull
    @Column(name = "hora_embarque", nullable = false)
    private LocalTime horaEmbarque;

    @NotNull @Min(0)
    @Column(name = "cupos_disponibles", nullable = false)
    private Integer cuposDisponibles;

    @NotNull @Min(0)
    @Column(name = "cupos_ocupados", nullable = false)
    @Builder.Default
    private Integer cuposOcupados = 0;

    @NotNull @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoViaje estado = EstadoViaje.PROGRAMADO;
}
