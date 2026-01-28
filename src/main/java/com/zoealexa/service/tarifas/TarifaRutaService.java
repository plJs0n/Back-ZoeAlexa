package com.zoealexa.service.tarifas;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zoealexa.dto.tarifas.TarifaRutaRequestDTO;
import com.zoealexa.dto.tarifas.TarifaRutaResponseDTO;
import com.zoealexa.entity.tarifas.TarifaRuta;
import com.zoealexa.entity.transporte.Ruta;
import com.zoealexa.exception.NotFoundException;
import com.zoealexa.mapper.tarifas.TarifaRutaMapper;
import com.zoealexa.repository.tarifas.TarifaRutaRepository;
import com.zoealexa.repository.transporte.RutaRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TarifaRutaService {

    private final TarifaRutaRepository tarifaRepository;
    private final RutaRepository rutaRepository;

    /**
     * Crear nueva tarifa para una ruta
     * - Cierra automáticamente la tarifa vigente (si existe)
     * - No pide fechas
     */
    public TarifaRutaResponseDTO crear(TarifaRutaRequestDTO request) {

        log.info("Creando nueva tarifa para ruta ID: {}", request.getIdRuta());

        Ruta ruta = rutaRepository.findById(request.getIdRuta())
                .orElseThrow(() ->
                        new NotFoundException("Ruta no encontrada con ID: " + request.getIdRuta())
                );

        // 1️⃣ Cerrar tarifa vigente (si existe)
        tarifaRepository.findByRutaIdRutaAndFechaFinIsNull(ruta.getIdRuta())
                .ifPresent(tarifaActiva -> {
                    log.info("Cerrando tarifa vigente ID: {}", tarifaActiva.getIdTarifa());
                    tarifaActiva.setFechaFin(LocalDateTime.now());
                    tarifaRepository.save(tarifaActiva);
                });

        // 2️⃣ Crear nueva tarifa
        TarifaRuta nuevaTarifa = TarifaRuta.builder()
                .ruta(ruta)
                .precioBase(request.getPrecioBase())
                .fechaInicio(LocalDateTime.now())
                .fechaFin(null)
                .build();

        nuevaTarifa = tarifaRepository.save(nuevaTarifa);

        log.info("Nueva tarifa creada con ID: {}", nuevaTarifa.getIdTarifa());

        return TarifaRutaMapper.toResponseDTO(nuevaTarifa);
    }

    /**
     * Listar todas las tarifas (histórico completo)
     */
    @Transactional(readOnly = true)
    public List<TarifaRutaResponseDTO> listarTodas() {
        return tarifaRepository.findAll()
                .stream()
                .map(TarifaRutaMapper::toResponseDTO)
                .toList();
    }

    /**
     * Obtener tarifa vigente por ruta
     */
    @Transactional(readOnly = true)
    public TarifaRutaResponseDTO obtenerPorRuta(Integer rutaId) {

        log.info("Buscando tarifa vigente para ruta ID: {}", rutaId);

        if (!rutaRepository.existsById(rutaId)) {
            throw new NotFoundException("Ruta no encontrada con ID: " + rutaId);
        }

        TarifaRuta tarifa = tarifaRepository
                .findByRutaIdRutaAndFechaFinIsNull(rutaId)
                .orElseThrow(() ->
                        new NotFoundException("No existe tarifa vigente para la ruta ID: " + rutaId)
                );

        return TarifaRutaMapper.toResponseDTO(tarifa);
    }
}