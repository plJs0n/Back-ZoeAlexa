package com.zoealexa.repository.seguridad;

import com.zoealexa.entity.enums.TipoDocumento;
import com.zoealexa.entity.reservas.Pasajero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// =====================================================
// PASAJERO REPOSITORY
// =====================================================
@Repository
public interface PasajeroRepository extends JpaRepository<Pasajero, Long> {

    Optional<Pasajero> findByTipoDocumentoAndNumeroDocumento(
            TipoDocumento tipoDocumento,
            String numeroDocumento
    );

    boolean existsByTipoDocumentoAndNumeroDocumento(
            TipoDocumento tipoDocumento,
            String numeroDocumento
    );

    List<Pasajero> findByApellidosContainingIgnoreCase(String apellidos);
}