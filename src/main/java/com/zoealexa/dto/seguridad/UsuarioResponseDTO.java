package com.zoealexa.dto.seguridad;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.zoealexa.entity.enums.EstadoUsuario;
import com.zoealexa.entity.enums.Rol;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para Usuario - ACTUALIZADO CON AGENCIA
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {

    private Integer idUsuario;
    private String nombresUsuario;
    private Rol rol;
    private String email;
    private String telefono;
    private EstadoUsuario estado;
    private LocalDateTime ultimoAcceso;
    private LocalDateTime fechaCreacion;

    // ==========================================
    // DATOS DE AGENCIA (NUEVOS)
    // ==========================================

    /**
     * ID de la agencia (solo si rol = AGENCIA)
     */
    private Integer idAgencia;

    /**
     * Nombre de la agencia (solo si rol = AGENCIA)
     */
    private String nombreAgencia;

    /**
     * RUC de la agencia (solo si rol = AGENCIA)
     */
    private String rucAgencia;
}