package com.epicentroGourmet.entities;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;

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

    //metodo de dominio
    public int calcularAntiguedad(){
        if(this.fechaIngreso == null){
            return 0;
        }
        return Period.between(this.fechaIngreso, LocalDate.now()).getYears();
    }

    // Sincroniza el campo 'antiguedad' antes de guardar o al cargar de la BD
    @PrePersist
    @PreUpdate
    @PostLoad
    public void sincronizarAntiguedad(){
        this.antiguedad = calcularAntiguedad();
    }


}
