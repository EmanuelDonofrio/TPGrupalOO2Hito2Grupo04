package com.epicentroGourmet.repositories;

import com.epicentroGourmet.entities.Festival;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFestivalRepository extends JpaRepository<Festival, Long> {
    @Query("SELECT f FROM Festival f JOIN f.unidadesDeVenta u WHERE u.idUnidadVenta = :idUnidad")
    List<Festival> findFestivalesByUnidadVentaId(@Param("idUnidad") Long idUnidad);

}


