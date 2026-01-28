package com.zoealexa.controller;


import com.zoealexa.dto.reserva.CancelarReservaRequest;
import com.zoealexa.dto.reserva.CrearReservaRequest;
import com.zoealexa.dto.reserva.EquipajeRequest;
import com.zoealexa.dto.reserva.RegistrarPagoRequest;
import com.zoealexa.dto.reserva.response.ApiResponse;
import com.zoealexa.dto.reserva.response.CancelacionReprogramacionResponse;
import com.zoealexa.dto.reserva.response.ReservaResponse;
import com.zoealexa.dto.reserva.response.ReservaSimpleResponse;
import com.zoealexa.security.JwtService;
import com.zoealexa.service.reserva.ReservaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * Controller REST para gestión de reservas
 */
@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReservaController {

    private final ReservaService reservaService;
    private final JwtService jwtService;

    /**
     * Crear nueva reserva
     * POST /api/reservas
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ASESOR_VENTAS', 'AGENCIA')")
    public ResponseEntity<ApiResponse<ReservaResponse>> crearReserva(
            @Valid @RequestBody CrearReservaRequest request,
            Authentication authentication) {

        log.info("Solicitud para crear reserva - Viaje ID: {}", request.getIdViaje());

        Integer idUsuario = obtenerIdUsuario(authentication);
        ReservaResponse reserva = reservaService.crearReserva(request, idUsuario);

        ApiResponse<ReservaResponse> response = ApiResponse.success(
                "Reserva creada exitosamente",
                reserva
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Buscar reserva por código
     * GET /api/reservas/{codigo}
     */
    @GetMapping("/{codigo}")
    @PreAuthorize("hasAnyRole('ASESOR_VENTAS', 'AGENCIA')")
    public ResponseEntity<ApiResponse<ReservaResponse>> buscarPorCodigo(
            @PathVariable String codigo) {

        log.info("Buscando reserva: {}", codigo);

        ReservaResponse reserva = reservaService.buscarPorCodigo(codigo);

        ApiResponse<ReservaResponse> response = ApiResponse.success(
                "Reserva encontrada",
                reserva
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Listar todas las reservas
     * GET /api/reservas
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ASESOR_VENTAS', 'AGENCIA')")
    public ResponseEntity<ApiResponse<List<ReservaSimpleResponse>>> listarReservas() {

        log.info("Listando todas las reservas");

        List<ReservaSimpleResponse> reservas = reservaService.listarReservas();

        ApiResponse<List<ReservaSimpleResponse>> response = ApiResponse.success(
                String.format("Se encontraron %d reservas", reservas.size()),
                reservas
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Registrar pago adicional
     * POST /api/reservas/pago
     */
    @PostMapping("/pago")
    @PreAuthorize("hasAnyRole('ASESOR_VENTAS', 'AGENCIA')")
    public ResponseEntity<ApiResponse<ReservaResponse>> registrarPago(
            @Valid @RequestBody RegistrarPagoRequest request,
            Authentication authentication) {

        log.info("Registrando pago para reserva: {}", request.getCodigoReserva());

        Integer idUsuario = obtenerIdUsuario(authentication);
        ReservaResponse reserva = reservaService.registrarPago(request, idUsuario);

        ApiResponse<ReservaResponse> response = ApiResponse.success(
                "Pago registrado exitosamente",
                reserva
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Cancelar reserva
     * POST /api/reservas/cancelar
     */
    @PostMapping("/cancelar")
    @PreAuthorize("hasAnyRole('ASESOR_VENTAS', 'AGENCIA')")
    public ResponseEntity<ApiResponse<CancelacionReprogramacionResponse>> cancelarReserva(
            @Valid @RequestBody CancelarReservaRequest request,
            Authentication authentication) {

        log.info("Cancelando reserva: {}", request.getCodigoReserva());

        Integer idUsuario = obtenerIdUsuario(authentication);
        CancelacionReprogramacionResponse cancelacion =
                reservaService.cancelarReserva(request, idUsuario);

        ApiResponse<CancelacionReprogramacionResponse> response = ApiResponse.success(
                "Reserva cancelada exitosamente",
                cancelacion
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Registrar equipaje para un pasajero
     * POST /api/reservas/{codigo}/equipaje/{idPasajero}
     */
    @PostMapping("/{codigo}/equipaje/{idPasajero}")
    @PreAuthorize("hasAnyRole('ASESOR_VENTAS', 'AGENCIA')")
    public ResponseEntity<ApiResponse<ReservaResponse>> registrarEquipaje(
            @PathVariable String codigo,
            @PathVariable Long idPasajero,
            @Valid @RequestBody EquipajeRequest request,
            Authentication authentication) {

        log.info("Registrando equipaje para reserva: {}, pasajero: {}", codigo, idPasajero);

        Integer idUsuario = obtenerIdUsuario(authentication);
        ReservaResponse reserva = reservaService.registrarEquipaje(codigo, idPasajero, request, idUsuario);

        ApiResponse<ReservaResponse> response = ApiResponse.success(
                "Equipaje registrado exitosamente",
                reserva
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene el ID del usuario autenticado desde el token JWT
     *
     * OPCIÓN 1: Extrae el userId directamente del token (RECOMENDADO)
     */
    private Integer obtenerIdUsuario(Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        // Obtener el token JWT del request
        String token = obtenerTokenDelRequest();

        if (token == null) {
            throw new RuntimeException("Token JWT no encontrado");
        }

        // Extraer userId del token usando JwtService
        Integer userId = jwtService.extractUserId(token);

        if (userId == null) {
            log.error("Token no contiene userId para usuario: {}", authentication.getName());
            throw new RuntimeException("Token inválido: no contiene ID de usuario");
        }

        log.debug("Usuario autenticado - ID: {}, Email: {}", userId, authentication.getName());
        return userId;
    }

    /**
     * Obtiene el token JWT del HttpServletRequest actual
     */
    private String obtenerTokenDelRequest() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
            return null;
        } catch (Exception e) {
            log.error("Error al obtener token del request", e);
            return null;
        }
    }
}