package com.zoealexa.controller;

import com.zoealexa.dto.seguridad.UpdateUsuarioRequestDTO;
import com.zoealexa.dto.seguridad.UsuarioResponseDTO;
import com.zoealexa.service.seguridad.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        log.info("Listando todos los usuarios");

        List<UsuarioResponseDTO> usuarios = usuarioService.listarTodos();

        return ResponseEntity.ok(usuarios);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateUsuarioRequestDTO request) {

        log.info("Actualizando usuario ID: {}", id);

        UsuarioResponseDTO usuario = usuarioService.actualizar(id, request);

        log.info("Usuario actualizado: {}", usuario.getEmail());

        return ResponseEntity.ok(usuario);
    }
}
