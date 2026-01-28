package com.zoealexa.entity.tarifas;

import com.zoealexa.entity.enums.TipoValor;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "regla_descuento")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idDescuento")
public class ReglaDescuento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_descuento")
    private Integer idDescuento;

    @NotBlank
    @Size(max = 200)
    @Column(name = "descripcion", nullable = false, length = 200)
    private String descripcion;

    @Min(0)
    @Column(name = "edad_minima")
    private Integer edadMinima;

    @Min(0)
    @Column(name = "edad_maxima")
    private Integer edadMaxima;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_valor", nullable = false, length = 20)
    private TipoValor tipoValor;

    @NotNull @DecimalMin("0.00")
    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @NotNull
    @Column(name = "activa", nullable = false)
    @Builder.Default
    private Boolean activa = true;

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}
