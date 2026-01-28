package com.zoealexa.controller;

import com.zoealexa.dto.transporte.RutaResponseDTO;
import com.zoealexa.dto.transporte.UpdateEmbarcacionRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.zoealexa.dto.transporte.EmbarcacionRequestDTO;
import com.zoealexa.dto.transporte.EmbarcacionResponseDTO;
import com.zoealexa.service.transporte.EmbarcacionService;

import java.util.List;

@RestController
@RequestMapping("/api/embarcaciones")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class EmbarcacionController {

    private final EmbarcacionService embarcacionService;

    /**
     * Crear nueva embarcación
     * */
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<EmbarcacionResponseDTO> crear(@Valid @RequestBody EmbarcacionRequestDTO request) {
        log.info("Creando embarcación: {}", request.getNombreEmbarcacion());

        EmbarcacionResponseDTO response = embarcacionService.crear(request);

        log.info("Embarcación creada con ID: {}", response.getIdEmbarcacion());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Listar todas las embarcaciones
     * */
    @GetMapping
    public ResponseEntity<List<EmbarcacionResponseDTO>> listarTodas() {
        log.info("Listando todas las embarcaciones");

        List<EmbarcacionResponseDTO> embarcaciones = embarcacionService.listarTodas();

        return ResponseEntity.ok(embarcaciones);
    }


    /**
     * Actualizar embarcación
     * */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<EmbarcacionResponseDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody EmbarcacionRequestDTO request) {

        log.info("Actualizando embarcación ID: {}", id);

        EmbarcacionResponseDTO response = embarcacionService.actualizar(id, request);

        return ResponseEntity.ok(response);
    }
}
