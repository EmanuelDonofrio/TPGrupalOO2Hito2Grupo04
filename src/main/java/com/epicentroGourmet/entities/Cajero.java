package com.epicentroGourmet.entities;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor
public class Cajero extends Empleado{

    public enum turnoEnum{
        TURNO_MANIANA,
        TURNO_TARDE,
        TURNO_NOCHE
    }
    @Enumerated(EnumType.STRING)
    @Column(name="turno", nullable=false, length=100)
    private turnoEnum turno;

    public Cajero(User usuario, LocalDate fechaIngreso, Integer antiguedad, double sueldoBase, String nombre, String apellido, LocalDate fechaNacimiento, Long dni, turnoEnum turno) {
        super(usuario, fechaIngreso, antiguedad, sueldoBase, nombre, apellido, fechaNacimiento, dni);
        this.turno = turno;
    }
}
