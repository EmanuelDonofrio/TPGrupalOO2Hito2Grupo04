package com.epicentroGourmet.services;

import com.epicentroGourmet.dto.PlatoDTO;
import com.epicentroGourmet.dto.RegistroUnidadVentaDTO;
import com.epicentroGourmet.dto.ReporteVentaDTO;
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

    ReporteVentaDTO reporteDeRecaudacion(UnidadVenta unidadVenta);
    double calculoDeCanon(UnidadVenta unidadVenta);

    PlatoDTO obtenerPlatoEstrella(Long idUnidadVenta, Long idFestival);
    
}
