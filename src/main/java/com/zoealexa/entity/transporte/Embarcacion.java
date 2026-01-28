package com.zoealexa.entity.transporte;

import com.zoealexa.entity.enums.EstadoEmbarcacion;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "embarcacion")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idEmbarcacion")
public class Embarcacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_embarcacion")
    private Integer idEmbarcacion;

    @NotBlank
    @Size(max = 100)
    @Column(name = "nombre_embarcacion", nullable = false, unique = true, length = 100)
    private String nombreEmbarcacion;

    @NotNull
    @Min(1)
    @Column(name = "capacidad", nullable = false)
    private Integer capacidad;

    @NotNull @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 50)
    @Builder.Default
    private EstadoEmbarcacion estado = EstadoEmbarcacion.EN_SERVICIO;

    @NotNull
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
