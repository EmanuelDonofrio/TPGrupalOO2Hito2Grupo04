package com.epicentroGourmet.entities;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor
public class Cocinero extends Empleado{

    private String especialidad;
    private String plusCategoria;

    public Cocinero(User usuario, LocalDate fechaIngreso, Integer antiguedad, double sueldoBase, String nombre, String apellido, LocalDate fechaNacimiento, Long dni, String especialidad, String plusCategoria) {
        super(usuario, fechaIngreso, antiguedad, sueldoBase, nombre, apellido, fechaNacimiento, dni);
        this.especialidad = especialidad;
        this.plusCategoria = plusCategoria;
    }
}
