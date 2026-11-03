package com.epicentroGourmet.services.negocio;

import com.epicentroGourmet.entities.Plato;
import com.epicentroGourmet.entities.UnidadVenta;

import java.util.List;

public interface IPlatoService {
    List<Plato> obtenerPorUnidadVenta(UnidadVenta unidadVenta);

    void crear(Plato plato, String usernameResponsable);

    void modificar(Long id, Plato datos, String usernameResponsable);

    void eliminar(Long id, String usernamerResponsable);

    double calcularMargenPromedioMenu(List<Plato> platos);

    List<Plato> obtenerPlatosCriticos(List<Plato> platos, double umbralPorcentual);

}
