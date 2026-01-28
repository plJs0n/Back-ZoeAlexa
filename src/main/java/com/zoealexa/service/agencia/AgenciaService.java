package com.zoealexa.service.agencia;

import com.zoealexa.dto.agencia.*;

import com.zoealexa.entity.enums.EstadoAgencia;
import com.zoealexa.entity.seguridad.Agencia;
import com.zoealexa.exception.ConflictException;
import com.zoealexa.exception.RecursoNoEncontradoException;
import com.zoealexa.exception.ValidacionNegocioException;
import com.zoealexa.mapper.agencia.AgenciaMapper;
import com.zoealexa.repository.seguridad.AgenciaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestión de Agencias
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AgenciaService {

    private final AgenciaRepository agenciaRepository;
    private final AgenciaMapper agenciaMapper;

    /**
     * Crear una nueva agencia
     */
    @Transactional
    public AgenciaResponse crearAgencia(CrearAgenciaRequest request) {
        log.info("Creando nueva agencia: {}", request.getNombreAgencia());

        // 1. Validar que el RUC no exista
        if (agenciaRepository.existsByRuc(request.getRuc())) {
            throw new ConflictException("Ya existe una agencia con el mismo RUC");
        }

        // 2. Crear entidad Agencia
        Agencia agencia = Agencia.builder()
                .nombreAgencia(request.getNombreAgencia())
                .ruc(request.getRuc())
                .direccion(request.getDireccion())
                .telefono(request.getTelefono())
                .tipoComision(request.getTipoComision())
                .valorComision(request.getValorComision())
                .estado(request.getEstado() != null ? request.getEstado() : EstadoAgencia.ACTIVO)
                .build();

        // 3. Guardar
        agencia = agenciaRepository.save(agencia);

        log.info("Agencia creada exitosamente con ID: {}", agencia.getIdAgencia());

        return agenciaMapper.toResponse(agencia);
    }

    /**
     * Actualizar una agencia existente
     */
    @Transactional
    public AgenciaResponse actualizarAgencia(Integer idAgencia, ActualizarAgenciaRequest request) {
        log.info("Actualizando agencia ID: {}", idAgencia);

        // 1. Buscar agencia
        Agencia agencia = agenciaRepository.findById(idAgencia)
                .orElseThrow(() -> new RecursoNoEncontradoException("Agencia", idAgencia.toString()));

        // 2. Validar que la agencia esté activa
        if (agencia.getEstado() == EstadoAgencia.INACTIVO) {
            throw new ValidacionNegocioException(
                    "No se puede actualizar una agencia inactiva. Active la agencia primero."
            );
        }

        // 3. Actualizar campos
        if(request.getNombreAgencia() != null){
            agencia.setNombreAgencia(request.getNombreAgencia());
        }
        if (request.getDireccion() != null){
            agencia.setDireccion(request.getDireccion());
        }
        if (request.getTelefono() != null){
            agencia.setTelefono(request.getTelefono());
        }
        if (request.getTipoComision() != null){
            agencia.setTipoComision(request.getTipoComision());
        }
        if (request.getValorComision() != null){
            agencia.setValorComision(request.getValorComision());
        }

        // 4. Guardar
        agencia = agenciaRepository.save(agencia);

        log.info("Agencia actualizada exitosamente: {}", agencia.getIdAgencia());

        return agenciaMapper.toResponse(agencia);
    }

    /**
     * Cambiar estado de una agencia (Activar/Desactivar)
     */
    @Transactional
    public AgenciaResponse cambiarEstado(Integer idAgencia, CambiarEstadoAgenciaRequest request) {
        log.info("Cambiando estado de agencia ID: {} a {}", idAgencia, request.getEstado());

        // 1. Buscar agencia
        Agencia agencia = agenciaRepository.findById(idAgencia)
                .orElseThrow(() -> new RecursoNoEncontradoException("Agencia", idAgencia.toString()));

        // 2. Validar que el estado sea diferente
        if (agencia.getEstado() == request.getEstado()) {
            throw new ConflictException(
                    String.format("La agencia ya está en estado: %s", request.getEstado())
            );
        }

        // 3. Si se va a desactivar, validar que no tenga usuarios activos
        if (request.getEstado() == EstadoAgencia.INACTIVO) {
            long usuariosActivos = agencia.getUsuarios().stream()
                    .filter(u -> "ACTIVO".equals(u.getEstado().name()))
                    .count();

            if (usuariosActivos > 0) {
                throw new ConflictException("No se puede desactivar la agencia. Tiene usuarios activos");
            }
        }

        // 4. Cambiar estado
        agencia.setEstado(request.getEstado());
        agencia = agenciaRepository.save(agencia);

        log.info("Estado de agencia cambiado exitosamente. Motivo: {}", request.getMotivo());

        return agenciaMapper.toResponse(agencia);
    }
//
//    /**
//     * Buscar agencia por ID
//     */
//    @Transactional(readOnly = true)
//    public AgenciaResponse buscarPorId(Integer idAgencia) {
//        log.debug("Buscando agencia por ID: {}", idAgencia);
//
//        Agencia agencia = agenciaRepository.findById(idAgencia)
//                .orElseThrow(() -> new RecursoNoEncontradoException("Agencia", idAgencia.toString()));
//
//        return agenciaMapper.toResponse(agencia);
//    }
//
//    /**
//     * Buscar agencia por RUC
//     */
//    @Transactional(readOnly = true)
//    public AgenciaResponse buscarPorRuc(String ruc) {
//        log.debug("Buscando agencia por RUC: {}", ruc);
//
//        Agencia agencia = agenciaRepository.findByRuc(ruc)
//                .orElseThrow(() -> new RecursoNoEncontradoException("Agencia con RUC", ruc));
//
//        return agenciaMapper.toResponse(agencia);
//    }
//
    /**
     * Listar todas las agencias
     */
    @Transactional(readOnly = true)
    public List<AgenciaSimpleResponse> listarTodas() {
        log.debug("Listando todas las agencias");

        List<Agencia> agencias = agenciaRepository.findAll();
        return agenciaMapper.toSimpleResponseList(agencias);
    }
//
//    /**
//     * Listar solo agencias activas
//     */
//    @Transactional(readOnly = true)
//    public List<AgenciaSimpleResponse> listarActivas() {
//        log.debug("Listando agencias activas");
//
//        List<Agencia> agencias = agenciaRepository.findAgenciasActivas();
//        return agenciaMapper.toSimpleResponseList(agencias);
//    }

    /**
     * Listar agencias por estado
     */
    @Transactional(readOnly = true)
    public List<AgenciaSimpleResponse> listarPorEstado(EstadoAgencia estado) {
        log.debug("Listando agencias con estado: {}", estado);

        List<Agencia> agencias = agenciaRepository.findByEstado(estado);
        return agenciaMapper.toSimpleResponseList(agencias);
    }
//
//    /**
//     * Buscar agencias por nombre (búsqueda parcial)
//     */
//    @Transactional(readOnly = true)
//    public List<AgenciaSimpleResponse> buscarPorNombre(String nombre) {
//        log.debug("Buscando agencias por nombre: {}", nombre);
//
//        List<Agencia> agencias = agenciaRepository.findByNombreAgenciaContainingIgnoreCase(nombre);
//        return agenciaMapper.toSimpleResponseList(agencias);
//    }

    /**
     * Validar si un RUC ya existe
     */
    @Transactional(readOnly = true)
    public boolean existeRuc(String ruc) {
        return agenciaRepository.existsByRuc(ruc);
    }
}
