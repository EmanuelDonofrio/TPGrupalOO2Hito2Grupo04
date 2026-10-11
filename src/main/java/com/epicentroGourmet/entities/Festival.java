package com.epicentroGourmet.entities;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor
public class Festival {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFestival;

    //Un festival contiene muchas unidades de venta y una unidad de venta puede estar en varios festivales
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "festival_unidadVenta",
            joinColumns = @JoinColumn(name = "idFestival"),
            inverseJoinColumns = @JoinColumn(name = "idUnidadVenta"))
    private Set<UnidadVenta> unidadesDeVenta = new HashSet<>();

    @OneToOne()
    @JoinColumn(name = "idCosto", nullable = false, unique = true)
    private Costo costo;

    private String nombre;

    private String temporada;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    public Festival(String nombre, String temporada, LocalDate fechaInicio, LocalDate fechaFin) {
        super();
        this.nombre = nombre;
        this.temporada = temporada;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }
}
