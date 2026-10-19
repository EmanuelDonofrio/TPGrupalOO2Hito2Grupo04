package com.epicentroGourmet.dto;

import com.epicentroGourmet.entities.Cajero;
import com.epicentroGourmet.entities.Cocinero;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmpleadoDTOForm {
    private String tipo;
    private String username;
    private String password;
    private String nombre;
    private String apellido;
    private Long dni;
    private LocalDate fechaNacimiento;
    private LocalDate fechaIngreso;
    private Integer antiguedad;
    private Double sueldoBase;
    private Cocinero.EspecialidadCocinaEnum especialidad;
    private Double plusCategoria;
    private Cajero.turnoEnum turno;
}
