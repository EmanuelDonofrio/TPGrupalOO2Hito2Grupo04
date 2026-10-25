package com.epicentroGourmet.entities;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class FoodTruck extends UnidadVenta{

    private String patente;
    private boolean conexionElectrica;

    public FoodTruck(Empleado responsable, String nombre, double superficie, Long codigo, String patente) {
        super(responsable, nombre, superficie, codigo);
        this.patente = patente;
    }
}
