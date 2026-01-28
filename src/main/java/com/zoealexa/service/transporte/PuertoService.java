package com.zoealexa.service.transporte;

import com.zoealexa.dto.transporte.EmbarcacionResponseDTO;
import com.zoealexa.dto.transporte.UpdatePuertoRequestDTO;
import com.zoealexa.entity.enums.EstadoEmbarcacion;
import com.zoealexa.entity.enums.TipoOperacion;
import com.zoealexa.mapper.transporte.EmbarcacionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zoealexa.dto.transporte.PuertoRequestDTO;
import com.zoealexa.dto.transporte.PuertoResponseDTO;
import com.zoealexa.entity.enums.EstadoPuerto;
import com.zoealexa.entity.transporte.Puerto;
import com.zoealexa.exception.NotFoundException;
import com.zoealexa.mapper.transporte.PuertoMapper;
import com.zoealexa.repository.transporte.PuertoRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de Puertos
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PuertoService {

    private final PuertoRepository puertoRepository;

    /**
     * Crear nuevo puerto
     */
    public PuertoResponseDTO crear(PuertoRequestDTO request) {
        log.info("Creando puerto: {}", request.getNombrePuerto());

        Puerto puerto = PuertoMapper.toEntity(request);

        // Valores por defecto si no se envían
        if (puerto.getTipoOperacion() == null) {
            puerto.setTipoOperacion(TipoOperacion.AMBOS);
        }
        if (puerto.getEstado() == null) {
            puerto.setEstado(EstadoPuerto.HABILITADO);
        }

        puerto = puertoRepository.save(puerto);

        log.info("Puerto creado exitosamente con ID: {}", puerto.getIdPuerto());

        return PuertoMapper.toResponseDTO(puerto);
    }

    /**
     * Listar todos los puertos
     */
    @Transactional(readOnly = true)
    public List<PuertoResponseDTO> listarTodos() {
        return puertoRepository.findAll().stream()
                .map(PuertoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualizar puerto (solo campos enviados)
     */
    public PuertoResponseDTO actualizar(Integer id, UpdatePuertoRequestDTO request) {
        Puerto puerto = puertoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Puerto no encontrado con ID: " + id));

        // Actualizar solo los campos que no son null
        if (request.getNombrePuerto() != null) {
            puerto.setNombrePuerto(request.getNombrePuerto());
        }
        if (request.getCiudad() != null) {
            puerto.setCiudad(request.getCiudad());
        }
        if (request.getDireccion() != null) {
            puerto.setDireccion(request.getDireccion());
        }
        if (request.getTipoOperacion() != null) {
            puerto.setTipoOperacion(request.getTipoOperacion());
        }
        if (request.getEstado() != null) {
            puerto.setEstado(request.getEstado());
        }

        puerto = puertoRepository.save(puerto);

        log.info("Puerto actualizado: {}", puerto.getIdPuerto());

        return PuertoMapper.toResponseDTO(puerto);
    }

    /**
     * Listar rutas activas
     */
    @Transactional(readOnly = true)
    public List<PuertoResponseDTO> listarEnServicio() {
        return puertoRepository.findByEstado(EstadoPuerto.HABILITADO).stream()
                .map(PuertoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}