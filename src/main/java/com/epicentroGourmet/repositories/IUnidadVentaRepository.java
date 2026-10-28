package com.epicentroGourmet.repositories;

import com.epicentroGourmet.entities.UnidadVenta;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


@Repository
public interface IUnidadVentaRepository extends JpaRepository<UnidadVenta, Long> {
    List<UnidadVenta> findByFestivales_IdFestival(Long idFestival);

    
    @Query("""
        SELECT
            uv.idUnidadVenta AS idUnidadVenta,
            uv.nombre AS nombre,
            SUM(p.total) AS recaudacion
        FROM UnidadVenta uv
        JOIN uv.festivales f
        JOIN uv.pedidos p
        WHERE f.idFestival = :idFestival
        GROUP BY uv.idUnidadVenta, uv.nombre
        ORDER BY SUM(p.total) DESC
        """)
    List<RankingUnidadProjection> obtenerRankingPorFestival(
        @Param("idFestival") Long idFestival
    );

}
