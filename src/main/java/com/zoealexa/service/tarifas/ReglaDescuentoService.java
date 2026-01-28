package com.zoealexa.service.tarifas;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zoealexa.dto.tarifas.ReglaDescuentoRequestDTO;
import com.zoealexa.dto.tarifas.ReglaDescuentoResponseDTO;
import com.zoealexa.dto.tarifas.UpdateReglaDescuentoRequestDTO;
import com.zoealexa.entity.tarifas.ReglaDescuento;
import com.zoealexa.exception.BadRequestException;
import com.zoealexa.exception.NotFoundException;
import com.zoealexa.mapper.tarifas.ReglaDescuentoMapper;
import com.zoealexa.repository.tarifas.ReglaDescuentoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de Reglas de Descuento
 *
 * Funcionalidades:
 * - Crear regla de descuento
 * - Listar reglas
 * - Buscar por ID
 * - Buscar reglas aplicables por edad
 * - Actualizar regla
 * - Activar/Desactivar regla
 * - Calcular descuento
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReglaDescuentoService {

    private final ReglaDescuentoRepository reglaDescuentoRepository;

    /**
     * Crear nueva regla de descuento
     *
     * @param request Datos de la regla
     * @return Regla creada
     */
    public ReglaDescuentoResponseDTO crear(ReglaDescuentoRequestDTO request) {
        log.info("Creando regla de descuento: {}", request.getDescripcion());

        // Validar rangos de edad
        validarRangoEdad(request.getEdadMinima(), request.getEdadMaxima());

        ReglaDescuento regla = ReglaDescuentoMapper.toEntity(request);
        regla = reglaDescuentoRepository.save(regla);

        log.info("Regla de descuento creada con ID: {}", regla.getIdDescuento());

        return ReglaDescuentoMapper.toResponseDTO(regla);
    }

    /**
     * Listar todas las reglas de descuento
     *
     * @return Lista de reglas
     */
    @Transactional(readOnly = true)
    public List<ReglaDescuentoResponseDTO> listarTodas() {
        log.debug("Listando todas las reglas de descuento");

        return reglaDescuentoRepository.findAllOrderByFechaCreacionDesc().stream()
                .map(ReglaDescuentoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar reglas activas
     *
     * @return Lista de reglas activas
     */
    @Transactional(readOnly = true)
    public List<ReglaDescuentoResponseDTO> listarActivas() {
        log.debug("Listando reglas de descuento activas");

        return reglaDescuentoRepository.findByActivaTrue().stream()
                .map(ReglaDescuentoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar regla por ID
     *
     * @param id ID de la regla
     * @return Regla encontrada
     */
    @Transactional(readOnly = true)
    public ReglaDescuentoResponseDTO buscarPorId(Integer id) {
        log.debug("Buscando regla de descuento por ID: {}", id);

        ReglaDescuento regla = reglaDescuentoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Regla de descuento no encontrada con ID: " + id));

        return ReglaDescuentoMapper.toResponseDTO(regla);
    }

    /**
     * Buscar reglas aplicables para una edad específica
     *
     * @param edad Edad del pasajero
     * @return Lista de reglas aplicables
     */
    @Transactional(readOnly = true)
    public List<ReglaDescuentoResponseDTO> buscarReglasParaEdad(int edad) {
        log.debug("Buscando reglas de descuento para edad: {}", edad);

        if (edad < 0) {
            throw new BadRequestException("La edad no puede ser negativa");
        }

        return reglaDescuentoRepository.findReglasAplicablesParaEdad(edad).stream()
                .map(ReglaDescuentoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar la mejor regla (mayor descuento) para una edad
     *
     * @param edad Edad del pasajero
     * @return Mejor regla o null si no aplica ninguna
     */
    @Transactional(readOnly = true)
    public ReglaDescuentoResponseDTO buscarMejorReglaParaEdad(int edad) {
        log.debug("Buscando mejor regla de descuento para edad: {}", edad);

        if (edad < 0) {
            throw new BadRequestException("La edad no puede ser negativa");
        }

        List<ReglaDescuento> reglas = reglaDescuentoRepository.findMejorReglaParaEdad(edad);

        if (reglas.isEmpty()) {
            return null;
        }

        return ReglaDescuentoMapper.toResponseDTO(reglas.get(0));
    }

    /**
     * Actualizar regla de descuento (actualización parcial)
     *
     * @param id ID de la regla
     * @param request Datos actualizados (todos opcionales)
     * @return Regla actualizada
     */
    public ReglaDescuentoResponseDTO actualizar(Integer id, UpdateReglaDescuentoRequestDTO request) {
        log.info("Actualizando regla de descuento ID: {}", id);

        ReglaDescuento regla = reglaDescuentoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Regla de descuento no encontrada con ID: " + id));

        // Actualizar solo los campos que no son null
        if (request.getDescripcion() != null) {
            regla.setDescripcion(request.getDescripcion());
        }
        if (request.getEdadMinima() != null || request.getEdadMaxima() != null) {
            Integer edadMin = request.getEdadMinima() != null ? request.getEdadMinima() : regla.getEdadMinima();
            Integer edadMax = request.getEdadMaxima() != null ? request.getEdadMaxima() : regla.getEdadMaxima();
            validarRangoEdad(edadMin, edadMax);

            if (request.getEdadMinima() != null) {
                regla.setEdadMinima(request.getEdadMinima());
            }
            if (request.getEdadMaxima() != null) {
                regla.setEdadMaxima(request.getEdadMaxima());
            }
        }
        if (request.getTipoValor() != null) {
            regla.setTipoValor(request.getTipoValor());
        }
        if (request.getValor() != null) {
            regla.setValor(request.getValor());
        }
        if (request.getActiva() != null) {
            regla.setActiva(request.getActiva());
        }

        regla = reglaDescuentoRepository.save(regla);

        log.info("Regla de descuento actualizada: {}", regla.getIdDescuento());

        return ReglaDescuentoMapper.toResponseDTO(regla);
    }

    /**
     * Activar regla de descuento
     *
     * @param id ID de la regla
     * @return Regla activada
     */
    public ReglaDescuentoResponseDTO activar(Integer id) {
        log.info("Activando regla de descuento ID: {}", id);

        ReglaDescuento regla = reglaDescuentoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Regla de descuento no encontrada con ID: " + id));

        regla.setActiva(true);
        regla = reglaDescuentoRepository.save(regla);

        return ReglaDescuentoMapper.toResponseDTO(regla);
    }

    /**
     * Desactivar regla de descuento
     *
     * @param id ID de la regla
     * @return Regla desactivada
     */
    public ReglaDescuentoResponseDTO desactivar(Integer id) {
        log.info("Desactivando regla de descuento ID: {}", id);

        ReglaDescuento regla = reglaDescuentoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Regla de descuento no encontrada con ID: " + id));

        regla.setActiva(false);
        regla = reglaDescuentoRepository.save(regla);

        return ReglaDescuentoMapper.toResponseDTO(regla);
    }

    /**
     * Calcular descuento para un monto y edad dados
     *
     * @param montoBase Monto base del pasaje
     * @param edad Edad del pasajero
     * @return Monto del descuento
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularDescuento(BigDecimal montoBase, int edad) {
        log.debug("Calculando descuento para monto: {} y edad: {}", montoBase, edad);

        ReglaDescuentoResponseDTO mejorRegla = buscarMejorReglaParaEdad(edad);

        if (mejorRegla == null) {
            log.debug("No se encontró regla aplicable para edad: {}", edad);
            return BigDecimal.ZERO;
        }

        BigDecimal descuento;

        if (mejorRegla.getTipoValor() == com.zoealexa.entity.enums.TipoValor.PORCENTAJE) {
            // Descuento por porcentaje
            descuento = montoBase.multiply(mejorRegla.getValor())
                    .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
        } else {
            // Descuento por monto fijo
            descuento = mejorRegla.getValor();
        }

        log.debug("Descuento calculado: {} (Regla: {})", descuento, mejorRegla.getDescripcion());

        return descuento;
    }

    /**
     * Contar reglas activas
     *
     * @return Número de reglas activas
     */
    @Transactional(readOnly = true)
    public Long contarActivas() {
        return reglaDescuentoRepository.contarActivas();
    }

    // ==========================================
    // MÉTODOS PRIVADOS DE VALIDACIÓN
    // ==========================================

    private void validarRangoEdad(Integer edadMinima, Integer edadMaxima) {
        if (edadMinima != null && edadMaxima != null) {
            if (edadMinima > edadMaxima) {
                throw new BadRequestException("La edad mínima no puede ser mayor que la edad máxima");
            }
        }
    }
}