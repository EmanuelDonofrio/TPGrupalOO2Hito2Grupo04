package com.epicentroGourmet.entities;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor
public class Cajero extends Empleado{

    private String turno;

    public Cajero(User usuario, LocalDate fechaIngreso, Integer antiguedad, double sueldoBase, String nombre, String apellido, LocalDate fechaNacimiento, Long dni, String turno) {
        super(usuario, fechaIngreso, antiguedad, sueldoBase, nombre, apellido, fechaNacimiento, dni);
        this.turno = turno;
    }
}
