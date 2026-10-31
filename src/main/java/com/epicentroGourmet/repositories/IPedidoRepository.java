package com.epicentroGourmet.repositories;

import com.epicentroGourmet.dto.PlatoDTO;
import com.epicentroGourmet.entities.Festival;
import com.epicentroGourmet.entities.Pedido;
import com.epicentroGourmet.entities.UnidadVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IPedidoRepository extends JpaRepository<Pedido, Long > {


    List<Pedido> findByPuesto(UnidadVenta puesto);
    List<Pedido> findByPuestoAndFechaCreacionBetween(UnidadVenta puesto, LocalDateTime desde, LocalDateTime hasta);



}
