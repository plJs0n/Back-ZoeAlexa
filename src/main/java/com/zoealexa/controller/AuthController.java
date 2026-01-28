package com.zoealexa.controller;

import com.zoealexa.dto.seguridad.*;
import com.zoealexa.security.JwtService;
import com.zoealexa.service.seguridad.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    /**
     * Login de usuario
     * */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request,
            HttpServletRequest httpRequest){

        log.info("Login request para email: {}", request.getEmail());

        // Optener IP y User Agent del Cliente
        String ip = getClientIP(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        //Autenticar
        LoginResponseDTO response = authService.login(request, ip, userAgent);
        log.info("Login exitoso para: {}", request.getEmail());

        return ResponseEntity.ok(response);
    }

    /**
     * Registro de nuevo usuario
     * */
    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO request) {

        log.info("Registro de nuevo usuario: {}", request.getEmail());

        UsuarioResponseDTO response = authService.registrar(request);

        log.info("Usuario registrado exitosamente: {}", response.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    /**
     * Solicitar código de recuperación de contraseña
     * Genera código de 6 dígitos y lo envía por email
     *
     * POST /api/auth/recuperar-password
     */
    @PostMapping("/obtener-codigo")
    public ResponseEntity<String> solicitarCodigoRecuperacion(
            @Valid @RequestBody SolicitarCodigoRequestDTO request) {

        log.info("Solicitud de código de recuperación para: {}", request.getEmail());

        authService.solicitarCodigoRecuperacion(request);

        return ResponseEntity.ok("Código de recuperación enviado a tu email. Revisa tu bandeja de entrada.");
    }

    /**
     * Verificar código y resetear contraseña
     *
     * POST /api/auth/verificar-codigo-reset
     */
    @PostMapping("/recuperar-password")
    public ResponseEntity<String> verificarCodigoYResetearPassword(
            @Valid @RequestBody VerificarCodigoResetPasswordDTO request) {

        log.info("Verificando código y reseteando contraseña para: {}", request.getEmail());

        authService.verificarCodigoYResetearPassword(request);

        return ResponseEntity.ok("Contraseña actualizada exitosamente. Ya puedes iniciar sesión.");
    }

    /**
     * Cambiar contraseña (usuario autenticado)
     *
     * POST /api/auth/cambiar-password
     */
    @PostMapping("/cambiar-password")
    public ResponseEntity<String> cambiarPassword(
            @Valid @RequestBody CambioPasswordRequestDTO request,
            Authentication authentication) {

        Integer usuarioId = Integer.parseInt(authentication.getName());

        log.info("Cambio de contraseña para usuario ID: {}", usuarioId);

        authService.cambiarPassword(usuarioId, request);

        return ResponseEntity.ok("Contraseña cambiada exitosamente");
    }

    /**
     * Logout
     *
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            Authentication authentication,
            HttpServletRequest httpRequest) {

        Integer usuarioId = obtenerIdUsuario(authentication);
        String ip = getClientIP(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        authService.logout(usuarioId, ip, userAgent);

        return ResponseEntity.ok("Logout exitoso");
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================

    /**
     * Obtener IP real del cliente (considerando proxies)
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }


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
