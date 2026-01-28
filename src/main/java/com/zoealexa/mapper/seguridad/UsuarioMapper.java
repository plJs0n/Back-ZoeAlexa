package com.zoealexa.mapper.seguridad;

import com.zoealexa.dto.seguridad.RegisterRequestDTO;
import com.zoealexa.dto.seguridad.UsuarioResponseDTO;
import com.zoealexa.entity.seguridad.Agencia;
import com.zoealexa.entity.enums.EstadoUsuario;
import com.zoealexa.entity.seguridad.Usuario;

/**
 * Mapper para conversión entre Usuario y DTOs - ACTUALIZADO CON AGENCIA
 */
public class UsuarioMapper {

    /**
     * Convierte RegisterRequestDTO a Entity
     * */
    public static Usuario toEntity(RegisterRequestDTO dto, String passwordEncriptado) {
        if (dto == null) return null;

        return Usuario.builder()
                .nombresUsuario(dto.getNombresUsuario())
                .rol(dto.getRol())
                .email(dto.getEmail())
                .password(passwordEncriptado)
                .telefono(dto.getTelefono())
                .estado(EstadoUsuario.ACTIVO)
                .intentosFallidos(0)
                // La agencia se asigna después en el service
                .build();
    }

    /**
     * Convierte Entity a UsuarioResponseDTO
     * Incluye datos de la agencia si aplica
     */
    public static UsuarioResponseDTO toResponseDTO(Usuario entity) {
        if (entity == null) return null;

        UsuarioResponseDTO.UsuarioResponseDTOBuilder builder = UsuarioResponseDTO.builder()
                .idUsuario(entity.getIdUsuario())
                .nombresUsuario(entity.getNombresUsuario())
                .rol(entity.getRol())
                .email(entity.getEmail())
                .telefono(entity.getTelefono())
                .estado(entity.getEstado())
                .ultimoAcceso(entity.getUltimoAcceso())
                .fechaCreacion(entity.getFechaCreacion());

        // Agregar datos de agencia si aplica
        if (entity.getAgencia() != null) {
            Agencia agencia = entity.getAgencia();
            builder.idAgencia(agencia.getIdAgencia())
                    .nombreAgencia(agencia.getNombreAgencia())
                    .rucAgencia(agencia.getRuc());
        }

        return builder.build();
    }
}