package com.epicentroGourmet.entities;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Plato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPlato;

    private String nombre;

    private double precioVenta;

    private double precioCosto;

    public Plato(String nombre, double precioVenta, double precioCosto) {
        super();
        this.nombre = nombre;
        this.precioVenta = precioVenta;
        this.precioCosto = precioCosto;
    }
}
