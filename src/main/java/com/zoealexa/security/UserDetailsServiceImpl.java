package com.zoealexa.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.zoealexa.entity.seguridad.Usuario;
import com.zoealexa.entity.enums.EstadoUsuario;
import com.zoealexa.repository.seguridad.UsuarioRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Cargar usuario por email (username en Spring Security)
     *
     * @param email Email del usuario
     * @return UserDetails con información del usuario
     * @throws UsernameNotFoundException Si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Cargando usuario por email: {}", email);

        // 1. Buscar usuario en la base de datos
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado: {}", email);
                    return new UsernameNotFoundException("Usuario no encontrado: " + email);
                });

        // 2. Verificar que el usuario esté activo
        if (usuario.getEstado() != EstadoUsuario.ACTIVO) {
            log.warn("Usuario inactivo: {}", email);
            throw new UsernameNotFoundException("Usuario inactivo: " + email);
        }

        // 3. Verificar que el usuario no esté bloqueado
        if (usuario.estaBloqueado()) {
            log.warn("Usuario bloqueado: {}", email);
            throw new UsernameNotFoundException("Usuario bloqueado temporalmente");
        }

        log.debug("Usuario cargado exitosamente: {} - Rol: {}",
                email, usuario.getRol());

        // 4. Construir y retornar UserDetails
        return buildUserDetails(usuario);
    }

    /**
     * Construir UserDetails desde la entidad Usuario
     *
     * @param usuario Entidad Usuario
     * @return UserDetails para Spring Security
     */
    private UserDetails buildUserDetails(Usuario usuario) {
        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities(getAuthorities(usuario))
                .accountExpired(false)
                .accountLocked(usuario.estaBloqueado())
                .credentialsExpired(false)
                .disabled(usuario.getEstado() != EstadoUsuario.ACTIVO)
                .build();
    }

    /**
     * Obtener las autoridades (roles) del usuario
     *
     * Spring Security usa el prefijo "ROLE_" para roles
     * Por ejemplo: ADMINISTRADOR se convierte en ROLE_ADMINISTRADOR
     *
     * @param usuario Entidad Usuario
     * @return Colección de autoridades
     */
    private Collection<? extends GrantedAuthority> getAuthorities(Usuario usuario) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Agregar rol con prefijo ROLE_
        String roleName = "ROLE_" + usuario.getRol();
        authorities.add(new SimpleGrantedAuthority(roleName));

        log.debug("Autoridades asignadas para {}: {}", usuario.getEmail(), roleName);

        return authorities;
    }
}
