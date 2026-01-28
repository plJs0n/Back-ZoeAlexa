package com.zoealexa.service.transporte;

import com.zoealexa.dto.transporte.EmbarcacionRequestDTO;
import com.zoealexa.dto.transporte.EmbarcacionResponseDTO;
import com.zoealexa.dto.transporte.RutaResponseDTO;
import com.zoealexa.dto.transporte.UpdateEmbarcacionRequestDTO;
import com.zoealexa.entity.enums.EstadoEmbarcacion;
import com.zoealexa.entity.enums.EstadoRuta;
import com.zoealexa.entity.transporte.Embarcacion;
import com.zoealexa.exception.ConflictException;
import com.zoealexa.exception.NotFoundException;
import com.zoealexa.mapper.transporte.EmbarcacionMapper;
import com.zoealexa.mapper.transporte.RutaMapper;
import com.zoealexa.repository.transporte.EmbarcacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EmbarcacionService {

    private final EmbarcacionRepository embarcacionRepository;

    /**
     * Crear nueva embarcación
     * */
    public EmbarcacionResponseDTO crear(EmbarcacionRequestDTO request){
        log.info("Creando embarcación: {}", request.getNombreEmbarcacion());

        if (embarcacionRepository.existsByNombreEmbarcacion(request.getNombreEmbarcacion())){
            log.warn("Embarcacion ya registrado: {}", request.getNombreEmbarcacion());
            throw new ConflictException("El nombre de la embarcación ya existe");
        }

        Embarcacion embarcacion = EmbarcacionMapper.toEntity(request);

        // Valor por defecto si no se envía
        if (embarcacion.getEstado() == null){
            embarcacion.setEstado(EstadoEmbarcacion.EN_SERVICIO);
        }

        embarcacion = embarcacionRepository.save(embarcacion);

        log.info("Embarcación creada exitosamente con ID: {}", embarcacion.getIdEmbarcacion());

        return EmbarcacionMapper.toResponseDTO(embarcacion);
    }

    /**
     * Listar todas las embarcaciones
     * */
    @Transactional(readOnly = true)
    public List<EmbarcacionResponseDTO> listarTodas() {
        log.debug("Listando todas las embarcaciones");

        return embarcacionRepository.findAll().stream()
                .map(EmbarcacionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualizar embarcación (solo campos enviados)
     * */
    public EmbarcacionResponseDTO actualizar(Integer id, EmbarcacionRequestDTO request) {
        log.info("Actualizando embarcación ID: {}", id);

        Embarcacion embarcacion = embarcacionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Embarcación no encontrada con ID: " + id));

        if (embarcacionRepository.existsByNombreEmbarcacionAndIdEmbarcacionNot(request.getNombreEmbarcacion(), id)){

            throw new ConflictException("El nombre de la embarcación ya existe");
        }

        // Actualizar solo los campos que no son null
        if (request.getNombreEmbarcacion() != null) {
            embarcacion.setNombreEmbarcacion(request.getNombreEmbarcacion());
        }
        if (request.getCapacidad() != null) {
            embarcacion.setCapacidad(request.getCapacidad());
        }
        if (request.getEstado() != null) {
            embarcacion.setEstado(request.getEstado());
        }

        embarcacion = embarcacionRepository.save(embarcacion);

        log.info("Embarcación actualizada: {}", embarcacion.getIdEmbarcacion());

        return EmbarcacionMapper.toResponseDTO(embarcacion);
    }
}
