package com.zoealexa.entity.reservas;

import com.zoealexa.entity.enums.TipoDocumento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Entity
@Table(name = "pasajero",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"tipo_documento", "numero_documento"})
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pasajero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pasajero")
    private Long idPasajero;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 20)
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_documento", nullable = false, length = 50)
    private String numeroDocumento;

    @Column(name = "nacionalidad", length = 50)
    private String nacionalidad = "PERUANA";

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }

    // Métodos de utilidad

    /**
     * Calcula la edad del pasajero a una fecha específica
     */
    public int calcularEdad(LocalDate fechaReferencia) {
        return Period.between(this.fechaNacimiento, fechaReferencia).getYears();
    }

    /**
     * Calcula la edad actual del pasajero
     */
    public int calcularEdadActual() {
        return calcularEdad(LocalDate.now());
    }

    /**
     * Retorna el nombre completo del pasajero
     */
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }
}