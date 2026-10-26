package com.epicentroGourmet.services.implementation;
import com.epicentroGourmet.entities.Plato;
import com.epicentroGourmet.entities.UnidadVenta;
import com.epicentroGourmet.repositories.IPlatoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlatoService {

    private final IPlatoRepository platoRepository;

    public PlatoService(IPlatoRepository platoRepository) {
        this.platoRepository = platoRepository;
    }

    public List<Plato> obtenerPorUnidadVenta(UnidadVenta unidadVenta) {

        if (unidadVenta == null) {
            return List.of();
        }
        return platoRepository.findByUnidadVenta(unidadVenta);
    }
}
