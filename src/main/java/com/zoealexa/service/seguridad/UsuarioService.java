package com.zoealexa.service.seguridad;

import com.zoealexa.dto.seguridad.UpdateUsuarioRequestDTO;
import com.zoealexa.dto.seguridad.UsuarioResponseDTO;
import com.zoealexa.entity.seguridad.Usuario;
import com.zoealexa.exception.ConflictException;
import com.zoealexa.exception.NotFoundException;
import com.zoealexa.mapper.seguridad.UsuarioMapper;
import com.zoealexa.repository.seguridad.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Listar todos los usuarios
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        log.info("Listando todos los usuarios");

        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualizar datos de usuario
     */
    public UsuarioResponseDTO actualizar(Integer id, UpdateUsuarioRequestDTO request) {
        log.info("Actualizando usuario ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        if (request.getEmail() != null) {
            if (!usuario.getEmail().equals(request.getEmail())){
                if (usuarioRepository.existsByEmail(request.getEmail())) {
                    log.warn("Email ya registrado: {}", request.getEmail());
                    throw new ConflictException("El email ya está registrado");
                }else {
                    usuario.setEmail(request.getEmail());
                }
            }
        }

        if (request.getNombresUsuario() != null){
            usuario.setNombresUsuario(request.getNombresUsuario());
        }
        if (request.getTelefono() != null){
            usuario.setTelefono(request.getTelefono());
        }
        if (request.getEstado() != null){
            usuario.setEstado(request.getEstado());
        }

        Usuario actualizado = usuarioRepository.save(usuario);

        log.info("Usuario actualizado: {}", actualizado.getEmail());

        return UsuarioMapper.toResponseDTO(actualizado);
    }
}
