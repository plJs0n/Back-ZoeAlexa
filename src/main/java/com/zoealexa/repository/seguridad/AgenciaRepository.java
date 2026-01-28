package com.zoealexa.repository.seguridad;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.zoealexa.entity.seguridad.Agencia;
import com.zoealexa.entity.enums.EstadoAgencia;

import java.util.List;
import java.util.Optional;

/**
 * Repository de Agencias
 */
@Repository
public interface AgenciaRepository extends JpaRepository<Agencia, Integer> {

    /**
     * Busca agencia por RUC
     */
    Optional<Agencia> findByRuc(String ruc);

    /**
     * Verifica si existe una agencia con el RUC dado
     */
    boolean existsByRuc(String ruc);

    /**
     * Lista agencias por estado
     */
    List<Agencia> findByEstado(EstadoAgencia estado);

    /**
     * Lista agencias activas
     */
    @Query("SELECT a FROM Agencia a WHERE a.estado = 'ACTIVO' ORDER BY a.nombreAgencia ASC")
    List<Agencia> findAgenciasActivas();

    /**
     * Busca agencias por nombre (búsqueda parcial)
     */
    @Query("SELECT a FROM Agencia a WHERE LOWER(a.nombreAgencia) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Agencia> buscarPorNombre(@Param("nombre") String nombre);

    /**
     * Cuenta agencias activas
     */
    @Query("SELECT COUNT(a) FROM Agencia a WHERE a.estado = 'ACTIVO'")
    Long contarAgenciasActivas();

    /**
     * Lista agencias con comisión por porcentaje
     */
    @Query("SELECT a FROM Agencia a WHERE a.tipoComision = 'PORCENTAJE' AND a.estado = 'ACTIVO'")
    List<Agencia> findAgenciasConComisionPorcentaje();

    /**
     * Lista agencias con comisión por monto fijo
     */
    @Query("SELECT a FROM Agencia a WHERE a.tipoComision = 'MONTO_FIJO' AND a.estado = 'ACTIVO'")
    List<Agencia> findAgenciasConComisionMontoFijo();
}