package com.zoealexa.service.transporte;

import com.zoealexa.dto.transporte.UpdateRutaRequestDTO;
import com.zoealexa.entity.enums.EstadoEmbarcacion;
import com.zoealexa.entity.enums.EstadoPuerto;
import com.zoealexa.exception.BusinessException;
import com.zoealexa.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zoealexa.dto.transporte.RutaRequestDTO;
import com.zoealexa.dto.transporte.RutaResponseDTO;
import com.zoealexa.entity.enums.EstadoRuta;
import com.zoealexa.entity.transporte.Puerto;
import com.zoealexa.entity.transporte.Ruta;
import com.zoealexa.exception.NotFoundException;
import com.zoealexa.mapper.transporte.RutaMapper;
import com.zoealexa.repository.transporte.PuertoRepository;
import com.zoealexa.repository.transporte.RutaRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de Rutas
 * */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RutaService {

    private final RutaRepository rutaRepository;
    private final PuertoRepository puertoRepository;

    /**
     * Crear nueva ruta
     */
    public RutaResponseDTO crear(RutaRequestDTO request) {
        log.info("Creando ruta: Puerto {} → Puerto {}",
                request.getIdPuertoOrigen(), request.getIdPuertoDestino());

        // 1. Validar que origen y destino sean diferentes
        if (request.getIdPuertoOrigen().equals(request.getIdPuertoDestino())) {
            throw new ConflictException("El puerto de origen y destino no pueden ser el mismo");
        }

        // 2. Buscar puerto de origen
        Puerto puertoOrigen = puertoRepository.findById(request.getIdPuertoOrigen())
                .orElseThrow(() -> new NotFoundException(
                        "Puerto de origen no encontrado con ID: " + request.getIdPuertoOrigen()
                ));
        if (puertoOrigen.getEstado() != EstadoPuerto.HABILITADO) {
            throw new ConflictException("No se puede crear viaje con un Puerto de Origen no Habilitado");
        }

        // 3. Buscar puerto de destino
        Puerto puertoDestino = puertoRepository.findById(request.getIdPuertoDestino())
                .orElseThrow(() -> new NotFoundException(
                        "Puerto de destino no encontrado con ID: " + request.getIdPuertoDestino()
                ));
        if (puertoDestino.getEstado() != EstadoPuerto.HABILITADO) {
            throw new ConflictException("No se puede crear viaje con un Puerto Destino no Habilitado");
        }

        // 4. Crear ruta
        Ruta ruta = RutaMapper.toEntity(request);
        ruta.setPuertoOrigen(puertoOrigen);
        ruta.setPuertoDestino(puertoDestino);

        // Estado por defecto: ACTIVA
        if (ruta.getEstado() == null) {
            ruta.setEstado(EstadoRuta.ACTIVA);
        }

        // 5. Guardar
        ruta = rutaRepository.save(ruta);

        log.info("Ruta creada exitosamente con ID: {} ({} → {})",
                ruta.getIdRuta(),
                puertoOrigen.getNombrePuerto(),
                puertoDestino.getNombrePuerto());

        return RutaMapper.toResponseDTO(ruta);
    }

    /**
     * Listar todas las rutas
     */
    @Transactional(readOnly = true)
    public List<RutaResponseDTO> listarTodas() {
        return rutaRepository.findAll().stream()
                .map(RutaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar ruta por ID
     */
    @Transactional(readOnly = true)
    public RutaResponseDTO buscarPorId(Integer id) {
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ruta no encontrada con ID: " + id));

        return RutaMapper.toResponseDTO(ruta);
    }

    /**
     * Listar rutas activas
     */
    @Transactional(readOnly = true)
    public List<RutaResponseDTO> listarActivas() {
        return rutaRepository.findByEstado(EstadoRuta.ACTIVA).stream()
                .map(RutaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualizar ruta (ACTUALIZACIÓN PARCIAL - FLEXIBLE)
     * */
    public RutaResponseDTO actualizar(Integer id, UpdateRutaRequestDTO request) {
        log.info("Actualizando ruta ID: {}", id);

        // 1. Buscar ruta existente
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ruta no encontrada con ID: " + id));

        // 2. Validar que si se envían ambos puertos, sean diferentes
        Integer origenId = request.getIdPuertoOrigen() != null
                ? request.getIdPuertoOrigen()
                : ruta.getPuertoOrigen().getIdPuerto();

        Integer destinoId = request.getIdPuertoDestino() != null
                ? request.getIdPuertoDestino()
                : ruta.getPuertoDestino().getIdPuerto();

        if (origenId.equals(destinoId)) {
            throw new ConflictException(
                    "El puerto de origen y destino no pueden ser el mismo"
            );
        }

        // 3. Actualizar puerto de origen si se envió
        if (request.getIdPuertoOrigen() != null) {
            Puerto puertoOrigen = puertoRepository.findById(request.getIdPuertoOrigen())
                    .orElseThrow(() -> new NotFoundException(
                            "Puerto de origen no encontrado con ID: " + request.getIdPuertoOrigen()
                    ));
            if (puertoOrigen.getEstado() != EstadoPuerto.HABILITADO) {
                throw new ConflictException("No se puede crear viaje con un Puerto de Origen no Habilitado");
            }
            ruta.setPuertoOrigen(puertoOrigen);
            log.debug("Puerto de origen actualizado a: {}", puertoOrigen.getNombrePuerto());
        }

        // 4. Actualizar puerto de destino si se envió
        if (request.getIdPuertoDestino() != null) {
            Puerto puertoDestino = puertoRepository.findById(request.getIdPuertoDestino())
                    .orElseThrow(() -> new NotFoundException(
                            "Puerto de destino no encontrado con ID: " + request.getIdPuertoDestino()
                    ));
            if (puertoDestino.getEstado() != EstadoPuerto.HABILITADO) {
                throw new ConflictException("No se puede crear viaje con un Puerto de Destino no Habilitado");
            }
            ruta.setPuertoDestino(puertoDestino);
            log.debug("Puerto de destino actualizado a: {}", puertoDestino.getNombrePuerto());
        }

        // 5. Actualizar duración si se envió
        if (request.getDiasOperacion() != null) {
            ruta.setDiasOperacion(request.getDiasOperacion());
            log.debug("Duración actualizada a: {} horas", request.getDiasOperacion());
        }

        // 6. Actualizar estado si se envió
        if (request.getEstado() != null) {
            ruta.setEstado(request.getEstado());
            log.debug("Estado actualizado a: {}", request.getEstado());
        }

        // 7. Guardar cambios
        ruta = rutaRepository.save(ruta);

        log.info("Ruta {} actualizada exitosamente", id);

        return RutaMapper.toResponseDTO(ruta);
    }
}