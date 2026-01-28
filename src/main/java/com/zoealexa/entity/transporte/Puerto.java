package com.zoealexa.entity.transporte;

import com.zoealexa.entity.enums.EstadoPuerto;
import com.zoealexa.entity.enums.TipoOperacion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "puerto")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Puerto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_puerto")
    private Integer idPuerto;

    @NotBlank
    @Size(max = 100)
    @Column(name = "ciudad", nullable = false, length = 100)
    private String ciudad;

    @NotBlank @Size(max = 150)
    @Column(name = "nombre_puerto", nullable = false, length = 150)
    private String nombrePuerto;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_operacion", nullable = false, length = 20)
    private TipoOperacion tipoOperacion;

    @Column(name = "direccion", columnDefinition = "TEXT")
    private String direccion;

    @NotNull @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    @Builder.Default
    private EstadoPuerto estado = EstadoPuerto.HABILITADO;

    @NotNull
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime fechaRegistro = LocalDateTime.now();
}
