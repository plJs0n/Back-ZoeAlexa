package com.zoealexa.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    /**
     * Configurar la cadena de filtros de seguridad
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF (no necesario para APIs stateless con JWT)
                .csrf(AbstractHttpConfigurer::disable)

                // Configurar CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Configurar autorización de requests
                .authorizeHttpRequests(auth -> auth
                        // ============================================
                        // ENDPOINTS PÚBLICOS (sin autenticación)
                        // ============================================

                        // Autenticación
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/obtener-codigo",
                                "/api/auth/recuperar-password",
                                "/api/auth//cambiar-password"
                        ).permitAll()

                        // Búsqueda de viajes (público)
                        .requestMatchers(HttpMethod.GET,
                                "/api/viajes/buscar",
                                "/api/viajes/proximos",
                                "/api/viajes/{id}"
                        ).permitAll()

                        // Swagger/OpenAPI (si está habilitado)
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // Health check (opcional)
                        .requestMatchers("/actuator/health").permitAll()

                        // ============================================
                        // ENDPOINTS SOLO ADMINISTRADOR
                        // ============================================

                        // Gestión de usuarios
                        .requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/*/desbloquear").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/*/estado").hasRole("ADMINISTRADOR")

                        // Creación de viajes
                        .requestMatchers(HttpMethod.POST, "/api/viajes").hasRole("ADMINISTRADOR")

                        // Gestión de puertos
                        .requestMatchers(HttpMethod.POST, "/api/puertos").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/puertos/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/puertos/**").hasRole("ADMINISTRADOR")

                        // ============================================
                        // ENDPOINTS ADMINISTRADOR O ASESOR_VENTAS
                        // ============================================

                        // Reportes y dashboard
                        .requestMatchers("/api/reportes/**").hasAnyRole("ADMINISTRADOR", "ASESOR_VENTAS")

                        // ============================================
                        // TODOS LOS DEMÁS ENDPOINTS (autenticación requerida)
                        // ============================================
                        .anyRequest().authenticated()
                )

                // Configurar gestión de sesiones (stateless para JWT)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Configurar el proveedor de autenticación
                .authenticationProvider(authenticationProvider())

                // Agregar el filtro JWT antes del filtro de autenticación de usuario/password
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configurar el proveedor de autenticación
     * Usa UserDetailsService y PasswordEncoder
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Bean para el AuthenticationManager
     * Necesario para el proceso de autenticación en el login
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Bean para el encriptador de passwords
     * BCrypt es el estándar recomendado
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuración de CORS para permitir requests desde el frontend
     *
     * IMPORTANTE: En producción, configurar origins específicos
     * No usar "*" en producción
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Orígenes permitidos (frontend)
        // En desarrollo: localhost con diferentes puertos
        // En producción: dominio específico
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",      // React
                "http://localhost:4200",      // Angular
                "http://localhost:5173",      // Vite
                "http://localhost:8081",       // Otro frontend
                "https://frontend-zoealexa-production.up.railway.app"
        ));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        // Headers expuestos (el frontend puede leerlos)
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"
        ));

        // Permitir credenciales (cookies, headers de autorización)
        configuration.setAllowCredentials(true);

        // Tiempo máximo de caché para preflight requests (OPTIONS)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }
}
