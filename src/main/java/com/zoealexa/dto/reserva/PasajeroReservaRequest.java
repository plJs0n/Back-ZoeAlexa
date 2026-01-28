package com.zoealexa.dto.reserva;

import com.zoealexa.entity.enums.TipoDocumento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasajeroReservaRequest {

    // Si el pasajero ya existe, solo enviar el ID
    private Long idPasajero;

    // Si es pasajero nuevo, enviar todos los datos
    @Size(max = 100, message = "Los nombres no pueden exceder 100 caracteres")
    private String nombres;

    @Size(max = 100, message = "Los apellidos no pueden exceder 100 caracteres")
    private String apellidos;

    private LocalDate fechaNacimiento;

    private TipoDocumento tipoDocumento;

    @Size(max = 50, message = "El número de documento no puede exceder 50 caracteres")
    private String numeroDocumento;

    @Size(max = 50, message = "La nacionalidad no puede exceder 50 caracteres")
    private String nacionalidad;

    @Pattern(regexp = "^[0-9+\\-() ]*$", message = "Formato de teléfono inválido")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;

    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;

    /**
     * Valida que si no hay idPasajero, los datos del pasajero sean completos
     */
    public boolean esNuevoPasajero() {
        return idPasajero == null;
    }
}