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
public class Cocinero extends Empleado{

    public enum EspecialidadCocinaEnum{
        COCINERO,
        COCINERO_AYUDANTE,
        LAVAPLATOS
    }
    @Enumerated(EnumType.STRING)
    @Column(name = "especialidad", nullable = false, length = 50)
    private EspecialidadCocinaEnum especialidad;
    private  double plusCategoria;

    public Cocinero(User usuario, LocalDate fechaIngreso, Integer antiguedad, double sueldoBase, String nombre, String apellido, LocalDate fechaNacimiento, Long dni, EspecialidadCocinaEnum especialidad, double plusCategoria) {
        super(usuario, fechaIngreso, antiguedad, sueldoBase, nombre, apellido, fechaNacimiento, dni);
        this.especialidad = especialidad;
        this.plusCategoria = plusCategoria;
    }
}
