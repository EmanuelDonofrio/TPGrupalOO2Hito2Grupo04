package com.epicentroGourmet.entities;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class PuestoDesarmable extends UnidadVenta{

    private Integer cantidadCarpas;
    private Integer tiempoMontaje;

    public PuestoDesarmable(Empleado responsable, String nombre, double superficie, Long codigo, Integer cantidadCarpas) {
        super(responsable, nombre, superficie, codigo);
        this.cantidadCarpas = cantidadCarpas;
    }
}
