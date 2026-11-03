package com.epicentroGourmet.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epicentroGourmet.entities.CierreCaja;
import com.epicentroGourmet.entities.UnidadVenta;

public interface ICierreCajaRepository
        extends JpaRepository<CierreCaja, Long> {

    Optional<CierreCaja> findByUnidadVentaAndAbiertaTrue(
        UnidadVenta unidadVenta
    );

    @Query("""
        SELECT COUNT(c) > 0
        FROM CierreCaja c
        WHERE c.unidadVenta = :unidad
        AND c.abierta = true
    """)
    boolean existeCajaAbierta(
        @Param("unidad") UnidadVenta unidad
    );
}
