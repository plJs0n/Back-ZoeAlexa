package com.zoealexa.controller;

import com.zoealexa.dto.agencia.ActualizarAgenciaRequest;
import com.zoealexa.dto.agencia.CambiarEstadoAgenciaRequest;
import com.zoealexa.dto.agencia.CrearAgenciaRequest;
import com.zoealexa.dto.agencia.AgenciaResponse;
import com.zoealexa.dto.agencia.AgenciaSimpleResponse;
import com.zoealexa.dto.reserva.response.ApiResponse;
import com.zoealexa.entity.enums.EstadoAgencia;
import com.zoealexa.service.agencia.AgenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestión de Agencias
 *
 * PERMISOS: Solo ADMINISTRADOR puede gestionar agencias
 */
@RestController
@RequestMapping("/api/agencias")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class AgenciaController {

    private final AgenciaService agenciaService;

    /**
     * Crear nueva agencia
     * POST /api/agencias
     *
     * PERMISOS: Solo ADMINISTRADOR
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<AgenciaResponse>> crearAgencia(
            @Valid @RequestBody CrearAgenciaRequest request) {

        log.info("Solicitud para crear agencia: {}", request.getNombreAgencia());

        AgenciaResponse agencia = agenciaService.crearAgencia(request);

        ApiResponse<AgenciaResponse> response = ApiResponse.success(
                "Agencia creada exitosamente",
                agencia
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualizar agencia existente
     * PUT /api/agencias/{id}
     *
     * PERMISOS: Solo ADMINISTRADOR
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<AgenciaResponse>> actualizarAgencia(
            @PathVariable("id") Integer idAgencia,
            @Valid @RequestBody ActualizarAgenciaRequest request) {

        log.info("Solicitud para actualizar agencia ID: {}", idAgencia);

        AgenciaResponse agencia = agenciaService.actualizarAgencia(idAgencia, request);

        ApiResponse<AgenciaResponse> response = ApiResponse.success(
                "Agencia actualizada exitosamente",
                agencia
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Cambiar estado de agencia (Activar/Desactivar)
     * PATCH /api/agencias/{id}/estado
     *
     * PERMISOS: Solo ADMINISTRADOR
     */
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<AgenciaResponse>> cambiarEstado(
            @PathVariable("id") Integer idAgencia,
            @Valid @RequestBody CambiarEstadoAgenciaRequest request) {

        log.info("Solicitud para cambiar estado de agencia ID: {} a {}", idAgencia, request.getEstado());

        AgenciaResponse agencia = agenciaService.cambiarEstado(idAgencia, request);

        String mensaje = request.getEstado() == EstadoAgencia.ACTIVO
                ? "Agencia activada exitosamente"
                : "Agencia desactivada exitosamente";

        ApiResponse<AgenciaResponse> response = ApiResponse.success(mensaje, agencia);

        return ResponseEntity.ok(response);
    }

    /**
     * Listar todas las agencias
     * GET /api/agencias
     *
     * PERMISOS: Cualquier usuario autenticado puede listar
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<AgenciaSimpleResponse>>> listarTodas(
            @RequestParam(required = false) EstadoAgencia estado) {

        log.debug("Listando agencias. Estado: {}", estado);

        List<AgenciaSimpleResponse> agencias;

        if (estado != null) {
            agencias = agenciaService.listarPorEstado(estado);
        } else {
            agencias = agenciaService.listarTodas();
        }

        ApiResponse<List<AgenciaSimpleResponse>> response = ApiResponse.success(
                String.format("Se encontraron %d agencias", agencias.size()),
                agencias
        );

        return ResponseEntity.ok(response);
    }
}