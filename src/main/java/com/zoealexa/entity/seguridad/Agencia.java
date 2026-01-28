package com.zoealexa.entity.seguridad;

import com.zoealexa.entity.auditoria.Auditable;
import com.zoealexa.entity.enums.EstadoAgencia;
import com.zoealexa.entity.enums.TipoComision;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "agencias", indexes = {
        @Index(name = "idx_agencias_ruc", columnList = "ruc"),
        @Index(name = "idx_agencias_estado", columnList = "estado"),
        @Index(name = "idx_agencias_nombre", columnList = "nombre_agencia")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agencia extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_agencia")
    private Integer idAgencia;

    @NotBlank(message = "El nombre de la agencia es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(name = "nombre_agencia", nullable = false, length = 100)
    private String nombreAgencia;

    @NotBlank(message = "El RUC es obligatorio")
    @Size(min = 11, max = 11, message = "El RUC debe tener 11 dígitos")
    @Pattern(regexp = "^\\d{11}$", message = "El RUC debe contener solo dígitos")
    @Column(name = "ruc", nullable = false, unique = true, length = 11)
    private String ruc;

    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    @Column(name = "direccion", length = 200)
    private String direccion;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    @Column(name = "telefono", length = 20)
    private String telefono;

    @NotNull(message = "El tipo de comisión es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comision", nullable = false, length = 20)
    @Builder.Default
    private TipoComision tipoComision = TipoComision.PORCENTAJE;

    @NotNull(message = "El valor de comisión es obligatorio")
    @DecimalMin(value = "0.01", message = "El valor de comisión debe ser mayor a 0")
    @DecimalMax(value = "100.00", message = "El valor de comisión no puede exceder 100")
    @Digits(integer = 8, fraction = 2, message = "El valor debe tener máximo 8 enteros y 2 decimales")
    @Column(name = "valor_comision", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal valorComision = BigDecimal.valueOf(10.00);

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoAgencia estado = EstadoAgencia.ACTIVO;

    // Relaciones
    @OneToMany(mappedBy = "agencia", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Usuario> usuarios = new HashSet<>();
}
