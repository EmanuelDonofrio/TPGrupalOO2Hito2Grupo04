package com.epicentroGourmet.repositories;

import com.epicentroGourmet.entities.Empleado;
import com.epicentroGourmet.entities.UnidadVenta;
import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IEmpleadoRepository extends JpaRepository<Empleado, Long> {

    //Se agrega Usuario antes de Username ya que el atributo Username no es de
    //empleado directamente, sino del Usuario del empleado
    Optional<Empleado> findByUsuarioUsername(String username);

    List<Empleado> findByUnidadVentaACargo(UnidadVenta unidadVenta);
}