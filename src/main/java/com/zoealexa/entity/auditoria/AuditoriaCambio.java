package com.zoealexa.entity.auditoria;

import com.zoealexa.entity.enums.Operacion;
import com.zoealexa.entity.seguridad.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_cambio", indexes = {
        @Index(name = "idx_auditoria_tabla", columnList = "tabla_afectada"),
        @Index(name = "idx_auditoria_operacion", columnList = "operacion"),
        @Index(name = "idx_auditoria_usuario", columnList = "id_usuario"),
        @Index(name = "idx_auditoria_fecha", columnList = "fecha_operacion"),
        @Index(name = "idx_auditoria_registro", columnList = "id_registro")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaCambio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Long idAuditoria;

    @NotBlank
    @Column(name = "tabla_afectada", nullable = false, length = 50)
    private String tablaAfectada;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "operacion", nullable = false, length = 20)
    private Operacion operacion;

    @NotNull
    @Column(name = "id_registro", nullable = false)
    private Integer idRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", foreignKey = @ForeignKey(name = "fk_auditoria_usuario"))
    private Usuario usuario;

    @Column(name = "fecha_operacion", nullable = false)
    @Builder.Default
    private LocalDateTime fechaOperacion = LocalDateTime.now();

    @Column(name = "ip_origen", length = 50)
    private String ipOrigen;

    @Column(name = "valores_anteriores", columnDefinition = "TEXT")
    private String valoresAnteriores; // JSON

    @Column(name = "valores_nuevos", columnDefinition = "TEXT")
    private String valoresNuevos;
}
