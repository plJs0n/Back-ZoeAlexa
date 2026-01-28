package com.zoealexa.service.seguridad;
import com.zoealexa.entity.enums.Accion;
import com.zoealexa.entity.enums.Resultado;
import com.zoealexa.entity.seguridad.Usuario;
import com.zoealexa.repository.seguridad.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginAttemptService {

    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarIncorrectPassword(Usuario usuario, String email, String ip, String userAgent){

        log.warn("⚠️ Registrando intento fallido para: {}", email);

        // Incrementar intentos por contraseña incorrecta
        usuario.incrementarIntentosFallidos();

        String motivo = "Contraseña incorrecta";

        // Bloquear si alcanzó el límite
        if (usuario.getIntentosFallidos() >= 5) {
            usuario.bloquearTemporalmente(1);
            motivo = "Usuario bloqueado por múltiples intentos";
            log.warn("🔒 Usuario bloqueado: {}", email);
        }

        // Guardar usuario
        usuarioRepository.save(usuario);

        log.info("💾 Usuario guardado - Intentos: {}", usuario.getIntentosFallidos());

        // Registrar auditoría
        auditoriaService.resgisterLoginWithUser(usuario,email, Accion.LOGIN_FALLIDO, ip, userAgent, motivo, Resultado.FALLIDO);

        log.warn("❌ Login fallido - Intentos: {}/5", usuario.getIntentosFallidos());
    }
}
