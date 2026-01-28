package com.zoealexa.repository.transporte;

import com.zoealexa.entity.enums.EstadoPuerto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.zoealexa.entity.transporte.Puerto;

import java.util.List;

@Repository
public interface PuertoRepository extends JpaRepository<Puerto, Integer> {

    /**
     * Busca puertos por estado
     */
    List<Puerto> findByEstado(EstadoPuerto estado);
}