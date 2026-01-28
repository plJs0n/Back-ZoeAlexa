package com.zoealexa.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.zoealexa.dto.tarifas.TarifaRutaRequestDTO;
import com.zoealexa.dto.tarifas.TarifaRutaResponseDTO;
import com.zoealexa.service.tarifas.TarifaRutaService;


import java.util.List;

@RestController
@RequestMapping("/api/tarifas")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class TarifaRutaController {

    private final TarifaRutaService tarifaService;

    /**
     * Crear nueva tarifa para una ruta
     * - Cierra automáticamente la tarifa vigente anterior
     * - Solo ADMINISTRADOR
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<TarifaRutaResponseDTO> crear(
            @Valid @RequestBody TarifaRutaRequestDTO request) {

        log.info("Creando nueva tarifa para ruta ID: {}", request.getIdRuta());

        TarifaRutaResponseDTO response = tarifaService.crear(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Listar todas las tarifas (histórico completo)
     */
    @GetMapping
    public ResponseEntity<List<TarifaRutaResponseDTO>> listarTodas() {

        log.info("Listando todas las tarifas");

        return ResponseEntity.ok(tarifaService.listarTodas());
    }

    /**
     * Obtener tarifa vigente de una ruta
     */
    @GetMapping("/ruta/{rutaId}")
    public ResponseEntity<TarifaRutaResponseDTO> obtenerPorRuta(
            @PathVariable Integer rutaId) {

        log.info("Obteniendo tarifa vigente para ruta ID: {}", rutaId);

        return ResponseEntity.ok(tarifaService.obtenerPorRuta(rutaId));
    }
}
