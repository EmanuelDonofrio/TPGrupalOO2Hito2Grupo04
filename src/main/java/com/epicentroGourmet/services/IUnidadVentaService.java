package com.epicentroGourmet.services;

import com.epicentroGourmet.dto.PlatoDTO;
import com.epicentroGourmet.dto.RegistroUnidadVentaDTO;
import com.epicentroGourmet.entities.UnidadVenta;
import com.epicentroGourmet.repositories.RankingUnidadProjection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IUnidadVentaService {
    void registrarUnidadConResponsable(RegistroUnidadVentaDTO dto) throws Exception;
    List<UnidadVenta> listarTodas();
    List<RankingUnidadProjection> obtenerRankingPorFestival(Long idFestival);

    BigDecimal calcularRentabilidadNeta(Long idUnidadVenta, Long idFestival);
    BigDecimal calcularRentabilidadNetaEntreFechas(Long idUnidadVenta, Long idFestival, LocalDate fechaDesde, LocalDate fechaHasta);

    PlatoDTO obtenerPlatoEstrella(Long idUnidadVenta, Long idFestival);
    
}
