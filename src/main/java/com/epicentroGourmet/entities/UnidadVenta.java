package com.epicentroGourmet.entities;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class UnidadVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUnidadVenta;

    //Bidireccionalidad en n:m con festival
    @ManyToMany(mappedBy = "unidadesDeVenta")
    private Set<Festival> festivales = new HashSet<>();

    // Una unidad tiene un único responsable, independiente de sus empleados.
    @OneToOne
    @JoinColumn(name = "idResponsable", unique = true)
    private Empleado responsable;

    //Una unidad de venta tiene muchos platos que ofrece. Unidireccional
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUnidadVenta")
    private Set<Plato> menu = new HashSet<>();

    // Una unidad tiene muchos empleados y cada empleado pertenece a una unidad.
    @OneToMany(mappedBy = "unidadVentaACargo", fetch = FetchType.LAZY)
    private Set<Empleado> empleados = new HashSet<>();

    //Una unidad de venta tiene muchos pedidos y un pedido pertenece a una unica unidad de venta
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "puesto")
    private Set<Pedido> pedidos = new HashSet<>();

    private String nombre;

    private double superficie;

    private Long codigo;

    public UnidadVenta(Empleado responsable, String nombre, double superficie, Long codigo) {
        super();
        this.responsable = responsable;
        this.nombre = nombre;
        this.superficie = superficie;
        this.codigo = codigo;
    }


}
