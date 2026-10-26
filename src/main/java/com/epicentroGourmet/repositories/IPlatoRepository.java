package com.epicentroGourmet.repositories;
import com.epicentroGourmet.entities.Plato;
import com.epicentroGourmet.entities.UnidadVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPlatoRepository extends JpaRepository<Plato, Long> {

    @Query("select plato from UnidadVenta unidad join unidad.menu plato " +
            "where unidad = :unidadVenta")
    List<Plato> findByUnidadVenta(@Param("unidadVenta") UnidadVenta unidadVenta);

}
