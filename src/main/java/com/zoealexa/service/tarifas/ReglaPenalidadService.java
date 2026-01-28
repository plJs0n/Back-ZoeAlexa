package com.zoealexa.service.tarifas;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zoealexa.dto.tarifas.ReglaPenalidadRequestDTO;
import com.zoealexa.dto.tarifas.ReglaPenalidadResponseDTO;
import com.zoealexa.dto.tarifas.UpdateReglaPenalidadRequestDTO;
import com.zoealexa.entity.enums.TipoPenalidad;
import com.zoealexa.entity.tarifas.ReglaPenalidad;
import com.zoealexa.exception.BadRequestException;
import com.zoealexa.exception.NotFoundException;
import com.zoealexa.mapper.tarifas.ReglaPenalidadMapper;
import com.zoealexa.repository.tarifas.ReglaPenalidadRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de Reglas de Penalidad
 *
 * Funcionalidades:
 * - Crear regla de penalidad
 * - Listar reglas
 * - Buscar por ID y tipo
 * - Actualizar regla
 * - Activar/Desactivar regla
 * - Calcular penalidades
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReglaPenalidadService {

    private final ReglaPenalidadRepository reglaPenalidadRepository;

    /**
     * Crear nueva regla de penalidad
     *
     * @param request Datos de la regla
     * @return Regla creada
     */
    public ReglaPenalidadResponseDTO crear(ReglaPenalidadRequestDTO request) {
        log.info("Creando regla de penalidad tipo: {}", request.getTipoPenalidad());

        // Validar campos según tipo de penalidad
        validarCamposSegunTipo(request);

        ReglaPenalidad regla = ReglaPenalidadMapper.toEntity(request);
        regla = reglaPenalidadRepository.save(regla);

        log.info("Regla de penalidad creada con ID: {}", regla.getIdPenalidad());

        return ReglaPenalidadMapper.toResponseDTO(regla);
    }

    /**
     * Listar todas las reglas de penalidad
     *
     * @return Lista de reglas
     */
    @Transactional(readOnly = true)
    public List<ReglaPenalidadResponseDTO> listarTodas() {
        log.debug("Listando todas las reglas de penalidad");

        return reglaPenalidadRepository.findAllOrderByFechaCreacionDesc().stream()
                .map(ReglaPenalidadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar reglas activas
     *
     * @return Lista de reglas activas
     */
    @Transactional(readOnly = true)
    public List<ReglaPenalidadResponseDTO> listarActivas() {
        log.debug("Listando reglas de penalidad activas");

        return reglaPenalidadRepository.findByActivaTrue().stream()
                .map(ReglaPenalidadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar regla por ID
     *
     * @param id ID de la regla
     * @return Regla encontrada
     */
    @Transactional(readOnly = true)
    public ReglaPenalidadResponseDTO buscarPorId(Integer id) {
        log.debug("Buscando regla de penalidad por ID: {}", id);

        ReglaPenalidad regla = reglaPenalidadRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Regla de penalidad no encontrada con ID: " + id));

        return ReglaPenalidadMapper.toResponseDTO(regla);
    }

    /**
     * Buscar reglas por tipo de penalidad
     *
     * @param tipo Tipo de penalidad
     * @return Lista de reglas del tipo especificado
     */
    @Transactional(readOnly = true)
    public List<ReglaPenalidadResponseDTO> buscarPorTipo(TipoPenalidad tipo) {
        log.debug("Buscando reglas de penalidad por tipo: {}", tipo);

        return reglaPenalidadRepository.findByTipoPenalidad(tipo).stream()
                .map(ReglaPenalidadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar reglas activas por tipo
     *
     * @param tipo Tipo de penalidad
     * @return Lista de reglas activas del tipo
     */
    @Transactional(readOnly = true)
    public List<ReglaPenalidadResponseDTO> buscarActivasPorTipo(TipoPenalidad tipo) {
        log.debug("Buscando reglas activas de tipo: {}", tipo);

        return reglaPenalidadRepository.findByTipoPenalidadActiva(tipo).stream()
                .map(ReglaPenalidadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar regla de equipaje activa
     *
     * @return Regla de equipaje o null si no existe
     */
    @Transactional(readOnly = true)
    public ReglaPenalidadResponseDTO buscarReglaEquipaje() {
        log.debug("Buscando regla de equipaje activa");

        return reglaPenalidadRepository.findReglaEquipajeActiva()
                .map(ReglaPenalidadMapper::toResponseDTO)
                .orElse(null);
    }

    /**
     * Actualizar regla de penalidad (actualización parcial)
     *
     * @param id ID de la regla
     * @param request Datos actualizados (todos opcionales)
     * @return Regla actualizada
     */
    public ReglaPenalidadResponseDTO actualizar(Integer id, UpdateReglaPenalidadRequestDTO request) {
        log.info("Actualizando regla de penalidad ID: {}", id);

        ReglaPenalidad regla = reglaPenalidadRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Regla de penalidad no encontrada con ID: " + id));

        // Actualizar solo los campos que no son null
        if (request.getDescripcion() != null) {
            regla.setDescripcion(request.getDescripcion());
        }

        if (regla.getTipoPenalidad() == TipoPenalidad.EQUIPAJE){

        }
        if (request.getTipoValor() != null) {
            if (regla.getTipoPenalidad() == TipoPenalidad.EQUIPAJE){
                throw new BadRequestException("Para penalidad de EQUIPAJE, tipoValor debe ser null");
            }else {
                regla.setTipoValor(request.getTipoValor());
            }
        }
        if (request.getValor() != null) {
            if (regla.getTipoPenalidad() == TipoPenalidad.EQUIPAJE){
                throw new BadRequestException("Para penalidad de EQUIPAJE, valor debe ser null");
            }else {
                regla.setValor(request.getValor());
            }
        }
        if (request.getKilosPermitidos() != null) {
            if (regla.getTipoPenalidad() != TipoPenalidad.EQUIPAJE){
                throw new BadRequestException("kilosPermitidos y precioPorKilo solo aplica para EQUIPAJE");
            }else {
                regla.setKilosPermitidos(request.getKilosPermitidos());
            }
        }
        if (request.getPrecioPorKilo() != null) {
            if (regla.getTipoPenalidad() != TipoPenalidad.EQUIPAJE){
                throw new BadRequestException("PrecioPorKilo solo aplica para EQUIPAJE");
            }else {
                regla.setPrecioPorKilo(request.getPrecioPorKilo());
            }
        }
        if (request.getActiva() != null) {
            regla.setActiva(request.getActiva());
        }

        regla = reglaPenalidadRepository.save(regla);

        log.info("Regla de penalidad actualizada: {}", regla.getIdPenalidad());

        return ReglaPenalidadMapper.toResponseDTO(regla);
    }

    /**
     * Activar regla de penalidad
     *
     * @param id ID de la regla
     * @return Regla activada
     */
    public ReglaPenalidadResponseDTO activar(Integer id) {
        log.info("Activando regla de penalidad ID: {}", id);

        ReglaPenalidad regla = reglaPenalidadRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Regla de penalidad no encontrada con ID: " + id));

        regla.setActiva(true);
        regla = reglaPenalidadRepository.save(regla);

        return ReglaPenalidadMapper.toResponseDTO(regla);
    }

    /**
     * Desactivar regla de penalidad
     *
     * @param id ID de la regla
     * @return Regla desactivada
     */
    public ReglaPenalidadResponseDTO desactivar(Integer id) {
        log.info("Desactivando regla de penalidad ID: {}", id);

        ReglaPenalidad regla = reglaPenalidadRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Regla de penalidad no encontrada con ID: " + id));

        regla.setActiva(false);
        regla = reglaPenalidadRepository.save(regla);

        return ReglaPenalidadMapper.toResponseDTO(regla);
    }

    /**
     * Calcular penalidad por cancelación
     *
     * @param montoTotal Monto total de la reserva
     * @return Monto de la penalidad
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularPenalidadCancelacion(BigDecimal montoTotal) {
        log.debug("Calculando penalidad por cancelación para monto: {}", montoTotal);

        List<ReglaPenalidad> reglas = reglaPenalidadRepository.findReglasCancelacionActivas();

        if (reglas.isEmpty()) {
            log.warn("No se encontró regla de cancelación activa");
            return BigDecimal.ZERO;
        }

        return calcularPenalidad(reglas.get(0), montoTotal);
    }

    /**
     * Calcular penalidad por reprogramación
     *
     * @param montoTotal Monto total de la reserva
     * @return Monto de la penalidad
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularPenalidadReprogramacion(BigDecimal montoTotal) {
        log.debug("Calculando penalidad por reprogramación para monto: {}", montoTotal);

        List<ReglaPenalidad> reglas = reglaPenalidadRepository.findReglasReprogramacionActivas();

        if (reglas.isEmpty()) {
            log.warn("No se encontró regla de reprogramación activa");
            return BigDecimal.ZERO;
        }

        return calcularPenalidad(reglas.get(0), montoTotal);
    }

    /**
     * Calcular costo de equipaje excedente
     *
     * @param pesoKg Peso del equipaje en kilogramos
     * @return Costo del exceso de equipaje
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularCostoEquipaje(BigDecimal pesoKg) {
        log.debug("Calculando costo de equipaje para peso: {} kg", pesoKg);

        ReglaPenalidad regla = reglaPenalidadRepository.findReglaEquipajeActiva()
                .orElseThrow(() -> new NotFoundException("No se encontró regla de equipaje activa"));

        // Calcular exceso de kilos
        BigDecimal kilosPermitidos = new BigDecimal(regla.getKilosPermitidos());
        BigDecimal exceso = pesoKg.subtract(kilosPermitidos);

        if (exceso.compareTo(BigDecimal.ZERO) <= 0) {
            log.debug("Equipaje dentro del límite permitido");
            return BigDecimal.ZERO;
        }

        // Costo = exceso * precio por kilo
        BigDecimal costo = exceso.multiply(regla.getPrecioPorKilo())
                .setScale(2, BigDecimal.ROUND_HALF_UP);

        log.debug("Costo de equipaje excedente: {}", costo);

        return costo;
    }

    /**
     * Contar reglas activas
     *
     * @return Número de reglas activas
     */
    @Transactional(readOnly = true)
    public Long contarActivas() {
        return reglaPenalidadRepository.contarActivas();
    }

    // ==========================================
    // MÉTODOS PRIVADOS
    // ==========================================

    /**
     * Validar campos según tipo de penalidad
     */
    private void validarCamposSegunTipo(ReglaPenalidadRequestDTO request) {
        if (request.getTipoPenalidad() == TipoPenalidad.EQUIPAJE) {
            // Para EQUIPAJE: Requerir kilosPermitidos y precioPorKilo
            if (request.getKilosPermitidos() == null) {
                throw new BadRequestException("Para penalidad de EQUIPAJE, kilosPermitidos es obligatorio");
            }
            if (request.getPrecioPorKilo() == null) {
                throw new BadRequestException("Para penalidad de EQUIPAJE, precioPorKilo es obligatorio");
            }
            // tipoValor y valor deben ser null
            if (request.getTipoValor() != null || request.getValor() != null) {
                throw new BadRequestException("Para penalidad de EQUIPAJE, tipoValor y valor deben ser null");
            }
        } else {
            // Para otros tipos: Requerir tipoValor y valor
            if (request.getTipoValor() == null) {
                throw new BadRequestException("Para este tipo de penalidad, tipoValor es obligatorio");
            }
            if (request.getValor() == null) {
                throw new BadRequestException("Para este tipo de penalidad, valor es obligatorio");
            }
            // kilosPermitidos y precioPorKilo deben ser null
            if (request.getKilosPermitidos() != null || request.getPrecioPorKilo() != null) {
                throw new BadRequestException("kilosPermitidos y precioPorKilo solo aplican para EQUIPAJE");
            }
        }
    }

    /**
     * Calcular penalidad según regla
     */
    private BigDecimal calcularPenalidad(ReglaPenalidad regla, BigDecimal montoTotal) {
        BigDecimal penalidad;

        if (regla.getTipoValor() == com.zoealexa.entity.enums.TipoValor.PORCENTAJE) {
            // Penalidad por porcentaje
            penalidad = montoTotal.multiply(regla.getValor())
                    .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
        } else {
            // Penalidad por monto fijo
            penalidad = regla.getValor();
        }

        log.debug("Penalidad calculada: {} (Regla: {})", penalidad, regla.getDescripcion());

        return penalidad;
    }
}