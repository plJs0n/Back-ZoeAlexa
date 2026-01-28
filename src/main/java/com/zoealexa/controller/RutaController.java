package com.zoealexa.controller;

import com.zoealexa.dto.transporte.UpdateRutaRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.zoealexa.dto.transporte.RutaRequestDTO;
import com.zoealexa.dto.transporte.RutaResponseDTO;
import com.zoealexa.service.transporte.RutaService;

import java.util.List;

/**
 * Controller de Rutas
 *
 * Endpoints:
 * - POST   /api/rutas - Crear nueva ruta
 * - GET    /api/rutas - Listar todas
 * - GET    /api/rutas/{id} - Buscar por ID
 * - GET    /api/rutas/activas - Listar rutas activas
 * - PUT    /api/rutas/{id} - Actualizar ruta
 * - PATCH  /api/rutas/{id}/estado - Cambiar estado
 */
@RestController
@RequestMapping("/api/rutas")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class RutaController {

    private final RutaService rutaService;

    /**
     * Crear nueva ruta
     *
     * POST /api/rutas
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RutaResponseDTO> crear(@Valid @RequestBody RutaRequestDTO request) {
        log.info("Creando ruta: Puerto {} → Puerto {}",
                request.getIdPuertoOrigen(), request.getIdPuertoDestino());

        RutaResponseDTO response = rutaService.crear(request);

        log.info("Ruta creada con ID: {}", response.getIdRuta());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Listar todas las rutas
     *
     * GET /api/rutas
     */
    @GetMapping
    public ResponseEntity<List<RutaResponseDTO>> listarTodas() {
        log.info("Listando todas las rutas");

        List<RutaResponseDTO> rutas = rutaService.listarTodas();

        return ResponseEntity.ok(rutas);
    }

    /**
     * Buscar ruta por ID
     *
     * GET /api/rutas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<RutaResponseDTO> buscarPorId(@PathVariable Integer id) {
        log.info("Buscando ruta por ID: {}", id);

        RutaResponseDTO ruta = rutaService.buscarPorId(id);

        return ResponseEntity.ok(ruta);
    }

    /**
     * Listar rutas activas
     *
     * GET /api/rutas/activas
     */
    @GetMapping("/activas")
    public ResponseEntity<List<RutaResponseDTO>> listarActivas() {
        log.info("Listando rutas activas");

        List<RutaResponseDTO> rutas = rutaService.listarActivas();

        return ResponseEntity.ok(rutas);
    }

    /**
     * Actualizar ruta (actualización parcial - flexible)
     *
     * PUT /api/rutas/{id}
     *
     * @param id ID de la ruta
     * @param request Datos a actualizar (todos opcionales)
     * @return Ruta actualizada
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RutaResponseDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateRutaRequestDTO request) {  // ⬅️ CAMBIO AQUÍ

        log.info("Actualizando ruta ID: {}", id);

        RutaResponseDTO response = rutaService.actualizar(id, request);

        return ResponseEntity.ok(response);
    }
}
