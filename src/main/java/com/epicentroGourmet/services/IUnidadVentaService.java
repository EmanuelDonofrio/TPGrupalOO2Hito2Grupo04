package com.epicentroGourmet.services;

import com.epicentroGourmet.dto.RegistroUnidadVentaDTO;
import com.epicentroGourmet.entities.UnidadVenta;
import com.epicentroGourmet.repositories.RankingUnidadProjection;

import java.util.List;

public interface IUnidadVentaService {
    void registrarUnidadConResponsable(RegistroUnidadVentaDTO dto) throws Exception;
    List<UnidadVenta> listarTodas();
    List<RankingUnidadProjection> obtenerRankingPorFestival(Long idFestival);
    
}
