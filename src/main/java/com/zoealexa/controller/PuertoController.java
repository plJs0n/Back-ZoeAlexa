package com.zoealexa.controller;

import com.zoealexa.dto.transporte.EmbarcacionResponseDTO;
import com.zoealexa.dto.transporte.UpdatePuertoRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.zoealexa.dto.transporte.PuertoRequestDTO;
import com.zoealexa.dto.transporte.PuertoResponseDTO;
import com.zoealexa.service.transporte.PuertoService;

import java.util.List;

/**
 * Controller de Puertos
 */
@RestController
@RequestMapping("/api/puertos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class PuertoController {

    private final PuertoService puertoService;

    /**
     * Crear nuevo puerto
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<PuertoResponseDTO> crear(@Valid @RequestBody PuertoRequestDTO request) {
        log.info("Creando puerto: {}", request.getNombrePuerto());

        PuertoResponseDTO response = puertoService.crear(request);

        log.info("Puerto creado con ID: {}", response.getIdPuerto());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Listar todos los puertos
     */
    @GetMapping
    public ResponseEntity<List<PuertoResponseDTO>> listarTodos() {
        log.info("Listando todos los puertos");

        List<PuertoResponseDTO> puertos = puertoService.listarTodos();

        return ResponseEntity.ok(puertos);
    }

    /**
     * Actualizar puerto (actualización parcial)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<PuertoResponseDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody UpdatePuertoRequestDTO request) {  // ⬅️ Usar UpdatePuertoRequestDTO

        log.info("Actualizando puerto ID: {}", id);

        PuertoResponseDTO response = puertoService.actualizar(id, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/en-servicio")
    public ResponseEntity<List<PuertoResponseDTO>> listarEnServicio() {
        log.info("Listando Embarcaciones EN_SERVICIO");

        List<PuertoResponseDTO> puertos = puertoService.listarEnServicio();

        return ResponseEntity.ok(puertos);
    }
}