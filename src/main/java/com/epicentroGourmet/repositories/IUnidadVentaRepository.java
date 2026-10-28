package com.epicentroGourmet.repositories;

import com.epicentroGourmet.entities.UnidadVenta;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUnidadVentaRepository extends JpaRepository<UnidadVenta, Long> {
    List<UnidadVenta> findByFestivales_IdFestival(Long idFestival);

}
