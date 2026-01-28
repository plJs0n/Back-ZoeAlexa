package com.zoealexa.service.seguridad;

import com.zoealexa.dto.seguridad.*;
import com.zoealexa.entity.enums.*;
import com.zoealexa.entity.seguridad.Agencia;
import com.zoealexa.entity.seguridad.TokenRecuperacion;
import com.zoealexa.entity.seguridad.Usuario;
import com.zoealexa.exception.*;
import com.zoealexa.mapper.seguridad.UsuarioMapper;
import com.zoealexa.repository.seguridad.AgenciaRepository;
import com.zoealexa.repository.seguridad.TokenRecuperacionRepository;
import com.zoealexa.repository.seguridad.UsuarioRepository;
import com.zoealexa.security.JwtService;
import com.zoealexa.security.UserDetailsServiceImpl;
import com.zoealexa.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuditoriaService auditoriaService;

    private final UsuarioRepository usuarioRepository;
    private final AgenciaRepository agenciaRepository;
    private final TokenRecuperacionRepository tokenRepository;
    private final EmailService emailService;  // ⬅️ NUEVO
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final LoginAttemptService loginAttemptService;

    @Value("${app.mail.recuperacion.codigo-vigencia-minutos:15}")
    private int vigenciaMinutosCodigo;

    public LoginResponseDTO login(LoginRequestDTO request, String ip, String userAgent){
        log.info("Intento de login para email: {}", request.getEmail());

        //1. Buscar usuario
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).
                orElseThrow(() -> {
                    log.warn("Usuario no encontrado: {}", request.getEmail());
                    auditoriaService.resgisterLoginWithoutUser(request.getEmail(), ip, userAgent, "Usuario no encontrado");
                    return new BadCredentialsException("Correo Invalido");
                });

        //2. Verificar si puede intentar login por tiempoBloqueo
        if(usuario.getEstado() == EstadoUsuario.BLOQUEADO && usuario.getBloqueadoHasta() != null){

            if (LocalDateTime.now().isAfter(usuario.getBloqueadoHasta())){
                usuario.reiniciarIntentos();
                usuarioRepository.save(usuario);
            }else {
                log.warn("Usuario bloqueado: {}", request.getEmail());
                auditoriaService.resgisterLoginWithUser(usuario,request.getEmail(), Accion.LOGIN_FALLIDO,ip, userAgent, "Usuario bloqueado", Resultado.FALLIDO);
                throw new AccountLockedException("Usuario bloqueado temporalmente. Intente nuevamente en unos momentos.");
            }
        }

        //3. Verificar estado del usuario
        if(usuario.getEstado() != EstadoUsuario.ACTIVO){
            log.warn("Usuario inactivo: {}", request.getEmail());
            auditoriaService.resgisterLoginWithUser(usuario, request.getEmail(), Accion.LOGIN_FALLIDO, ip, userAgent, "Usuario Inactivo", Resultado.FALLIDO);
            throw new AccountLockedException("Usuario inactivo. Contacte al administrador.");
        }

        try{
            //4. Autenticar con spring security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            //5. Si llegamos aquí, la autenticación fue exitosa
            usuario.reiniciarIntentos();
            usuario.registrarAcceso();
            usuarioRepository.save(usuario);

            //6. Obtener UserDetails desde el contexto de seguridad
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            //7. Generar token JWT INCLUYENDO ID DEL USUARIO (OPCIÓN 1)
            String token = jwtService.generateTokenWithUserId(
                    usuario.getIdUsuario(),
                    usuario.getRol().name(),
                    userDetails
            );

            log.info("Login exitoso para: {}", request.getEmail());

            //8. Registrar auditoria exitosa
            auditoriaService.resgisterLoginWithUser(usuario, request.getEmail(), Accion.LOGIN_OK, ip, userAgent, "Login Exitoso", Resultado.EXITOSO);

            //9. Construir respuesta
            return LoginResponseDTO.builder()
                    .token(token)
                    .tipo("Bearer")
                    .usuario(UsuarioMapper.toResponseDTO(usuario))
                    .build();
        } catch (org.springframework.security.authentication.BadCredentialsException e){

            //10. Contraseña incorrecta - incrementar intentos fallidos
            loginAttemptService.registrarIncorrectPassword(usuario, request.getEmail(), ip, userAgent);
            throw new BadCredentialsException("Credenciales inválidas");
        }
    }

    /**
     * Registrar nuevo usuario - ACTUALIZADO CON AGENCIA
     */
    public UsuarioResponseDTO registrar(RegisterRequestDTO request) {
        log.info("Registrando nuevo usuario: {}", request.getEmail());

        // 1. Verificar que el email no exista
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            log.warn("Email ya registrado: {}", request.getEmail());
            throw new ConflictException("El email ya está registrado");
        }

        // 2. VALIDAR AGENCIA SEGÚN ROL
        Agencia agencia = null;
        if (request.getRol() == Rol.AGENCIA) {
            // Si es AGENCIA, el idAgencia es OBLIGATORIO
            if (request.getIdAgencia() == null) {
                throw new BadRequestException("Para usuarios de agencia, el ID de agencia es obligatorio");
            }

            // Buscar y validar agencia
            agencia = agenciaRepository.findById(request.getIdAgencia())
                    .orElseThrow(() -> new NotFoundException("Agencia no encontrada con ID: " + request.getIdAgencia()));

            // Verificar que la agencia esté activa
            if (agencia.getEstado() != EstadoAgencia.ACTIVO) {
                throw new ConflictException("La agencia no está activa");
            }

        } else {
            // Si NO es AGENCIA, el idAgencia debe ser NULL
            if (request.getIdAgencia() != null) {
                throw new BadRequestException("Solo usuarios con rol AGENCIA pueden tener una agencia asignada");
            }
        }

        // 3. Crear usuario
        String passwordEncriptado = passwordEncoder.encode(request.getPassword());
        Usuario usuario = UsuarioMapper.toEntity(request, passwordEncriptado);

        // 4. Asignar agencia si aplica
        if (agencia != null) {
            usuario.setAgencia(agencia);
        }

        // 5. Guardar
        usuario = usuarioRepository.save(usuario);

        log.info("Usuario registrado exitosamente: {}", usuario.getEmail());

        return UsuarioMapper.toResponseDTO(usuario);
    }

    /**
     * Solicitar código de recuperación de contraseña
     * Genera código de 6 dígitos y lo envía por email
     */
    public void solicitarCodigoRecuperacion(SolicitarCodigoRequestDTO request) {
        log.info("Solicitando código de recuperación para: {}", request.getEmail());

        // 1. Buscar usuario
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con el email proporcionado"));

        // 2. Verificar que el usuario esté activo
        if (usuario.getEstado() == EstadoUsuario.BLOQUEADO && usuario.getBloqueadoHasta() != null) {
            if (LocalDateTime.now().isAfter(usuario.getBloqueadoHasta())){
                usuario.reiniciarIntentos();
                usuarioRepository.save(usuario);
            }else {
                throw new ConflictException("La cuenta no está activa");
            }
        }

        // 3. Invalidar tokens anteriores del usuario
        tokenRepository.invalidarTokensAnteriores(usuario.getIdUsuario());

        // 4. Crear nuevo token con código de 6 dígitos
        TokenRecuperacion token = TokenRecuperacion.crear(usuario, vigenciaMinutosCodigo);
        token = tokenRepository.save(token);

        log.info("Código de recuperación generado: {} (válido por {} minutos)",
                token.getCodigo(), vigenciaMinutosCodigo);

        // 5. Enviar email con el código
        try {
            emailService.enviarCodigoRecuperacion(
                    usuario.getEmail(),
                    usuario.getNombresUsuario(),
                    token.getCodigo(),
                    vigenciaMinutosCodigo
            );

            log.info("Código de recuperación enviado exitosamente a: {}", request.getEmail());

        } catch (Exception e) {
            log.error("Error al enviar email de recuperación: {}", e.getMessage());
            throw new RuntimeException("No se pudo enviar el código de recuperación. Intente más tarde.");
        }
    }

    /**
     * Verificar código y resetear contraseña
     */
    public void verificarCodigoYResetearPassword(VerificarCodigoResetPasswordDTO request) {
        log.info("Verificando código de recuperación para: {}", request.getEmail());

        // 1. Buscar usuario
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        // 2. Buscar token vigente
        TokenRecuperacion token = tokenRepository.findByCodigoVigente(
                        request.getCodigo(),
                        LocalDateTime.now()
                )
                .orElseThrow(() -> new TokenInvalidException("Código inválido o expirado"));

        // 3. Verificar que el token pertenece al usuario
        if (!token.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new TokenInvalidException("El código no corresponde al email proporcionado");
        }

        // 4. Cambiar contraseña
        usuario.setPassword(passwordEncoder.encode(request.getNuevaPassword()));
        usuario.reiniciarIntentos();
        usuarioRepository.save(usuario);

        // 5. Marcar token como utilizado
        token.marcarComoUtilizado();
        tokenRepository.save(token);

        log.info("Contraseña reseteada exitosamente para: {}", usuario.getEmail());
    }

    /**
     * Cambiar contraseña (usuario autenticado)
     */
    public void cambiarPassword(Integer usuarioId, CambioPasswordRequestDTO request) {
        log.info("Cambiando contraseña para usuario ID: {}", usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        // Verificar contraseña actual
        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())) {
            throw new BadCredentialsException("Contraseña actual incorrecta");
        }

        // Verificar que la nueva sea diferente
        if (request.getPasswordActual().equals(request.getPasswordNueva())) {
            throw new BadRequestException("La nueva contraseña debe ser diferente a la actual");
        }

        usuario.setPassword(passwordEncoder.encode(request.getPasswordNueva()));
        usuarioRepository.save(usuario);

        log.info("Contraseña cambiada exitosamente para: {}", usuario.getEmail());
    }

    /**
     * Logout
     */
    public void logout(Integer usuarioId, String ip, String userAgent) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        auditoriaService.resgisterLoginWithUser(usuario, usuario.getEmail(), Accion.LOGOUT ,ip, userAgent, "Logout", Resultado.EXITOSO);

        log.info("Logout exitoso para: {}", usuario.getEmail());
    }
}
