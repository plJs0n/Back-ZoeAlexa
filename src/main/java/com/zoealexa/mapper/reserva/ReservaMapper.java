package com.zoealexa.mapper.reserva;

import com.zoealexa.dto.reserva.response.*;
import com.zoealexa.entity.comprobantes.Comprobante;
import com.zoealexa.entity.enums.EstadoReserva;
import com.zoealexa.entity.equipaje.Equipaje;
import com.zoealexa.entity.pagos.Pago;
import com.zoealexa.entity.reservas.CancelacionReprogramacion;
import com.zoealexa.entity.reservas.Pasajero;
import com.zoealexa.entity.reservas.Reserva;
import com.zoealexa.entity.reservas.ReservaDetalle;
import com.zoealexa.entity.transporte.Viaje;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entidades a DTOs de respuesta
 */
@Component
public class ReservaMapper {

    /**
     * Convierte Reserva a ReservaResponse completa
     */
    public ReservaResponse toResponse(Reserva reserva) {
        return ReservaResponse.builder()
                .idReserva(reserva.getIdReserva())
                .codigoReserva(reserva.getCodigoReserva())
                .viaje(toViajeInfoResponse(reserva.getViaje()))
                .nombreUsuario(reserva.getUsuario().getNombresUsuario())
                .nombreAgencia(reserva.getAgencia() != null ?
                        reserva.getAgencia().getNombreAgencia() : null)
                .origen(reserva.getOrigen())
                .destino(reserva.getDestino())
                .total(reserva.getTotal())
                .montoPagado(reserva.getMontoPagado())
                .saldoPendiente(reserva.getSaldoPendiente())
                .penalidadAplicada(reserva.getPenalidadAplicada())
                .comisionAgencia(reserva.getComisionAgencia())
                .estado(reserva.getEstado())
                .pasajeros(toPasajeroDetalleResponseList(reserva.getDetalles()))
                .pagos(toPagoResponseList(reserva.getPagos()))
                .fechaReserva(reserva.getFechaReserva())
                .fechaActualizacion(reserva.getFechaActualizacion())
                .puedeCancelar(puedeCancelar(reserva))
                .puedeReprogramar(puedeReprogramar(reserva))
                .requierePagoSaldo(reserva.tieneSaldoPendiente())
                .build();
    }

    /**
     * Convierte Viaje a ViajeInfoResponse
     */
    public ViajeInfoResponse toViajeInfoResponse(Viaje viaje) {
        return ViajeInfoResponse.builder()
                .idViaje(viaje.getIdViaje())
                .nombreRuta(viaje.getRuta().getNombreRuta())
                .nombreEmbarcacion(viaje.getEmbarcacion().getNombreEmbarcacion())
                .fechaViaje(viaje.getFechaViaje())
                .horaEmbarque(viaje.getHoraEmbarque())
                .cuposDisponibles(viaje.getCuposDisponibles())
                .cuposOcupados(viaje.getCuposOcupados())
                .estado(viaje.getEstado())
                .build();
    }

    /**
     * Convierte lista de ReservaDetalle a lista de PasajeroDetalleResponse
     */
    public List<PasajeroDetalleResponse> toPasajeroDetalleResponseList(List<ReservaDetalle> detalles) {
        return detalles.stream()
                .map(this::toPasajeroDetalleResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convierte ReservaDetalle a PasajeroDetalleResponse
     */
    public PasajeroDetalleResponse toPasajeroDetalleResponse(ReservaDetalle detalle) {
        Pasajero pasajero = detalle.getPasajero();

        return PasajeroDetalleResponse.builder()
                .idPasajero(pasajero.getIdPasajero())
                .nombreCompleto(pasajero.getNombreCompleto())
                .tipoDocumento(pasajero.getTipoDocumento().name())
                .numeroDocumento(pasajero.getNumeroDocumento())
                .edad(pasajero.calcularEdadActual())
                .fechaNacimiento(pasajero.getFechaNacimiento())
                .tipoTarifa(detalle.getTipoTarifa())
                .precioBase(detalle.getPrecioBase())
                .porcentajeDescuento(detalle.getPorcentajeDescuento())
                .montoDescuento(detalle.getMontoDescuento())
                .precioFinal(detalle.getPrecioFinal())
                .equipaje(detalle.getEquipaje() != null ?
                        toEquipajeResponse(detalle.getEquipaje()) : null)
                .build();
    }

    /**
     * Convierte Equipaje a EquipajeResponse
     */
    public EquipajeResponse toEquipajeResponse(Equipaje equipaje) {
        return EquipajeResponse.builder()
                .idEquipaje(equipaje.getIdEquipaje())
                .pesoKg(equipaje.getPesoKg())
                .limiteIncluido(equipaje.getLimiteIncluido())
                .pesoExcedenteKg(equipaje.getPesoExcedenteKg())
                .volumenM3(equipaje.getVolumenM3())
                .precioPorKilo(equipaje.getPrecioPorKilo())
                .costoExceso(equipaje.getCostoExceso())
                .descripcion(equipaje.getDescripcion())
                .tieneExceso(equipaje.tieneExceso())
                .tieneComprobanteGenerado(equipaje.tieneComprobanteExceso())
                .build();
    }

    /**
     * Convierte lista de Pago a lista de PagoResponse
     */
    public List<PagoResponse> toPagoResponseList(List<Pago> pagos) {
        return pagos.stream()
                .map(this::toPagoResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convierte Pago a PagoResponse
     */
    public PagoResponse toPagoResponse(Pago pago) {
        return PagoResponse.builder()
                .idPago(pago.getIdPago())
                .tipoPago(pago.getTipoPago())
                .metodoPago(pago.getMetodoPago())
                .monto(pago.getMonto())
                .referenciaTransaccion(pago.getReferenciaTransaccion())
                .estado(pago.getEstado())
                .fechaPago(pago.getFechaPago())
                .comprobantes(toComprobanteResponseList(pago.getComprobantes()))
                .build();
    }

    /**
     * Convierte lista de Comprobante a lista de ComprobanteResponse
     */
    public List<ComprobanteResponse> toComprobanteResponseList(List<Comprobante> comprobantes) {
        return comprobantes.stream()
                .map(this::toComprobanteResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convierte Comprobante a ComprobanteResponse
     */
    public ComprobanteResponse toComprobanteResponse(Comprobante comprobante) {
        String numeroCompleto = comprobante.getSerie() + "-" + comprobante.getNumero();

        return ComprobanteResponse.builder()
                .idComprobante(comprobante.getIdComprobante())
                .tipo(comprobante.getTipo())
                .serie(comprobante.getSerie())
                .numero(comprobante.getNumero())
                .numeroCompleto(numeroCompleto)
                .concepto(comprobante.getConcepto())
                .rucCliente(comprobante.getRucCliente())
                .razonSocial(comprobante.getRazonSocial())
                .subtotal(comprobante.getSubtotal())
                .igv(comprobante.getIgv())
                .total(comprobante.getTotal())
                .estadoSunat(comprobante.getEstadoSunat())
                .mensajeRespuesta(comprobante.getMensajeRespuesta())
                .fechaEmision(comprobante.getFechaEmision())
                .esComprobanteEquipaje(comprobante.esComprobanteEquipaje())
                .build();
    }

    /**
     * Convierte CancelacionReprogramacion a CancelacionReprogramacionResponse
     */
    public CancelacionReprogramacionResponse toCancelacionResponse(CancelacionReprogramacion cancelacion) {
        String viajeOriginal = String.format("%s - %s (%s)",
                cancelacion.getViajeOriginal().getRuta().getPuertoOrigen().getNombrePuerto(),
                cancelacion.getViajeOriginal().getRuta().getPuertoDestino().getNombrePuerto(),
                cancelacion.getViajeOriginal().getFechaViaje()
        );

        String viajeNuevo = null;
        if (cancelacion.getViajeNuevo() != null) {
            viajeNuevo = String.format("%s - %s (%s)",
                    cancelacion.getViajeNuevo().getRuta().getPuertoOrigen().getNombrePuerto(),
                    cancelacion.getViajeNuevo().getRuta().getPuertoDestino().getNombrePuerto(),
                    cancelacion.getViajeNuevo().getFechaViaje()
            );
        }

        return CancelacionReprogramacionResponse.builder()
                .idOperacion(cancelacion.getIdOperacion())
                .codigoReserva(cancelacion.getReserva().getCodigoReserva())
                .tipoOperacion(cancelacion.getTipoOperacion())
                .viajeOriginal(viajeOriginal)
                .viajeNuevo(viajeNuevo)
                .montoOriginal(cancelacion.getMontoOriginal())
                .porcentajePenalidad(cancelacion.getPorcentajePenalidad())
                .montoPenalidad(cancelacion.getMontoPenalidad())
                .montoDevolver(cancelacion.getMontoDevolver())
                .motivo(cancelacion.getMotivo())
                .nombreUsuario(cancelacion.getUsuario().getNombresUsuario())
                .fechaOperacion(cancelacion.getFechaOperacion())
                .build();
    }

    /**
     * Convierte lista de Reserva a lista de ReservaSimpleResponse
     */
    public List<ReservaSimpleResponse> toSimpleResponseList(List<Reserva> reservas) {
        return reservas.stream()
                .map(this::toSimpleResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convierte Reserva a ReservaSimpleResponse (para listados)
     */
    public ReservaSimpleResponse toSimpleResponse(Reserva reserva) {
        return ReservaSimpleResponse.builder()
                .idReserva(reserva.getIdReserva())
                .codigoReserva(reserva.getCodigoReserva())
                .origen(reserva.getOrigen())
                .destino(reserva.getDestino())
                .fechaViaje(reserva.getViaje().getFechaViaje())
                .horaEmbarque(reserva.getViaje().getHoraEmbarque())
                .cantidadPasajeros(reserva.getDetalles().size())
                .total(reserva.getTotal())
                .saldoPendiente(reserva.getSaldoPendiente())
                .estado(reserva.getEstado())
                .fechaReserva(reserva.getFechaReserva())
                .build();
    }

    // Métodos auxiliares privados

    private boolean puedeCancelar(Reserva reserva) {
        return reserva.getEstado() != EstadoReserva.CANCELADA &&
                reserva.getEstado() != EstadoReserva.COMPLETADA &&
                reserva.getViaje().getFechaViaje().isAfter(java.time.LocalDate.now());
    }

    private boolean puedeReprogramar(Reserva reserva) {
        return reserva.getEstado() != EstadoReserva.CANCELADA &&
                reserva.getEstado() != EstadoReserva.COMPLETADA &&
                reserva.getViaje().getFechaViaje().isAfter(java.time.LocalDate.now());
    }
}
