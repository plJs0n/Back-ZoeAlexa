package com.zoealexa.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.zoealexa.dto.transporte.*;
import com.zoealexa.entity.enums.EstadoViaje;
import com.zoealexa.service.transporte.ViajeService;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller de Viajes
 *
 * Endpoints:
 * - POST   /api/viajes - Crear nuevo viaje
 * - GET    /api/viajes/{id} - Buscar por ID
 * - GET    /api/viajes/buscar - Buscar viajes disponibles
 * - GET    /api/viajes/proximos - Viajes próximos (7 días)
 * - GET    /api/viajes/estado/{estado} - Listar por estado
 */
@RestController
@RequestMapping("/api/viajes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class ViajeController {

    private final ViajeService viajeService;

    /**
     * Crear nuevo viaje
     *
     * POST /api/viajes
     *
     * @param request Datos del viaje
     * @return Viaje creado
     */
    @PostMapping
    public ResponseEntity<ViajeResponseDTO> crear(@Valid @RequestBody ViajeRequestDTO request) {
        log.info("Creando viaje para ruta ID: {} en fecha: {}",
                request.getIdRuta(), request.getFechaViaje());

        ViajeResponseDTO response = viajeService.crear(request);

        log.info("Viaje creado con ID: {}", response.getIdViaje());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Listar todos los viajes
     * Get api/viajes
     */
    @GetMapping
    public ResponseEntity<List<ViajeResponseDTO>> listarTodos(){
        log.info("Listando todos los viajes");

        List<ViajeResponseDTO> viajes = viajeService.listarTodos();

        return ResponseEntity.ok(viajes);
    }

    /**
     * Buscar viaje por ID
     *
     * GET /api/viajes/{id}
     *
     * @param id ID del viaje
     * @return Viaje encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<ViajeResponseDTO> buscarPorId(@PathVariable Integer id) {
        log.info("Buscando viaje por ID: {}", id);

        ViajeResponseDTO response = viajeService.buscarPorId(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Buscar viajes disponibles
     *
     * GET /api/viajes/buscar?idRuta=1&fechaInicio=2024-12-15&fechaFin=2024-12-30
     *
     * @param idRuta ID de la ruta
     * @param fechaInicio Fecha de inicio de búsqueda
     * @param fechaFin Fecha fin de búsqueda
     * @return Lista de viajes disponibles
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<ViajeBusquedaDTO>> buscarDisponibles(
            @RequestParam Integer idRuta,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        log.info("Buscando viajes disponibles - Ruta: {}, Del {} al {}",
                idRuta, fechaInicio, fechaFin);

        List<ViajeBusquedaDTO> viajes = viajeService.buscarDisponibles(
                idRuta,
                fechaInicio,
                fechaFin
        );

        log.info("Viajes encontrados: {}", viajes.size());

        return ResponseEntity.ok(viajes);
    }

    /**
     * Obtener viajes próximos (siguientes 7 días)
     *
     * GET /api/viajes/proximos
     *
     * @return Lista de viajes próximos
     */
    @GetMapping("/proximos")
    public ResponseEntity<List<ViajeBusquedaDTO>> obtenerProximos() {
        log.info("Obteniendo viajes próximos (7 días)");

        List<ViajeBusquedaDTO> viajes = viajeService.obtenerProximos();

        return ResponseEntity.ok(viajes);
    }

    /**
     * Listar viajes por estado
     *
     * GET /api/viajes/estado/{estado}
     *
     * @param estado Estado del viaje (PROGRAMADO, EN_CURSO, COMPLETADO, CANCELADO)
     * @return Lista de viajes
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ViajeResponseDTO>> listarPorEstado(
            @PathVariable EstadoViaje estado) {

        log.info("Listando viajes por estado: {}", estado);

        List<ViajeResponseDTO> viajes = viajeService.listarPorEstado(estado);

        return ResponseEntity.ok(viajes);
    }


    /**
     * Actualiza un viaje existente
     * Permite actualización parcial (solo los campos proporcionados se actualizan)
     *
     * PUT /api/viajes/{id}
     *
     * @param id ID del viaje a actualizar
     * @param updateDTO DTO con los campos a actualizar
     * @return ViajeResponseDTO con la información actualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<ViajeResponseDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ViajeUpdateDTO updateDTO) {

        log.info("Actualizando viaje con ID: {}", id);
        log.debug("Datos de actualización: {}", updateDTO);

        ViajeResponseDTO viajeActualizado = viajeService.actualizar(id, updateDTO);

        return ResponseEntity.ok(viajeActualizado);
    }
}
