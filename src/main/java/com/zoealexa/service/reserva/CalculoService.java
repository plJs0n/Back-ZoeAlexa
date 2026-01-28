package com.zoealexa.service.reserva;

import com.zoealexa.entity.enums.TipoComision;
import com.zoealexa.entity.enums.TipoOperacionCancelacion;
import com.zoealexa.entity.enums.TipoValor;
import com.zoealexa.entity.reservas.Pasajero;
import com.zoealexa.entity.reservas.Reserva;
import com.zoealexa.entity.seguridad.Agencia;
import com.zoealexa.entity.tarifas.ReglaDescuento;
import com.zoealexa.entity.tarifas.ReglaPenalidad;
import com.zoealexa.repository.tarifas.ReglaDescuentoRepository;
import com.zoealexa.repository.tarifas.ReglaPenalidadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Servicio para cálculos de descuentos, penalidades y tarifas
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CalculoService {

    private final ReglaDescuentoRepository reglaDescuentoRepository;
    private final ReglaPenalidadRepository reglaPenalidadRepository;

    // Constantes del negocio
    private static final BigDecimal ADELANTO_MINIMO_PORCENTAJE = new BigDecimal("50.00");
    private static final BigDecimal PORCENTAJE_100 = new BigDecimal("100");
    private static final BigDecimal IGV_PORCENTAJE = new BigDecimal("0.18");

    /**
     * Calcula el adelanto mínimo requerido (50% del total)
     */
    public BigDecimal calcularAdelantoMinimo(BigDecimal total) {
        return total
                .multiply(ADELANTO_MINIMO_PORCENTAJE)
                .divide(PORCENTAJE_100, 2, RoundingMode.HALF_UP);
    }

    /**
     * Valida si el monto de adelanto es suficiente
     */
    public boolean esAdelantoSuficiente(BigDecimal monto, BigDecimal total) {
        BigDecimal adelantoMinimo = calcularAdelantoMinimo(total);
        return monto.compareTo(adelantoMinimo) >= 0;
    }

    /**
     * Aplica descuento a un pasajero según su edad
     */
    public ResultadoDescuento aplicarDescuentoPorEdad(Pasajero pasajero, BigDecimal precioBase, LocalDate fechaViaje) {
        int edad = pasajero.calcularEdad(fechaViaje);

        // Buscar regla de descuento aplicable
        Optional<ReglaDescuento> reglaOpt = reglaDescuentoRepository
                .findByEdadAndActiva(edad, true)
                .stream()
                .findFirst();

        if (reglaOpt.isEmpty()) {
            // Sin descuento aplicable
            return ResultadoDescuento.builder()
                    .tipoTarifa(obtenerTipoTarifaPorEdad(edad))
                    .precioBase(precioBase)
                    .porcentajeDescuento(BigDecimal.ZERO)
                    .montoDescuento(BigDecimal.ZERO)
                    .precioFinal(precioBase)
                    .build();
        }

        ReglaDescuento regla = reglaOpt.get();
        BigDecimal montoDescuento = BigDecimal.ZERO;
        BigDecimal porcentajeDescuento = BigDecimal.ZERO;

        if (regla.getTipoValor() == TipoValor.PORCENTAJE) {
            porcentajeDescuento = regla.getValor();
            montoDescuento = precioBase
                    .multiply(porcentajeDescuento)
                    .divide(PORCENTAJE_100, 2, RoundingMode.HALF_UP);
        } else if (regla.getTipoValor() == TipoValor.MONTO_FIJO) {
            montoDescuento = regla.getValor();
            porcentajeDescuento = montoDescuento
                    .multiply(PORCENTAJE_100)
                    .divide(precioBase, 2, RoundingMode.HALF_UP);
        }

        BigDecimal precioFinal = precioBase.subtract(montoDescuento);
        if (precioFinal.compareTo(BigDecimal.ZERO) < 0) {
            precioFinal = BigDecimal.ZERO;
        }

        return ResultadoDescuento.builder()
                .tipoTarifa(regla.getDescripcion())
                .precioBase(precioBase)
                .porcentajeDescuento(porcentajeDescuento)
                .montoDescuento(montoDescuento)
                .precioFinal(precioFinal)
                .build();
    }

    /**
     * Calcula la penalidad por cancelación o reprogramación
     */
    public ResultadoPenalidad calcularPenalidad(
            Reserva reserva,
            TipoOperacionCancelacion tipoOperacion) {

        // Si la reserva fue hecha el mismo día, no hay penalidad
        if (reserva.puedeCambiarSinPenalidad()) {
            log.info("Reserva {} hecha el mismo día - sin penalidad", reserva.getCodigoReserva());
            return ResultadoPenalidad.sinPenalidad(reserva.getMontoPagado());
        }

        // Validar que se avise con al menos 1 día de anticipación
        LocalDate fechaViaje = reserva.getViaje().getFechaViaje();
        LocalDate hoy = LocalDate.now();
        long diasAnticipacion = ChronoUnit.DAYS.between(hoy, fechaViaje);

        if (diasAnticipacion < 1) {
            log.warn("Reserva {} - No se cumple requisito de 1 día de anticipación",
                    reserva.getCodigoReserva());
        }

        // Buscar regla de penalidad
        String tipoPenalidadStr = tipoOperacion == TipoOperacionCancelacion.CANCELACION
                ? "CANCELACION" : "REPROGRAMACION";

        Optional<ReglaPenalidad> reglaOpt = reglaPenalidadRepository
                .findByTipoPenalidadAndActiva(tipoPenalidadStr, true)
                .stream()
                .findFirst();

        if (reglaOpt.isEmpty()) {
            log.warn("No se encontró regla de penalidad para {}", tipoPenalidadStr);
            return ResultadoPenalidad.sinPenalidad(reserva.getMontoPagado());
        }

        ReglaPenalidad regla = reglaOpt.get();
        BigDecimal montoPagado = reserva.getMontoPagado();
        BigDecimal porcentajePenalidad = BigDecimal.ZERO;
        BigDecimal montoPenalidad = BigDecimal.ZERO;

        if (regla.getTipoValor() == TipoValor.PORCENTAJE) {
            porcentajePenalidad = regla.getValor();
            montoPenalidad = montoPagado
                    .multiply(porcentajePenalidad)
                    .divide(PORCENTAJE_100, 2, RoundingMode.HALF_UP);
        } else if (regla.getTipoValor() == TipoValor.MONTO_FIJO) {
            montoPenalidad = regla.getValor();
            porcentajePenalidad = montoPenalidad
                    .multiply(PORCENTAJE_100)
                    .divide(montoPagado, 2, RoundingMode.HALF_UP);
        }

        BigDecimal montoDevolver = montoPagado.subtract(montoPenalidad);
        if (montoDevolver.compareTo(BigDecimal.ZERO) < 0) {
            montoDevolver = BigDecimal.ZERO;
        }

        return ResultadoPenalidad.builder()
                .porcentajePenalidad(porcentajePenalidad)
                .montoPenalidad(montoPenalidad)
                .montoDevolver(montoDevolver)
                .descripcionRegla(regla.getDescripcion())
                .build();
    }

    /**
     * Calcula el costo de equipaje excedente
     */
    public ResultadoEquipaje calcularCostoEquipaje(BigDecimal pesoKg) {
        // Buscar regla de equipaje
        Optional<ReglaPenalidad> reglaOpt = reglaPenalidadRepository
                .findByTipoPenalidadAndActiva("EQUIPAJE", true)
                .stream()
                .findFirst();

        if (reglaOpt.isEmpty()) {
            log.warn("No se encontró regla de equipaje - usando defaults");
            return ResultadoEquipaje.builder()
                    .limiteIncluido(new BigDecimal("15.00"))
                    .precioPorKilo(new BigDecimal("10.00"))
                    .pesoExcedente(BigDecimal.ZERO)
                    .costoExceso(BigDecimal.ZERO)
                    .build();
        }

        ReglaPenalidad regla = reglaOpt.get();
        BigDecimal limiteIncluido = new BigDecimal(regla.getKilosPermitidos());
        BigDecimal precioPorKilo = regla.getPrecioPorKilo();

        BigDecimal pesoExcedente = pesoKg.subtract(limiteIncluido);
        if (pesoExcedente.compareTo(BigDecimal.ZERO) < 0) {
            pesoExcedente = BigDecimal.ZERO;
        }

        BigDecimal costoExceso = pesoExcedente.multiply(precioPorKilo);

        return ResultadoEquipaje.builder()
                .limiteIncluido(limiteIncluido)
                .precioPorKilo(precioPorKilo)
                .pesoExcedente(pesoExcedente)
                .costoExceso(costoExceso)
                .build();
    }

    /**
     * Calcula la comisión de agencia
     */
    public BigDecimal calcularComisionAgencia(Agencia agencia, BigDecimal montoVenta) {
        if (agencia == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal comision = BigDecimal.ZERO;

        if (agencia.getTipoComision() == TipoComision.PORCENTAJE) {
            comision = montoVenta
                    .multiply(agencia.getValorComision())
                    .divide(PORCENTAJE_100, 2, RoundingMode.HALF_UP);
        } else if (agencia.getTipoComision() == TipoComision.MONTO_FIJO) {
            comision = agencia.getValorComision();
        }

        return comision;
    }

    /**
     * Calcula subtotal e IGV a partir del total
     */
    public MontoConIgv calcularMontoConIgv(BigDecimal totalConIgv) {
        BigDecimal subtotal = totalConIgv
                .divide(BigDecimal.ONE.add(IGV_PORCENTAJE), 2, RoundingMode.HALF_UP);
        BigDecimal igv = totalConIgv.subtract(subtotal);

        return new MontoConIgv(subtotal, igv, totalConIgv);
    }

    // Método auxiliar
    private String obtenerTipoTarifaPorEdad(int edad) {
        if (edad >= 0 && edad <= 2) {
            return "Niño 0-2 años (Gratis - En piernas)";
        } else if (edad >= 3 && edad <= 5) {
            return "Niño 3-5 años (Medio pasaje)";
        } else {
            return "Adulto (Pasaje completo)";
        }
    }

    // Classes auxiliares para resultados

    @lombok.Data
    @lombok.Builder
    public static class ResultadoDescuento {
        private String tipoTarifa;
        private BigDecimal precioBase;
        private BigDecimal porcentajeDescuento;
        private BigDecimal montoDescuento;
        private BigDecimal precioFinal;
    }

    @lombok.Data
    @lombok.Builder
    public static class ResultadoPenalidad {
        private BigDecimal porcentajePenalidad;
        private BigDecimal montoPenalidad;
        private BigDecimal montoDevolver;
        private String descripcionRegla;

        public static ResultadoPenalidad sinPenalidad(BigDecimal montoCompleto) {
            return ResultadoPenalidad.builder()
                    .porcentajePenalidad(BigDecimal.ZERO)
                    .montoPenalidad(BigDecimal.ZERO)
                    .montoDevolver(montoCompleto)
                    .descripcionRegla("Sin penalidad - Reserva del mismo día")
                    .build();
        }
    }

    @lombok.Data
    @lombok.Builder
    public static class ResultadoEquipaje {
        private BigDecimal limiteIncluido;
        private BigDecimal precioPorKilo;
        private BigDecimal pesoExcedente;
        private BigDecimal costoExceso;
    }

    public record MontoConIgv(BigDecimal subtotal, BigDecimal igv, BigDecimal total) {}
}
