package com.zoealexa.repository.seguridad;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.zoealexa.entity.seguridad.TokenRecuperacion;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository de Tokens de Recuperación - ACTUALIZADO
 */
@Repository
public interface TokenRecuperacionRepository extends JpaRepository<TokenRecuperacion, Integer> {

    /**
     * Busca token por código de 6 dígitos
     */
    Optional<TokenRecuperacion> findByCodigo(String codigo);

    /**
     * Busca token por código y que no esté utilizado
     */
    @Query("SELECT t FROM TokenRecuperacion t WHERE t.codigo = :codigo AND t.utilizado = false")
    Optional<TokenRecuperacion> findByCodigoAndNotUtilizado(@Param("codigo") String codigo);

    /**
     * Busca token vigente (no utilizado y no expirado)
     */
    @Query("SELECT t FROM TokenRecuperacion t WHERE t.codigo = :codigo " +
            "AND t.utilizado = false " +
            "AND t.fechaExpiracion > :ahora")
    Optional<TokenRecuperacion> findByCodigoVigente(
            @Param("codigo") String codigo,
            @Param("ahora") LocalDateTime ahora
    );

    /**
     * Invalida todos los tokens anteriores de un usuario
     */
    @Modifying
    @Query("UPDATE TokenRecuperacion t SET t.utilizado = true " +
            "WHERE t.usuario.idUsuario = :usuarioId AND t.utilizado = false")
    void invalidarTokensAnteriores(@Param("usuarioId") Integer usuarioId);

    /**
     * Cuenta tokens vigentes de un usuario
     */
    @Query("SELECT COUNT(t) FROM TokenRecuperacion t " +
            "WHERE t.usuario.idUsuario = :usuarioId " +
            "AND t.utilizado = false " +
            "AND t.fechaExpiracion > :ahora")
    Long contarTokensVigentes(
            @Param("usuarioId") Integer usuarioId,
            @Param("ahora") LocalDateTime ahora
    );

    /**
     * Elimina tokens expirados (limpieza periódica)
     */
    @Modifying
    @Query("DELETE FROM TokenRecuperacion t WHERE t.fechaExpiracion < :fecha")
    void eliminarTokensExpirados(@Param("fecha") LocalDateTime fecha);
}