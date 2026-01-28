package com.zoealexa.entity.seguridad;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Entity
@Table(name = "token_recuperacion", indexes = {
        @Index(name = "idx_token_codigo", columnList = "codigo"),
        @Index(name = "idx_token_usuario", columnList = "id_usuario")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRecuperacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_token")
    private Integer idToken;

    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false, foreignKey = @ForeignKey(name = "fk_token_usuario"))
    private Usuario usuario;

    @NotBlank(message = "El código es obligatorio")
    @Column(name = "codigo", nullable = false, length = 6)
    private String codigo;

    @NotNull(message = "El estado de utilización es obligatorio")
    @Column(name = "utilizado", nullable = false)
    @Builder.Default
    private Boolean utilizado = false;

    @NotNull(message = "La fecha de creación es obligatoria")
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @NotNull(message = "La fecha de expiración es obligatoria")
    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    // ==========================================
    // MÉTODOS DE NEGOCIO
    // ==========================================

    /**
     * Genera un código aleatorio de 6 dígitos
     *
     * @return Código de 6 dígitos como String
     */
    public static String generarCodigo() {
        SecureRandom random = new SecureRandom();
        int codigo = 100000 + random.nextInt(900000); // Entre 100000 y 999999
        return String.valueOf(codigo);
    }

    /**
     * Crea un nuevo token de recuperación para un usuario
     *
     * @param usuario Usuario para el que se crea el token
     * @param vigenciaMinutos Minutos de vigencia del código (default 15)
     * @return Token de recuperación creado
     */
    public static TokenRecuperacion crear(Usuario usuario, int vigenciaMinutos) {
        return TokenRecuperacion.builder()
                .usuario(usuario)
                .codigo(generarCodigo())
                .utilizado(false)
                .fechaCreacion(LocalDateTime.now())
                .fechaExpiracion(LocalDateTime.now().plusMinutes(vigenciaMinutos))
                .build();
    }

    /**
     * Crea un token con vigencia por defecto de 15 minutos
     */
    public static TokenRecuperacion crear(Usuario usuario) {
        return crear(usuario, 15);
    }

    /**
     * Verifica si el token está vigente
     *
     * @return true si está vigente (no usado y no expirado)
     */
    public boolean estaVigente() {
        return !utilizado && LocalDateTime.now().isBefore(fechaExpiracion);
    }

    /**
     * Verifica si el token ha expirado
     */
    public boolean haExpirado() {
        return LocalDateTime.now().isAfter(fechaExpiracion);
    }

    /**
     * Marca el token como utilizado
     */
    public void marcarComoUtilizado() {
        this.utilizado = true;
    }

    /**
     * Obtiene los minutos restantes de vigencia
     */
    public long getMinutosRestantes() {
        if (haExpirado()) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), fechaExpiracion).toMinutes();
    }

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (utilizado == null) {
            utilizado = false;
        }
    }
}