package com.zoealexa.service.reserva;

import com.zoealexa.entity.enums.Rol;
import com.zoealexa.entity.reservas.Reserva;
import com.zoealexa.entity.seguridad.Usuario;
import com.zoealexa.exception.ConflictException;
import com.zoealexa.exception.OperacionNoPermitidaException;
import com.zoealexa.exception.RecursoNoEncontradoException;
import com.zoealexa.repository.reservas.ReservaRepository;
import com.zoealexa.repository.seguridad.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Servicio para validar permisos y autorización
 * Usado para verificar que las agencias solo vean sus propias reservas
 */
@Service("autorizacionService")
@RequiredArgsConstructor
@Slf4j
public class AutorizacionService {

    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;

    /**
     * Verifica si el usuario actual puede ver una reserva específica
     * REGLAS:
     * - ASESOR_VENTAS: Puede ver todas las reservas
     * - AGENCIA: Solo puede ver reservas de su propia agencia
     * - ADMINISTRADOR: Puede ver todas (aunque no tenga acceso al endpoint)
     *
     * @param codigoReserva Código de la reserva a verificar
     * @return true si tiene acceso, false si no
     */
    public boolean puedeVerReserva(String codigoReserva) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                log.warn("Usuario no autenticado intentando ver reserva: {}", codigoReserva);
                return false;
            }

            String email = auth.getName();
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", email));

            // Si es ASESOR_VENTAS o ADMINISTRADOR, puede ver todas
            if (usuario.getRol() == Rol.ASESOR_VENTAS || usuario.getRol() == Rol.ADMINISTRADOR) {
                log.debug("Usuario {} (rol: {}) puede ver todas las reservas",
                        email, usuario.getRol());
                return true;
            }

            // Si es AGENCIA, solo puede ver sus propias reservas
            if (usuario.getRol() == Rol.AGENCIA) {
                Reserva reserva = reservaRepository.findByCodigoReserva(codigoReserva)
                        .orElseThrow(() -> new RecursoNoEncontradoException("Reserva", codigoReserva));

                // Si la reserva no tiene agencia asociada, no puede verla
                if (reserva.getAgencia() == null) {
                    log.warn("Agencia {} intentó ver reserva sin agencia: {}",
                            usuario.getAgencia().getIdAgencia(), codigoReserva);
                    return false;
                }

                // Verificar que la reserva pertenezca a su agencia
                boolean perteneceAMiAgencia = reserva.getAgencia().getIdAgencia()
                        .equals(usuario.getAgencia().getIdAgencia());

                if (!perteneceAMiAgencia) {
                    log.warn("Agencia {} intentó ver reserva de otra agencia: {}",
                            usuario.getAgencia().getIdAgencia(), codigoReserva);
                }

                return perteneceAMiAgencia;
            }

            log.warn("Rol desconocido: {} intentando ver reserva", usuario.getRol());
            return false;

        } catch (Exception e) {
            log.error("Error al verificar permisos para reserva: {}", codigoReserva, e);
            return false;
        }
    }

    /**
     * Verifica si el usuario actual puede modificar una reserva
     * (cancelar, reprogramar, registrar pago, etc.)
     *
     * @param codigoReserva Código de la reserva
     * @return true si puede modificar, false si no
     */
    public boolean puedeModificarReserva(String codigoReserva) {
        // Por ahora, las mismas reglas que para ver
        // Podrías agregar reglas más estrictas aquí
        return puedeVerReserva(codigoReserva);
    }

    /**
     * Valida y lanza excepción si no tiene permisos
     *
     * @param codigoReserva Código de la reserva
     * @throws OperacionNoPermitidaException si no tiene permisos
     */
    public void validarAccesoReserva(String codigoReserva) {
        if (!puedeVerReserva(codigoReserva)) {
            throw new ConflictException(
                    "No tienes permisos para acceder a esta reserva"
            );
        }
    }

    /**
     * Obtiene el ID de agencia del usuario actual (si es AGENCIA)
     *
     * @return ID de agencia o null si no es rol AGENCIA
     */
    public Integer obtenerIdAgenciaActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario != null && usuario.getRol() == Rol.AGENCIA) {
            return usuario.getAgencia().getIdAgencia();
        }

        return null;
    }

    /**
     * Verifica si el usuario actual es ASESOR_VENTAS
     */
    public boolean esAsesorVentas() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;

        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ASESOR_VENTAS"));
    }

    /**
     * Verifica si el usuario actual es AGENCIA
     */
    public boolean esAgencia() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;

        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_AGENCIA"));
    }

    /**
     * Valida que la agencia solo pueda crear reservas para sí misma
     * REGLAS:
     * - ASESOR_VENTAS: Puede crear para cualquier agencia o sin agencia (venta directa)
     * - AGENCIA: Solo puede crear reservas asociadas a su propia agencia
     *
     * @param idAgenciaReserva ID de agencia que se está intentando asociar a la reserva
     * @throws OperacionNoPermitidaException si la agencia intenta crear para otra agencia
     */
    public void validarCreacionReserva(Integer idAgenciaReserva) {
        // Si es ASESOR_VENTAS, puede crear para cualquier agencia
        if (esAsesorVentas()) {
            log.debug("ASESOR_VENTAS puede crear reserva para cualquier agencia");
            return;
        }

        // Si es AGENCIA, validar que sea su propia agencia
        if (esAgencia()) {
            Integer idAgenciaActual = obtenerIdAgenciaActual();

            if (idAgenciaActual == null) {
                log.error("Usuario AGENCIA sin id_agencia asociado");
                throw new ConflictException(
                        "Error de configuración: Usuario AGENCIA sin agencia asociada"
                );
            }

            // La agencia debe crear reservas asociadas a ella misma
            if (idAgenciaReserva == null) {
                throw new ConflictException(
                        "Las agencias deben asociar sus reservas a su propia agencia"
                );
            }

            if (!idAgenciaActual.equals(idAgenciaReserva)) {
                log.warn("Agencia {} intentó crear reserva para Agencia {}",
                        idAgenciaActual, idAgenciaReserva);
                throw new ConflictException(
                        "No puedes crear reservas para otra agencia. Solo puedes crear reservas asociadas a tu agencia."
                );
            }

            log.debug("Validación OK: Agencia {} creando reserva para sí misma", idAgenciaActual);
        }
    }
}