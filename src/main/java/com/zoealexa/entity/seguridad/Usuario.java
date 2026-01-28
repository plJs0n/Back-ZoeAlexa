package com.zoealexa.entity.seguridad;

import com.zoealexa.entity.enums.EstadoUsuario;
import com.zoealexa.entity.enums.Rol;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "usuario", indexes = {
        @Index(name = "idx_usuario_email", columnList = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "idUsuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    // Relación con Agencia (solo para rol AGENCIA)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agencia", foreignKey = @ForeignKey(name = "fk_usuario_agencia"))
    private Agencia agencia;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(name = "nombres_usuario", nullable = false, length = 100)
    private String nombresUsuario;

    @NotNull(message = "El rol es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 20)
    private Rol rol;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(max = 255, message = "El hash de contraseña no puede exceder 255 caracteres")
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\d{9}$", message = "El teléfono debe tener 9 dígitos")
    @Column(name = "telefono", nullable = false, length = 9)
    private String telefono;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;

    @Min(value = 0, message = "Los intentos fallidos no pueden ser negativos")
    @Max(value = 10, message = "Los intentos fallidos no pueden exceder 10")
    @Column(name = "intentos_fallidos", nullable = false)
    @Builder.Default
    private Integer intentosFallidos = 0;

    @Column(name = "bloqueado_hasta")
    private LocalDateTime bloqueadoHasta;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    // Auditoría automática
    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<TokenRecuperacion> tokensRecuperacion = new HashSet<>();

    /**
     * Verifica si el usuario está bloqueado temporalmente
     * @return true si está bloqueado, false en caso contrario
     */
    public boolean estaBloqueado() {
        if (bloqueadoHasta == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(bloqueadoHasta);
    }

    /**
     * Incrementa el contador de intentos fallidos de login
     * Si alcanza 5 intentos, bloquea al usuario por 30 segundos
     */
    public void incrementarIntentosFallidos() {
        this.intentosFallidos++;

        // Bloquear temporalmente después de 5 intentos fallidos
        if (this.intentosFallidos >= 5) {
            this.bloqueadoHasta = LocalDateTime.now().plusSeconds(30);
            this.estado = EstadoUsuario.BLOQUEADO;
        }
    }

    /**
     * Bloquea al usuario por un período de tiempo específico
     * @param minutos Cantidad de minutos de bloqueo
     */
    public void bloquearTemporalmente(int minutos) {
        this.bloqueadoHasta = LocalDateTime.now().plusMinutes(minutos);
        this.estado = EstadoUsuario.BLOQUEADO;
    }

    /**
     * Reinicia el contador de intentos fallidos
     * Se llama después de un login exitoso
     */
    public void reiniciarIntentos() {
        this.intentosFallidos = 0;
        this.bloqueadoHasta = null;
        if (this.estado == EstadoUsuario.BLOQUEADO) {
            this.estado = EstadoUsuario.ACTIVO;
        }
    }

    /**
     * Registra el último acceso del usuario
     * Se llama después de un login exitoso
     */
    public void registrarAcceso() {
        this.ultimoAcceso = LocalDateTime.now();
    }
}
