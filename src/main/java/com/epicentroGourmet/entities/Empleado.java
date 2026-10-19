package com.epicentroGourmet.entities;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEmpleado;

    // Muchos empleados pueden pertenecer a una misma unidad de venta.
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "idUnidadVenta")
    private UnidadVenta unidadVentaACargo;

    //Se debe crear el empleado obligatoriamente con un usuario asociado
    @OneToOne(optional = false)
    @JoinColumn(name = "idUser", nullable = false, unique = true)
    private User usuario;

    private LocalDate fechaIngreso;

    private Integer antiguedad;

    private double sueldoBase;

    private String nombre;

    private String apellido;

    private LocalDate fechaNacimiento;

    private Long dni;

    public Empleado(User usuario, LocalDate fechaIngreso, Integer antiguedad, double sueldoBase, String nombre, String apellido, LocalDate fechaNacimiento, Long dni) {
        this.usuario = usuario;
        this.fechaIngreso = fechaIngreso;
        this.antiguedad = antiguedad;
        this.sueldoBase = sueldoBase;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.dni = dni;
    }
}
