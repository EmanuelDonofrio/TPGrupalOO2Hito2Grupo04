package com.epicentroGourmet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlatoDTO {
    private Long idPlato;
    private String nombre;
    private double precioVenta;
    private double precioCosto;
    private Long cantidadPedidos; // Total acumulado de veces o unidades pedidas
}
