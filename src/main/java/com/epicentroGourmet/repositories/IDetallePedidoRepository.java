package com.epicentroGourmet.repositories;

import com.epicentroGourmet.entities.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDetallePedidoRepository
        extends JpaRepository<DetallePedido, Long> {
}