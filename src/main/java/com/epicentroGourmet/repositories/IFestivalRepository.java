package com.epicentroGourmet.repositories;

import com.epicentroGourmet.entities.Festival;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IFestivalRepository extends JpaRepository<Festival, Long> {

    @Query("""
            SELECT DISTINCT f
            FROM Festival f
            JOIN f.unidadesDeVenta uv
            WHERE uv.idUnidadVenta = :idUnidadVenta
            """)
    List<Festival> findByUnidadVentaId(@Param("idUnidadVenta") Long idUnidadVenta);
}
