package com.zoealexa.entity.tarifas;

import com.zoealexa.entity.enums.TipoPenalidad;
import com.zoealexa.entity.enums.TipoValor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "regla_penalidad")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "idPenalidad")
public class ReglaPenalidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_penalidad")
    private Integer idPenalidad;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_penalidad", nullable = false, length = 30)
    private TipoPenalidad tipoPenalidad;

    @NotBlank
    @Column(name = "descripcion", nullable = false, length = 200)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_valor", length = 20)
    private TipoValor tipoValor; // Null para EQUIPAJE

    @Column(name = "valor", precision = 10, scale = 2)
    private BigDecimal valor; // Null para EQUIPAJE

    // Solo para EQUIPAJE
    @Column(name = "kilos_permitidos")
    private Integer kilosPermitidos;

    @Column(name = "precio_por_kilo", precision = 10, scale = 2)
    private BigDecimal precioPorKilo;

    @Column(name = "activa", nullable = false)
    @Builder.Default
    private Boolean activa = true;

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

}
