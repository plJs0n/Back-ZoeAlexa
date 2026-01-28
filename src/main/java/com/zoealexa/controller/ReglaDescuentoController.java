package com.zoealexa.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.zoealexa.dto.tarifas.ReglaDescuentoRequestDTO;
import com.zoealexa.dto.tarifas.ReglaDescuentoResponseDTO;
import com.zoealexa.dto.tarifas.UpdateReglaDescuentoRequestDTO;
import com.zoealexa.service.tarifas.ReglaDescuentoService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller de Reglas de Descuento
 *
 * Endpoints:
 * - POST   /api/reglas-descuento - Crear regla
 * - GET    /api/reglas-descuento - Listar todas
 * - GET    /api/reglas-descuento/{id} - Buscar por ID
 * - GET    /api/reglas-descuento/activas - Listar activas
 * - GET    /api/reglas-descuento/edad/{edad} - Buscar por edad
 * - GET    /api/reglas-descuento/mejor-edad/{edad} - Mejor regla para edad
 * - PUT    /api/reglas-descuento/{id} - Actualizar
 * - PATCH  /api/reglas-descuento/{id}/activar - Activar
 * - PATCH  /api/reglas-descuento/{id}/desactivar - Desactivar
 * - GET    /api/reglas-descuento/calcular - Calcular descuento
 */
@RestController
@RequestMapping("/api/reglas-descuento")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReglaDescuentoController {

    private final ReglaDescuentoService reglaDescuentoService;

    /**
     * Crear nueva regla de descuento
     *
     * POST /api/reglas-descuento
     *
     * @param request Datos de la regla
     * @return Regla creada
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ReglaDescuentoResponseDTO> crear(@Valid @RequestBody ReglaDescuentoRequestDTO request) {
        log.info("Creando regla de descuento: {}", request.getDescripcion());

        ReglaDescuentoResponseDTO response = reglaDescuentoService.crear(request);

        log.info("Regla de descuento creada con ID: {}", response.getIdDescuento());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Listar todas las reglas de descuento
     *
     * GET /api/reglas-descuento
     *
     * @return Lista de reglas
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ASESOR_VENTAS')")
    public ResponseEntity<List<ReglaDescuentoResponseDTO>> listarTodas() {
        log.info("Listando todas las reglas de descuento");

        List<ReglaDescuentoResponseDTO> reglas = reglaDescuentoService.listarTodas();

        return ResponseEntity.ok(reglas);
    }

    /**
     * Listar reglas activas
     *
     * GET /api/reglas-descuento/activas
     *
     * @return Lista de reglas activas
     */
    @GetMapping("/activas")
    public ResponseEntity<List<ReglaDescuentoResponseDTO>> listarActivas() {
        log.info("Listando reglas de descuento activas");

        List<ReglaDescuentoResponseDTO> reglas = reglaDescuentoService.listarActivas();

        return ResponseEntity.ok(reglas);
    }

    /**
     * Buscar regla por ID
     *
     * GET /api/reglas-descuento/{id}
     *
     * @param id ID de la regla
     * @return Regla encontrada
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ASESOR_VENTAS')")
    public ResponseEntity<ReglaDescuentoResponseDTO> buscarPorId(@PathVariable Integer id) {
        log.info("Buscando regla de descuento por ID: {}", id);

        ReglaDescuentoResponseDTO regla = reglaDescuentoService.buscarPorId(id);

        return ResponseEntity.ok(regla);
    }

    /**
     * Buscar reglas aplicables para una edad
     *
     * GET /api/reglas-descuento/edad/{edad}
     *
     * @param edad Edad del pasajero
     * @return Lista de reglas aplicables
     */
    @GetMapping("/edad/{edad}")
    public ResponseEntity<List<ReglaDescuentoResponseDTO>> buscarPorEdad(@PathVariable int edad) {
        log.info("Buscando reglas de descuento para edad: {}", edad);

        List<ReglaDescuentoResponseDTO> reglas = reglaDescuentoService.buscarReglasParaEdad(edad);

        return ResponseEntity.ok(reglas);
    }

    /**
     * Buscar la mejor regla (mayor descuento) para una edad
     *
     * GET /api/reglas-descuento/mejor-edad/{edad}
     *
     * @param edad Edad del pasajero
     * @return Mejor regla o 204 No Content si no aplica ninguna
     */
    @GetMapping("/mejor-edad/{edad}")
    public ResponseEntity<ReglaDescuentoResponseDTO> buscarMejorReglaParaEdad(@PathVariable int edad) {
        log.info("Buscando mejor regla de descuento para edad: {}", edad);

        ReglaDescuentoResponseDTO regla = reglaDescuentoService.buscarMejorReglaParaEdad(edad);

        if (regla == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(regla);
    }

    /**
     * Actualizar regla de descuento (actualización parcial)
     *
     * PUT /api/reglas-descuento/{id}
     *
     * @param id ID de la regla
     * @param request Datos actualizados (todos opcionales)
     * @return Regla actualizada
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ReglaDescuentoResponseDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateReglaDescuentoRequestDTO request) {

        log.info("Actualizando regla de descuento ID: {}", id);

        ReglaDescuentoResponseDTO response = reglaDescuentoService.actualizar(id, request);

        return ResponseEntity.ok(response);
    }

    /**
     * Activar regla de descuento
     *
     * PATCH /api/reglas-descuento/{id}/activar
     *
     * @param id ID de la regla
     * @return Regla activada
     */
    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ReglaDescuentoResponseDTO> activar(@PathVariable Integer id) {
        log.info("Activando regla de descuento ID: {}", id);

        ReglaDescuentoResponseDTO response = reglaDescuentoService.activar(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Desactivar regla de descuento
     *
     * PATCH /api/reglas-descuento/{id}/desactivar
     *
     * @param id ID de la regla
     * @return Regla desactivada
     */
    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ReglaDescuentoResponseDTO> desactivar(@PathVariable Integer id) {
        log.info("Desactivando regla de descuento ID: {}", id);

        ReglaDescuentoResponseDTO response = reglaDescuentoService.desactivar(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Calcular descuento para un monto y edad
     *
     * GET /api/reglas-descuento/calcular?monto=100.00&edad=5
     *
     * @param monto Monto base del pasaje
     * @param edad Edad del pasajero
     * @return Monto del descuento calculado
     */
    @GetMapping("/calcular")
    public ResponseEntity<BigDecimal> calcularDescuento(
            @RequestParam BigDecimal monto,
            @RequestParam int edad) {

        log.info("Calculando descuento para monto: {} y edad: {}", monto, edad);

        BigDecimal descuento = reglaDescuentoService.calcularDescuento(monto, edad);

        return ResponseEntity.ok(descuento);
    }

    /**
     * Contar reglas activas
     *
     * GET /api/reglas-descuento/count/activas
     *
     * @return Número de reglas activas
     */
    @GetMapping("/count/activas")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Long> contarActivas() {
        log.info("Contando reglas de descuento activas");

        Long count = reglaDescuentoService.contarActivas();

        return ResponseEntity.ok(count);
    }
}