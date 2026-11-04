package com.epicentroGourmet.repositories;

import com.epicentroGourmet.entities.UnidadVenta;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface IUnidadVentaRepository extends JpaRepository<UnidadVenta, Long> {
    List<UnidadVenta> findByFestivales_IdFestival(Long idFestival);

    
    @Query("""
         SELECT
            uv.idUnidadVenta AS idUnidadVenta,
            uv.nombre AS nombre,
            COALESCE(SUM(p.total), 0) AS recaudacion
        FROM UnidadVenta uv
        JOIN uv.festivales f
        LEFT JOIN uv.pedidos p
        WHERE f.idFestival = :idFestival
        GROUP BY uv.idUnidadVenta, uv.nombre
        ORDER BY COALESCE(SUM(p.total), 0) DESC
        """)
    List<RankingUnidadProjection> obtenerRankingPorFestival(
        @Param("idFestival") Long idFestival
    );

    @Query("""
        SELECT uv
        FROM UnidadVenta uv
        WHERE NOT EXISTS (
            SELECT f.idFestival
            FROM Festival f
            JOIN f.unidadesDeVenta unidad
            WHERE f.idFestival = :idFestival
            AND unidad.idUnidadVenta = uv.idUnidadVenta
        )
        """)
    List<UnidadVenta> findDisponiblesParaFestival(
        @Param("idFestival") Long idFestival
    );

}
