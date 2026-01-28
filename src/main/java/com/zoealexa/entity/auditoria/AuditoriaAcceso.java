package com.zoealexa.entity.auditoria;

import com.zoealexa.entity.enums.Accion;
import com.zoealexa.entity.enums.Resultado;
import com.zoealexa.entity.seguridad.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_acceso", indexes = {
        @Index(name = "idx_auditoria_acceso_usuario", columnList = "id_usuario"),
        @Index(name = "idx_auditoria_acceso_accion", columnList = "accion"),
        @Index(name = "idx_auditoria_acceso_fecha", columnList = "fecha_hora"),
        @Index(name = "idx_auditoria_acceso_resultado", columnList = "resultado")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditoriaAcceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log")
    private Long idLog;

    // Puede ser null si el login falló antes de identificar usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "email_intento", length = 100)
    private String emailIntento;

    @NotNull(message = "La acción es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(name = "accion", nullable = false, length = 50)
    private Accion accion;

    @Size(max = 50, message = "La IP no puede exceder 50 caracteres")
    @Column(name = "ip", length = 50)
    private String ip;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @NotNull(message = "El resultado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "resultado", nullable = false, length = 20)
    private Resultado resultado;

    @Column(name = "motivo", columnDefinition = "TEXT")
    private String motivo;

    @NotNull(message = "La fecha y hora son obligatorias")
    @Column(name = "fecha_hora", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime fechaHora = LocalDateTime.now();
}
