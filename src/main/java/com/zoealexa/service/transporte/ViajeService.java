package com.zoealexa.service.transporte;

import com.zoealexa.entity.enums.EstadoEmbarcacion;
import com.zoealexa.entity.enums.EstadoRuta;
import com.zoealexa.exception.BadRequestException;
import com.zoealexa.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zoealexa.dto.transporte.*;
import com.zoealexa.entity.enums.EstadoViaje;
import com.zoealexa.entity.transporte.*;
import com.zoealexa.exception.NotFoundException;
import com.zoealexa.mapper.transporte.ViajeMapper;
import com.zoealexa.repository.transporte.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ViajeService {

    private final ViajeRepository viajeRepository;
    private final RutaRepository rutaRepository;
    private final EmbarcacionRepository embarcacionRepository;

    /**
     * Crear nuevo viaje
     */
    public ViajeResponseDTO crear(ViajeRequestDTO request) {
        log.debug("Creando viaje para ruta: {}", request.getIdRuta());

        // 1. Validar ruta
        Ruta ruta = rutaRepository.findById(request.getIdRuta())
                .orElseThrow(() -> new NotFoundException("Ruta no encontrada con id: " + request.getIdRuta()));

        if (ruta.getEstado() != EstadoRuta.ACTIVA) {
            throw new ConflictException("No se puede crear viaje para una ruta inactiva");
        }

        // 2. Validar embarcación
        Embarcacion embarcacion = embarcacionRepository.findById(request.getIdEmbarcacion())
                .orElseThrow(() -> new NotFoundException("Embarcación no encontrada con id: " + request.getIdEmbarcacion()));

        if (embarcacion.getEstado() != EstadoEmbarcacion.EN_SERVICIO) {
            throw new ConflictException("No se puede crear viaje con una embarcación no operativa");
        }

        // 3. Crear viaje
        Viaje viaje = ViajeMapper.toEntity(request);
        viaje.setRuta(ruta);
        viaje.setEmbarcacion(embarcacion);

        // ⬅️ AGREGAR ESTO: Establecer cupos disponibles desde la capacidad de la embarcación
        if (request.getCuposDisponibles() == null) {
            viaje.setCuposDisponibles(embarcacion.getCapacidad());
            log.debug("Cupos disponibles establecidos automáticamente: {}", embarcacion.getCapacidad());
        }

        // Validar que la fecha sea futura (solo si el viaje está programado)
        if (viaje.getEstado() == EstadoViaje.PROGRAMADO) {
            LocalDate hoy = LocalDate.now();
            if (request.getFechaViaje().isBefore(hoy)) {
                throw new BadRequestException("La fecha del viaje debe ser igual o posterior a hoy");
            }
        }

        // Validar que coincida con los días de operación de la ruta
        DayOfWeek diaSemana = request.getFechaViaje().getDayOfWeek();
        String codigoDia = obtenerCodigoDia(diaSemana);

        if (!viaje.getRuta().getDiasOperacion().contains(codigoDia)) {
            throw new BadRequestException(
                    String.format("La fecha %s (%s) no coincide con los días de operación de la ruta: %s",
                                request.getFechaViaje(), diaSemana, viaje.getRuta().getDiasOperacion())
            );
        }

        // 4. Guardar
        Viaje viajeGuardado = viajeRepository.save(viaje);

        log.info("Viaje creado exitosamente con id: {}", viajeGuardado.getIdViaje());
        return ViajeMapper.toResponseDTO(viajeGuardado);
    }

    /**
     * Buscar viajes disponibles
     */
    @Transactional(readOnly = true)
    public List<ViajeBusquedaDTO> buscarDisponibles(
            Integer rutaId,
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        log.info("Buscando viajes disponibles - Ruta: {}, Del {} al {}",
                rutaId, fechaInicio, fechaFin);

        List<Viaje> viajes = viajeRepository.findViajesDisponibles(
                rutaId,
                fechaInicio,
                fechaFin
        );

        log.info("Viajes encontrados: {}", viajes.size());

        return viajes.stream()
                .map(ViajeMapper::toBusquedaDTO)
                .collect(Collectors.toList());
    }
    /**
     * Listar Viajes
     */
    @Transactional(readOnly = true)
    public List<ViajeResponseDTO> listarTodos(){
        return viajeRepository.findAll().stream()
                .map(ViajeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener viajes próximos (siguientes 7 días)
     */
    @Transactional(readOnly = true)
    public List<ViajeBusquedaDTO> obtenerProximos() {
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(7);

        log.info("Obteniendo viajes próximos (7 días)");

        List<Viaje> viajes = viajeRepository.findViajesProximos(hoy, limite);

        return viajes.stream()
                .map(ViajeMapper::toBusquedaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar viaje por ID
     */
    @Transactional(readOnly = true)
    public ViajeResponseDTO buscarPorId(Integer id) {
        Viaje viaje = viajeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Viaje no encontrado"));

        return ViajeMapper.toResponseDTO(viaje);
    }

    /**
     * Listar viajes por estado
     */
    @Transactional(readOnly = true)
    public List<ViajeResponseDTO> listarPorEstado(EstadoViaje estado) {
        log.info("Listando viajes por estado: {}", estado);

        return viajeRepository.findByEstadoOrderByFechaViajeDesc(estado)
                .stream()
                .map(ViajeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }



    /**
     * Actualiza un viaje existente
     * Permite actualización parcial (solo los campos proporcionados)
     */
    @Transactional
    public ViajeResponseDTO actualizar(Integer idViaje, ViajeUpdateDTO updateDTO) {
        log.info("Actualizando viaje con ID: {}", idViaje);

        // Verificar que el viaje existe
        Viaje viaje = viajeRepository.findById(idViaje)
                .orElseThrow(() -> new NotFoundException("Viaje no encontrado con ID: " + idViaje));

        // Actualizar ruta si se proporciona
        if (updateDTO.hasIdRuta()) {
            log.debug("Actualizando ruta a ID: {}", updateDTO.getIdRuta());
            Ruta ruta = rutaRepository.findById(updateDTO.getIdRuta())
                    .orElseThrow(() -> new BadRequestException("Ruta no encontrada con ID: " + updateDTO.getIdRuta()));

            // Validar que la ruta esté activa
            if (ruta.getEstado() != EstadoRuta.ACTIVA) {
                throw new BadRequestException("La ruta con ID " + updateDTO.getIdRuta() + " no está activa");
            }

            viaje.setRuta(ruta);
        }

        // Actualizar embarcación si se proporciona
        if (updateDTO.hasIdEmbarcacion()) {
            log.debug("Actualizando embarcación a ID: {}", updateDTO.getIdEmbarcacion());
            Embarcacion embarcacion = embarcacionRepository.findById(updateDTO.getIdEmbarcacion())
                    .orElseThrow(() -> new BadRequestException("Embarcación no encontrada con ID: " + updateDTO.getIdEmbarcacion()));

            // Validar que la embarcación esté disponible
            if (embarcacion.getEstado() != EstadoEmbarcacion.EN_SERVICIO) {
                throw new BadRequestException("La embarcación con ID " + updateDTO.getIdEmbarcacion() + " no está disponible");
            }

            // Si se cambia la embarcación y NO se proporcionan cupos, ajustar automáticamente
            if (!updateDTO.hasCuposDisponibles()) {
                log.debug("Ajustando cupos disponibles a la capacidad de la nueva embarcación: {}", embarcacion.getCapacidad());
                if (embarcacion.getCapacidad() - viaje.getCuposOcupados() < 0){
                    throw new ConflictException("Capacidad de embarcación insuficiente");
                }
                viaje.setCuposDisponibles(embarcacion.getCapacidad() - viaje.getCuposOcupados());
            }

            viaje.setEmbarcacion(embarcacion);
        }

        // Actualizar fecha si se proporciona
        if (updateDTO.hasFechaViaje()) {
            log.debug("Actualizando fecha a: {}", updateDTO.getFechaViaje());

            // Validar que la fecha sea futura (solo si el viaje está programado)
            if (viaje.getEstado() == EstadoViaje.PROGRAMADO) {
                LocalDate hoy = LocalDate.now();
                if (updateDTO.getFechaViaje().isBefore(hoy)) {
                    throw new BadRequestException("La fecha del viaje debe ser igual o posterior a hoy");
                }
            }

            // Validar que coincida con los días de operación de la ruta
            DayOfWeek diaSemana = updateDTO.getFechaViaje().getDayOfWeek();
            String codigoDia = obtenerCodigoDia(diaSemana);

            if (!viaje.getRuta().getDiasOperacion().contains(codigoDia)) {
                throw new BadRequestException(
                        String.format("La fecha %s (%s) no coincide con los días de operación de la ruta: %s",
                                updateDTO.getFechaViaje(), diaSemana, viaje.getRuta().getDiasOperacion())
                );
            }

            viaje.setFechaViaje(updateDTO.getFechaViaje());
        }

        // Actualizar hora si se proporciona
        if (updateDTO.hasHoraEmbarque()) {
            log.debug("Actualizando hora de embarque a: {}", updateDTO.getHoraEmbarque());
            viaje.setHoraEmbarque(updateDTO.getHoraEmbarque());
        }

        // Actualizar cupos si se proporciona
        if (updateDTO.hasCuposDisponibles()) {
            log.debug("Actualizando cupos disponibles a: {}", updateDTO.getCuposDisponibles());

            // Validar que no exceda la capacidad de la embarcación
            if (updateDTO.getCuposDisponibles() > viaje.getEmbarcacion().getCapacidad()) {
                throw new BadRequestException(
                        String.format("Los cupos disponibles (%d) no pueden exceder la capacidad de la embarcación (%d)",
                                updateDTO.getCuposDisponibles(), viaje.getEmbarcacion().getCapacidad())
                );
            }

            viaje.setCuposDisponibles(updateDTO.getCuposDisponibles());
        }

        // Actualizar estado si se proporciona
        if (updateDTO.hasEstado()) {
            log.debug("Actualizando estado a: {}", updateDTO.getEstado());

            // Validar transiciones de estado
            validarTransicionEstado(viaje.getEstado(), updateDTO.getEstado());

            viaje.setEstado(updateDTO.getEstado());
        }

        // Guardar cambios
        Viaje viajeActualizado = viajeRepository.save(viaje);
        log.info("Viaje actualizado exitosamente con ID: {}", idViaje);

        // Convertir a DTO y retornar
        return ViajeMapper.toResponseDTO(viajeActualizado);
    }

    /**
     * Valida que la transición de estado sea válida
     * */
    private void validarTransicionEstado(EstadoViaje estadoActual, EstadoViaje nuevoEstado) {
        if (estadoActual == nuevoEstado) {
            return; // Mismo estado, no hay problema
        }

        // Definir transiciones válidas
        boolean transicionValida = switch (estadoActual) {
            case PROGRAMADO -> nuevoEstado == EstadoViaje.EN_CURSO ||
                    nuevoEstado == EstadoViaje.CANCELADO;
            case EN_CURSO -> nuevoEstado == EstadoViaje.COMPLETADO;
            case COMPLETADO, CANCELADO -> false; // Estados finales, no se pueden cambiar
        };

        if (!transicionValida) {
            throw new BadRequestException(
                    String.format("Transición de estado no válida: %s -> %s", estadoActual, nuevoEstado)
            );
        }
    }

    /**
     * Obtiene el código de día (LUN, MAR, etc.) desde DayOfWeek
     */
    private String obtenerCodigoDia(DayOfWeek diaSemana) {
        return switch (diaSemana) {
            case MONDAY -> "LUN";
            case TUESDAY -> "MAR";
            case WEDNESDAY -> "MIE";
            case THURSDAY -> "JUE";
            case FRIDAY -> "VIE";
            case SATURDAY -> "SAB";
            case SUNDAY -> "DOM";
        };
    }
}