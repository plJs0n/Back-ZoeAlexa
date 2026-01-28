package com.zoealexa.repository.reservas;

import com.zoealexa.repository.pago.ComprobanteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

/**
 * Servicio para generar códigos únicos de reservas y comprobantes
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CodigoGeneradorService {

    private final ReservaRepository reservaRepository;
    private final ComprobanteRepository comprobanteRepository;

    /**
     * Genera un código de reserva único
     * Formato: RV-YYYY-NNNNNN (ej: RV-2025-000001)
     */
    @Transactional
    public synchronized String generarCodigoReserva() {
        int anioActual = Year.now().getValue();
        String patron = "RV-" + anioActual + "-%";

        // Obtener el último número del año actual
        Integer ultimoNumero = reservaRepository.findUltimoNumeroReservaDelAnio(patron);

        int siguienteNumero = (ultimoNumero != null) ? ultimoNumero + 1 : 1;

        // Formatear a 6 dígitos
        String codigo = String.format("RV-%d-%06d", anioActual, siguienteNumero);

        // Verificar que no exista (medida de seguridad extra)
        while (reservaRepository.existsByCodigoReserva(codigo)) {
            siguienteNumero++;
            codigo = String.format("RV-%d-%06d", anioActual, siguienteNumero);
        }

        log.debug("Código de reserva generado: {}", codigo);
        return codigo;
    }

    /**
     * Genera el siguiente número de comprobante para una serie específica
     * Formato: Serie = B001 o F001, Número = 00000001
     */
    @Transactional
    public synchronized String generarNumeroComprobante(String serie) {
        Integer ultimoNumero = comprobanteRepository.findUltimoNumeroBySerie(serie);

        int siguienteNumero = (ultimoNumero != null) ? ultimoNumero + 1 : 1;

        // Formatear a 8 dígitos
        String numero = String.format("%08d", siguienteNumero);

        // Verificar que no exista
        while (comprobanteRepository.findBySerieAndNumero(serie, numero).isPresent()) {
            siguienteNumero++;
            numero = String.format("%08d", siguienteNumero);
        }

        log.debug("Número de comprobante generado: {}-{}", serie, numero);
        return numero;
    }

    /**
     * Obtiene la serie de comprobante según el tipo
     */
    public String obtenerSerie(String tipoComprobante) {
        return switch (tipoComprobante.toUpperCase()) {
            case "BOLETA" -> "B001";
            case "FACTURA" -> "F001";
            default -> throw new IllegalArgumentException("Tipo de comprobante no válido: " + tipoComprobante);
        };
    }
}
