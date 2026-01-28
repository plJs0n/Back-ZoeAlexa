package com.zoealexa.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.zoealexa.dto.tarifas.ReglaPenalidadRequestDTO;
import com.zoealexa.dto.tarifas.ReglaPenalidadResponseDTO;
import com.zoealexa.dto.tarifas.UpdateReglaPenalidadRequestDTO;
import com.zoealexa.entity.enums.TipoPenalidad;
import com.zoealexa.service.tarifas.ReglaPenalidadService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller de Reglas de Penalidad
 *
 * Endpoints:
 * - POST   /api/reglas-penalidad - Crear regla
 * - GET    /api/reglas-penalidad - Listar todas
 * - GET    /api/reglas-penalidad/{id} - Buscar por ID
 * - GET    /api/reglas-penalidad/activas - Listar activas
 * - GET    /api/reglas-penalidad/tipo/{tipo} - Buscar por tipo
 * - GET    /api/reglas-penalidad/equipaje - Regla de equipaje
 * - PUT    /api/reglas-penalidad/{id} - Actualizar
 * - PATCH  /api/reglas-penalidad/{id}/activar - Activar
 * - PATCH  /api/reglas-penalidad/{id}/desactivar - Desactivar
 * - GET    /api/reglas-penalidad/calcular-cancelacion - Calcular penalidad cancelación
 * - GET    /api/reglas-penalidad/calcular-reprogramacion - Calcular penalidad reprogramación
 * - GET    /api/reglas-penalidad/calcular-equipaje - Calcular costo equipaje
 */
@RestController
@RequestMapping("/api/reglas-penalidad")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReglaPenalidadController {

    private final ReglaPenalidadService reglaPenalidadService;

    /**
     * Crear nueva regla de penalidad
     *
     * POST /api/reglas-penalidad
     *
     * @param request Datos de la regla
     * @return Regla creada
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ReglaPenalidadResponseDTO> crear(@Valid @RequestBody ReglaPenalidadRequestDTO request) {
        log.info("Creando regla de penalidad tipo: {}", request.getTipoPenalidad());

        ReglaPenalidadResponseDTO response = reglaPenalidadService.crear(request);

        log.info("Regla de penalidad creada con ID: {}", response.getIdPenalidad());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Listar todas las reglas de penalidad
     *
     * GET /api/reglas-penalidad
     *
     * @return Lista de reglas
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ASESOR_VENTAS')")
    public ResponseEntity<List<ReglaPenalidadResponseDTO>> listarTodas() {
        log.info("Listando todas las reglas de penalidad");

        List<ReglaPenalidadResponseDTO> reglas = reglaPenalidadService.listarTodas();

        return ResponseEntity.ok(reglas);
    }

    /**
     * Listar reglas activas
     *
     * GET /api/reglas-penalidad/activas
     *
     * @return Lista de reglas activas
     */
    @GetMapping("/activas")
    public ResponseEntity<List<ReglaPenalidadResponseDTO>> listarActivas() {
        log.info("Listando reglas de penalidad activas");

        List<ReglaPenalidadResponseDTO> reglas = reglaPenalidadService.listarActivas();

        return ResponseEntity.ok(reglas);
    }

    /**
     * Buscar regla por ID
     *
     * GET /api/reglas-penalidad/{id}
     *
     * @param id ID de la regla
     * @return Regla encontrada
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ASESOR_VENTAS')")
    public ResponseEntity<ReglaPenalidadResponseDTO> buscarPorId(@PathVariable Integer id) {
        log.info("Buscando regla de penalidad por ID: {}", id);

        ReglaPenalidadResponseDTO regla = reglaPenalidadService.buscarPorId(id);

        return ResponseEntity.ok(regla);
    }

    /**
     * Buscar reglas por tipo de penalidad
     *
     * GET /api/reglas-penalidad/tipo/{tipo}
     *
     * @param tipo Tipo de penalidad (CANCELACION, REPROGRAMACION, EQUIPAJE, GENERICA)
     * @return Lista de reglas del tipo
     */
    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ASESOR_VENTAS')")
    public ResponseEntity<List<ReglaPenalidadResponseDTO>> buscarPorTipo(@PathVariable TipoPenalidad tipo) {
        log.info("Buscando reglas de penalidad por tipo: {}", tipo);

        List<ReglaPenalidadResponseDTO> reglas = reglaPenalidadService.buscarPorTipo(tipo);

        return ResponseEntity.ok(reglas);
    }

    /**
     * Buscar regla de equipaje activa
     *
     * GET /api/reglas-penalidad/equipaje
     *
     * @return Regla de equipaje o 204 No Content si no existe
     */
    @GetMapping("/equipaje")
    public ResponseEntity<ReglaPenalidadResponseDTO> buscarReglaEquipaje() {
        log.info("Buscando regla de equipaje activa");

        ReglaPenalidadResponseDTO regla = reglaPenalidadService.buscarReglaEquipaje();

        if (regla == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(regla);
    }

    /**
     * Actualizar regla de penalidad (actualización parcial)
     *
     * PUT /api/reglas-penalidad/{id}
     *
     * @param id ID de la regla
     * @param request Datos actualizados (todos opcionales)
     * @return Regla actualizada
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ReglaPenalidadResponseDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateReglaPenalidadRequestDTO request) {

        log.info("Actualizando regla de penalidad ID: {}", id);

        ReglaPenalidadResponseDTO response = reglaPenalidadService.actualizar(id, request);

        return ResponseEntity.ok(response);
    }

    /**
     * Activar regla de penalidad
     *
     * PATCH /api/reglas-penalidad/{id}/activar
     *
     * @param id ID de la regla
     * @return Regla activada
     */
    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ReglaPenalidadResponseDTO> activar(@PathVariable Integer id) {
        log.info("Activando regla de penalidad ID: {}", id);

        ReglaPenalidadResponseDTO response = reglaPenalidadService.activar(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Desactivar regla de penalidad
     *
     * PATCH /api/reglas-penalidad/{id}/desactivar
     *
     * @param id ID de la regla
     * @return Regla desactivada
     */
    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ReglaPenalidadResponseDTO> desactivar(@PathVariable Integer id) {
        log.info("Desactivando regla de penalidad ID: {}", id);

        ReglaPenalidadResponseDTO response = reglaPenalidadService.desactivar(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Calcular penalidad por cancelación
     *
     * GET /api/reglas-penalidad/calcular-cancelacion?monto=150.00
     *
     * @param monto Monto total de la reserva
     * @return Monto de la penalidad
     */
    @GetMapping("/calcular-cancelacion")
    public ResponseEntity<BigDecimal> calcularPenalidadCancelacion(@RequestParam BigDecimal monto) {
        log.info("Calculando penalidad por cancelación para monto: {}", monto);

        BigDecimal penalidad = reglaPenalidadService.calcularPenalidadCancelacion(monto);

        return ResponseEntity.ok(penalidad);
    }

    /**
     * Calcular penalidad por reprogramación
     *
     * GET /api/reglas-penalidad/calcular-reprogramacion?monto=150.00
     *
     * @param monto Monto total de la reserva
     * @return Monto de la penalidad
     */
    @GetMapping("/calcular-reprogramacion")
    public ResponseEntity<BigDecimal> calcularPenalidadReprogramacion(@RequestParam BigDecimal monto) {
        log.info("Calculando penalidad por reprogramación para monto: {}", monto);

        BigDecimal penalidad = reglaPenalidadService.calcularPenalidadReprogramacion(monto);

        return ResponseEntity.ok(penalidad);
    }

    /**
     * Calcular costo de equipaje excedente
     *
     * GET /api/reglas-penalidad/calcular-equipaje?peso=25.5
     *
     * @param peso Peso del equipaje en kilogramos
     * @return Costo del exceso de equipaje
     */
    @GetMapping("/calcular-equipaje")
    public ResponseEntity<BigDecimal> calcularCostoEquipaje(@RequestParam BigDecimal peso) {
        log.info("Calculando costo de equipaje para peso: {} kg", peso);

        BigDecimal costo = reglaPenalidadService.calcularCostoEquipaje(peso);

        return ResponseEntity.ok(costo);
    }

    /**
     * Contar reglas activas
     *
     * GET /api/reglas-penalidad/count/activas
     *
     * @return Número de reglas activas
     */
    @GetMapping("/count/activas")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Long> contarActivas() {
        log.info("Contando reglas de penalidad activas");

        Long count = reglaPenalidadService.contarActivas();

        return ResponseEntity.ok(count);
    }
}