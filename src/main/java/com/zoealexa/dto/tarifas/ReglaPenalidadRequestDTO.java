package com.zoealexa.dto.tarifas;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Data;
import com.zoealexa.entity.enums.TipoPenalidad;
import com.zoealexa.entity.enums.TipoValor;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para crear una nueva regla de penalidad
 *
 * IMPORTANTE:
 * - Si tipoPenalidad = EQUIPAJE: enviar kilosPermitidos y precioPorKilo
 * - Si tipoPenalidad != EQUIPAJE: enviar tipoValor y valor
 */
@Data
public class ReglaPenalidadRequestDTO {

    @NotNull(message = "El tipo de penalidad es obligatorio")
    private TipoPenalidad tipoPenalidad;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    private String descripcion;

    // ==========================================
    // PARA CANCELACION, REPROGRAMACION, GENERICA
    // ==========================================

    private TipoValor tipoValor;

    @DecimalMin(value = "0.00", message = "El valor no puede ser negativo")
    private BigDecimal valor;

    // ==========================================
    // SOLO PARA EQUIPAJE
    // ==========================================

    @Min(value = 0, message = "Los kilos permitidos no pueden ser negativos")
    private Integer kilosPermitidos;

    @DecimalMin(value = "0.00", message = "El precio por kilo no puede ser negativo")
    private BigDecimal precioPorKilo;

    // ==========================================
    // ESTADO
    // ==========================================

    private Boolean activa;

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}