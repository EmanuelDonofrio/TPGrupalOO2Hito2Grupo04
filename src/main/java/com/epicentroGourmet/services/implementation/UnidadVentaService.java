package com.epicentroGourmet.services.implementation;

import com.epicentroGourmet.repositories.UnidadVentaRepository;
import com.epicentroGourmet.entities.UnidadVenta;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UnidadVentaService {
    private final UnidadVentaRepository unidadVentaRepository;

    public UnidadVentaService(UnidadVentaRepository unidadVentaRepository) {
        this.unidadVentaRepository = unidadVentaRepository;
    }

    public List<UnidadVenta> listarTodas() {
        return unidadVentaRepository.findAll();
    }
}
