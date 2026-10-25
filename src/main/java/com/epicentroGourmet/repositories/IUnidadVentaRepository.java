package com.epicentroGourmet.repositories;

import com.epicentroGourmet.entities.UnidadVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUnidadVentaRepository extends JpaRepository<UnidadVenta, Long> {
}
