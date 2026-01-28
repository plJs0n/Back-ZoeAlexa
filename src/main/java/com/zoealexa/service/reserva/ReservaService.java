package com.zoealexa.service.reserva;

import com.zoealexa.dto.reserva.*;
import com.zoealexa.dto.reserva.response.CancelacionReprogramacionResponse;
import com.zoealexa.dto.reserva.response.ReservaResponse;
import com.zoealexa.dto.reserva.response.ReservaSimpleResponse;
import com.zoealexa.entity.enums.EstadoPago;
import com.zoealexa.entity.enums.EstadoReserva;
import com.zoealexa.entity.enums.EstadoViaje;
import com.zoealexa.entity.enums.TipoOperacionCancelacion;
import com.zoealexa.entity.equipaje.Equipaje;
import com.zoealexa.entity.pagos.Pago;
import com.zoealexa.entity.reservas.CancelacionReprogramacion;
import com.zoealexa.entity.reservas.Pasajero;
import com.zoealexa.entity.reservas.Reserva;
import com.zoealexa.entity.reservas.ReservaDetalle;
import com.zoealexa.entity.seguridad.Agencia;
import com.zoealexa.entity.seguridad.Usuario;
import com.zoealexa.entity.tarifas.TarifaRuta;
import com.zoealexa.entity.transporte.Viaje;
import com.zoealexa.exception.*;
import com.zoealexa.mapper.reserva.ReservaMapper;
import com.zoealexa.repository.pago.CancelacionReprogramacionRepository;
import com.zoealexa.repository.pago.PagoRepository;
import com.zoealexa.repository.reservas.CodigoGeneradorService;
import com.zoealexa.repository.reservas.EquipajeRepository;
import com.zoealexa.repository.reservas.ReservaDetalleRepository;
import com.zoealexa.repository.reservas.ReservaRepository;
import com.zoealexa.repository.seguridad.AgenciaRepository;
import com.zoealexa.repository.seguridad.PasajeroRepository;
import com.zoealexa.repository.seguridad.UsuarioRepository;
import com.zoealexa.repository.tarifas.TarifaRutaRepository;
import com.zoealexa.repository.transporte.ViajeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio principal para gestión de reservas
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ReservaService {

    // Repositories
    private final ReservaRepository reservaRepository;
    private final ViajeRepository viajeRepository;
    private final PasajeroRepository pasajeroRepository;
    private final ReservaDetalleRepository reservaDetalleRepository;
    private final EquipajeRepository equipajeRepository;
    private final PagoRepository pagoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AgenciaRepository agenciaRepository;
    private final TarifaRutaRepository tarifaRutaRepository;
    private final CancelacionReprogramacionRepository cancelacionRepository;

    // Services
    private final CalculoService calculoService;
    private final CodigoGeneradorService codigoGeneradorService;
    private final AutorizacionService autorizacionService;

    // Mapper
    private final ReservaMapper reservaMapper;

    /**
     * Crea una nueva reserva
     */
    @Transactional
    public ReservaResponse crearReserva(CrearReservaRequest request, Integer idUsuarioActual) {
        log.info("Iniciando creación de reserva para viaje ID: {}", request.getIdViaje());

        // 0. VALIDAR que la agencia solo pueda crear reservas para sí misma
        autorizacionService.validarCreacionReserva(request.getIdAgencia());

        // 1. Validar y obtener viaje
        Viaje viaje = viajeRepository.findById(request.getIdViaje())
                .orElseThrow(() -> new RecursoNoEncontradoException("Viaje", request.getIdViaje().toString()));

        validarViajeDisponible(viaje);

        // 2. Validar cupos disponibles
        int cantidadPasajeros = request.getPasajeros().size();
        if (viaje.getCuposDisponibles() < cantidadPasajeros) {
            throw new ConflictException("Cupos disponibles insuficientes");
        }

        // 3. Obtener usuario y agencia
        Usuario usuario = usuarioRepository.findById(idUsuarioActual)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", idUsuarioActual.toString()));

        Agencia agencia = null;
        if (request.getIdAgencia() != null) {
            agencia = agenciaRepository.findById(request.getIdAgencia())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Agencia", request.getIdAgencia().toString()));
        }

        // 4. Obtener precio base de la ruta
        TarifaRuta tarifa = tarifaRutaRepository.findByRutaAndFechaFinIsNull(viaje.getRuta())
                .orElseThrow(() -> new ConflictException("No hay tarifa activa para esta ruta"));

        BigDecimal precioBase = tarifa.getPrecioBase();

        // 5. Crear reserva
        Reserva reserva = Reserva.builder()
                .codigoReserva(codigoGeneradorService.generarCodigoReserva())
                .viaje(viaje)
                .usuario(usuario)
                .agencia(agencia)
                .origen(viaje.getRuta().getPuertoOrigen().getNombrePuerto())
                .destino(viaje.getRuta().getPuertoDestino().getNombrePuerto())
                .total(BigDecimal.ZERO) // Se calculará después
                .montoPagado(BigDecimal.ZERO)
                .saldoPendiente(BigDecimal.ZERO)
                .estado(EstadoReserva.PENDIENTE)
                .build();

        // 6. Procesar cada pasajero
        BigDecimal totalReserva = BigDecimal.ZERO;
        List<ReservaDetalle> detalles = new ArrayList<>();

        for (PasajeroReservaRequest pasajeroReq : request.getPasajeros()) {
            // 6.1 Obtener o crear pasajero
            Pasajero pasajero = obtenerOCrearPasajero(pasajeroReq);

            // 6.2 Validar que no esté duplicado en esta reserva
            if (existePasajeroEnReserva(detalles, pasajero)) {
                throw new PasajeroDuplicadoException(pasajero.getNombreCompleto());
            }

            // 6.3 Calcular precio con descuento
            CalculoService.ResultadoDescuento resultado = calculoService.aplicarDescuentoPorEdad(
                    pasajero, precioBase, viaje.getFechaViaje()
            );

            // 6.4 Crear detalle de reserva
            ReservaDetalle detalle = ReservaDetalle.builder()
                    .reserva(reserva)
                    .pasajero(pasajero)
                    .tipoTarifa(resultado.getTipoTarifa())
                    .precioBase(resultado.getPrecioBase())
                    .porcentajeDescuento(resultado.getPorcentajeDescuento())
                    .montoDescuento(resultado.getMontoDescuento())
                    .precioFinal(resultado.getPrecioFinal())
                    .build();

            detalles.add(detalle);
            totalReserva = totalReserva.add(resultado.getPrecioFinal());

        }

        // 7. Actualizar totales de la reserva
        reserva.setTotal(totalReserva);
        reserva.setSaldoPendiente(totalReserva);

        // 8. Calcular comisión si hay agencia
        if (agencia != null) {
            BigDecimal comision = calculoService.calcularComisionAgencia(agencia, totalReserva);
            reserva.setComisionAgencia(comision);
        }

        // 9. Procesar pago inicial si existe
        if (request.getPagoInicial() != null) {
            procesarPagoInicial(reserva, request.getPagoInicial(), request.getDatosFactura());
        }

        // 10. Guardar reserva y detalles
        reserva.getDetalles().addAll(detalles);
        reserva = reservaRepository.save(reserva);

        // 11. Actualizar cupos del viaje
        viaje.setCuposOcupados(viaje.getCuposOcupados() + cantidadPasajeros);
        viaje.setCuposDisponibles(viaje.getCuposDisponibles() - cantidadPasajeros);
        viajeRepository.save(viaje);

        log.info("Reserva creada exitosamente: {}", reserva.getCodigoReserva());

        return reservaMapper.toResponse(reserva);
    }

    /**
     * Busca una reserva por código
     * RESTRICCIÓN: Las agencias solo pueden ver sus propias reservas
     */
    @Transactional(readOnly = true)
    public ReservaResponse buscarPorCodigo(String codigoReserva) {
        Reserva reserva = reservaRepository.findByCodigoReserva(codigoReserva)
                .orElseThrow(() -> new NotFoundException(String.format("Reserva %s no encontrada", codigoReserva)));

        // Validar que el usuario tenga acceso a esta reserva
        autorizacionService.validarAccesoReserva(codigoReserva);

        return reservaMapper.toResponse(reserva);
    }

    /**
     * Lista todas las reservas
     * RESTRICCIÓN: Las agencias solo ven sus propias reservas
     */
    @Transactional(readOnly = true)
    public List<ReservaSimpleResponse> listarReservas() {
        List<Reserva> reservas;

        // Si es AGENCIA, filtrar solo sus reservas
        if (autorizacionService.esAgencia()) {
            Integer idAgencia = autorizacionService.obtenerIdAgenciaActual();
            if (idAgencia == null) {
                log.warn("Usuario AGENCIA sin idAgencia asociado");
                return List.of();
            }
            reservas = reservaRepository.findByAgenciaIdAgencia(idAgencia);
            log.debug("Listando reservas para agencia ID: {}", idAgencia);
        } else {
            // ASESOR_VENTAS ve todas las reservas
            reservas = reservaRepository.findAll();
            log.debug("Listando todas las reservas");
        }

        return reservaMapper.toSimpleResponseList(reservas);
    }

    /**
     * Registra un pago adicional
     */
    @Transactional
    public ReservaResponse registrarPago(RegistrarPagoRequest request, Integer idUsuarioActual) {
        log.info("Registrando pago para reserva: {}", request.getCodigoReserva());

        // Validar acceso a la reserva
        autorizacionService.validarAccesoReserva(request.getCodigoReserva());

        // 1. Obtener reserva
        Reserva reserva = reservaRepository.findByCodigoReserva(request.getCodigoReserva())
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva", request.getCodigoReserva()));

        // 2. Validar estado
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new OperacionNoPermitidaException("No se pueden registrar pagos en reservas canceladas");
        }

        if (reserva.getEstado() == EstadoReserva.COMPLETADA) {
            throw new OperacionNoPermitidaException("Esta reserva ya está completada");
        }

        // 3. Validar que el monto no exceda el saldo pendiente
        if (request.getMonto().compareTo(reserva.getSaldoPendiente()) > 0) {
            throw new ValidacionNegocioException(
                    String.format("El monto (S/ %.2f) excede el saldo pendiente (S/ %.2f)",
                            request.getMonto(), reserva.getSaldoPendiente())
            );
        }

        // 4. Crear y guardar pago
        Pago pago = Pago.builder()
                .reserva(reserva)
                .tipoPago(request.getTipoPago())
                .metodoPago(request.getMetodoPago())
                .monto(request.getMonto())
                .referenciaTransaccion(request.getReferenciaTransaccion())
                .estado(EstadoPago.CONFIRMADO)
                .build();

        pago = pagoRepository.save(pago);

        // 5. Actualizar montos de la reserva
        reserva.setMontoPagado(reserva.getMontoPagado().add(request.getMonto()));
        reserva.calcularSaldoPendiente();

        // 6. Actualizar estado si corresponde
        if (reserva.getSaldoPendiente().compareTo(BigDecimal.ZERO) == 0) {
            reserva.setEstado(EstadoReserva.PAGADA);
        } else if (reserva.tieneAdelantoSuficiente()) {
            reserva.setEstado(EstadoReserva.CONFIRMADA);
        }

        reserva = reservaRepository.save(reserva);

        // 7. Generar comprobante si se proporcionaron datos de factura
        // (implementar según necesidad)

        log.info("Pago registrado exitosamente para reserva: {}", reserva.getCodigoReserva());

        return reservaMapper.toResponse(reserva);
    }

    /**
     * Cancela una reserva
     */
    @Transactional
    public CancelacionReprogramacionResponse cancelarReserva(
            CancelarReservaRequest request, Integer idUsuarioActual) {

        log.info("Iniciando cancelación de reserva: {}", request.getCodigoReserva());

        // Validar acceso a la reserva
        autorizacionService.validarAccesoReserva(request.getCodigoReserva());

        // 1. Obtener reserva
        Reserva reserva = reservaRepository.findByCodigoReserva(request.getCodigoReserva())
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva", request.getCodigoReserva()));

        // 2. Validar que se pueda cancelar
        validarCancelacion(reserva);

        // 3. Calcular penalidad
        CalculoService.ResultadoPenalidad penalidad = calculoService.calcularPenalidad(
                reserva, TipoOperacionCancelacion.CANCELACION
        );

        // 4. Obtener usuario
        Usuario usuario = usuarioRepository.findById(idUsuarioActual)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", idUsuarioActual.toString()));

        // 5. Crear registro de cancelación
        CancelacionReprogramacion cancelacion = CancelacionReprogramacion.builder()
                .reserva(reserva)
                .tipoOperacion(TipoOperacionCancelacion.CANCELACION)
                .viajeOriginal(reserva.getViaje())
                .montoOriginal(reserva.getMontoPagado())
                .porcentajePenalidad(penalidad.getPorcentajePenalidad())
                .montoPenalidad(penalidad.getMontoPenalidad())
                .montoDevolver(penalidad.getMontoDevolver())
                .motivo(request.getMotivo())
                .usuario(usuario)
                .build();

        cancelacion = cancelacionRepository.save(cancelacion);

        // 6. Actualizar reserva
        reserva.setEstado(EstadoReserva.CANCELADA);
        reserva.setPenalidadAplicada(penalidad.getMontoPenalidad());
        reserva = reservaRepository.save(reserva);

        // 7. Liberar cupos del viaje
        Viaje viaje = reserva.getViaje();
        int cantidadPasajeros = reserva.getDetalles().size();
        viaje.setCuposOcupados(viaje.getCuposOcupados() - cantidadPasajeros);
        viaje.setCuposDisponibles(viaje.getCuposDisponibles() + cantidadPasajeros);
        viajeRepository.save(viaje);

        log.info("Reserva cancelada exitosamente: {}", reserva.getCodigoReserva());

        return reservaMapper.toCancelacionResponse(cancelacion);
    }

    /**
     * Registra equipaje para un pasajero en una reserva existente
     * Se hace al momento del embarque
     */
    @Transactional
    public ReservaResponse registrarEquipaje(
            String codigoReserva,
            Long idPasajero,
            EquipajeRequest equipajeRequest,
            Integer idUsuarioActual) {

        log.info("Registrando equipaje para reserva: {}, pasajero: {}", codigoReserva, idPasajero);

        // Validar acceso a la reserva
        autorizacionService.validarAccesoReserva(codigoReserva);

        // 1. Obtener reserva
        Reserva reserva = reservaRepository.findByCodigoReserva(codigoReserva)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva", codigoReserva));

        // 2. Validar que la reserva esté en estado válido
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new OperacionNoPermitidaException("No se puede registrar equipaje en reservas canceladas");
        }

        // 3. Obtener el pasajero
        Pasajero pasajero = pasajeroRepository.findById(idPasajero)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pasajero", idPasajero.toString()));

        // 4. Verificar que el pasajero esté en la reserva
        ReservaDetalle detalle = reservaDetalleRepository.findByReservaIdReservaAndPasajeroIdPasajero(
                reserva.getIdReserva(), idPasajero
        ).orElseThrow(() -> new ValidacionNegocioException(
                "El pasajero no está asociado a esta reserva"
        ));

        // 5. Verificar que no tenga equipaje ya registrado
        if (equipajeRepository.findByReservaIdReservaAndPasajeroIdPasajero(
                reserva.getIdReserva(), idPasajero).isPresent()) {
            throw new ValidacionNegocioException(
                    "El pasajero ya tiene equipaje registrado en esta reserva"
            );
        }

        // 6. Calcular costo del equipaje
        CalculoService.ResultadoEquipaje resultado = calculoService.calcularCostoEquipaje(
                equipajeRequest.getPesoKg()
        );

        // 7. Crear el equipaje
        Equipaje equipaje = Equipaje.builder()
                .reserva(reserva)
                .reservaDetalle(detalle)
                .pasajero(pasajero)
                .pesoKg(equipajeRequest.getPesoKg())
                .limiteIncluido(resultado.getLimiteIncluido())
                .pesoExcedenteKg(resultado.getPesoExcedente())
                .volumenM3(equipajeRequest.getVolumenM3())
                .precioPorKilo(resultado.getPrecioPorKilo())
                .costoExceso(resultado.getCostoExceso())
                .descripcion(equipajeRequest.getDescripcion())
                .build();

        equipaje = equipajeRepository.save(equipaje);

        // 8. Si hay exceso, se cobrará por separado (boleta independiente)
        if (equipaje.tieneExceso()) {
            log.info("Equipaje con exceso detectado: {} kg, costo: S/ {}",
                    equipaje.getPesoExcedenteKg(), equipaje.getCostoExceso());
            // TODO: Aquí se generaría la boleta por exceso de equipaje
            // Se implementará en el módulo de comprobantes
        }

        log.info("Equipaje registrado exitosamente - ID: {}", equipaje.getIdEquipaje());

        // 9. Retornar la reserva actualizada
        return reservaMapper.toResponse(reserva);
    }

    // ===== MÉTODOS AUXILIARES PRIVADOS =====

    private void validarViajeDisponible(Viaje viaje) {
        if (viaje.getEstado() == EstadoViaje.CANCELADO) {
            throw new ConflictException("El viaje está cancelado");
        }

        if (viaje.getEstado() == EstadoViaje.COMPLETADO) {
            throw new ConflictException("El viaje ya fue completado");
        }

        if (viaje.getFechaViaje().isBefore(LocalDate.now())) {
            throw new ConflictException("No se pueden crear reservas para viajes pasados");
        }
    }

    private Pasajero obtenerOCrearPasajero(PasajeroReservaRequest request) {
        // Si viene el ID, buscar el pasajero existente
        if (request.getIdPasajero() != null) {
            return pasajeroRepository.findById(request.getIdPasajero())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Pasajero", request.getIdPasajero().toString()));
        }

        // Si es nuevo, verificar que no exista por documento
        if (pasajeroRepository.existsByTipoDocumentoAndNumeroDocumento(
                request.getTipoDocumento(), request.getNumeroDocumento())) {

            // Si existe, retornarlo
            return pasajeroRepository.findByTipoDocumentoAndNumeroDocumento(
                    request.getTipoDocumento(), request.getNumeroDocumento()
            ).orElseThrow();
        }

        // Crear nuevo pasajero
        Pasajero nuevoPasajero = Pasajero.builder()
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .fechaNacimiento(request.getFechaNacimiento())
                .tipoDocumento(request.getTipoDocumento())
                .numeroDocumento(request.getNumeroDocumento())
                .nacionalidad(request.getNacionalidad() != null ? request.getNacionalidad() : "PERUANA")
                .telefono(request.getTelefono())
                .email(request.getEmail())
                .build();

        return pasajeroRepository.save(nuevoPasajero);
    }

    private boolean existePasajeroEnReserva(List<ReservaDetalle> detalles, Pasajero pasajero) {
        return detalles.stream()
                .anyMatch(d -> d.getPasajero().getIdPasajero().equals(pasajero.getIdPasajero()));
    }

    private void procesarPagoInicial(Reserva reserva, PagoInicialRequest pagoRequest,
                                     DatosFacturaRequest facturaRequest) {

        // Validar adelanto mínimo
        if (!calculoService.esAdelantoSuficiente(pagoRequest.getMonto(), reserva.getTotal())) {
            BigDecimal adelantoMinimo = calculoService.calcularAdelantoMinimo(reserva.getTotal());
            throw new AdelantoInsuficienteException(
                    String.format("El adelanto mínimo es S/ %.2f (50%% del total)", adelantoMinimo)
            );
        }

        // Crear pago
        Pago pago = Pago.builder()
                .reserva(reserva)
                .tipoPago(pagoRequest.getTipoPago())
                .metodoPago(pagoRequest.getMetodoPago())
                .monto(pagoRequest.getMonto())
                .referenciaTransaccion(pagoRequest.getReferenciaTransaccion())
                .estado(EstadoPago.CONFIRMADO)
                .build();


        reserva.agregarPago(pago);

        // Actualizar montos
        reserva.setMontoPagado(pagoRequest.getMonto());
        reserva.calcularSaldoPendiente();

        // Actualizar estado
        if (reserva.getSaldoPendiente().compareTo(BigDecimal.ZERO) == 0) {
            reserva.setEstado(EstadoReserva.PAGADA);
        } else {
            reserva.setEstado(EstadoReserva.CONFIRMADA);
        }
    }

    private void validarCancelacion(Reserva reserva) {
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new OperacionNoPermitidaException("La reserva ya está cancelada");
        }

        if (reserva.getEstado() == EstadoReserva.COMPLETADA) {
            throw new OperacionNoPermitidaException("No se puede cancelar una reserva completada");
        }

        // Validar que el viaje no haya pasado
        if (reserva.getViaje().getFechaViaje().isBefore(LocalDate.now())) {
            throw new OperacionNoPermitidaException("No se puede cancelar una reserva de un viaje pasado");
        }
    }
}
