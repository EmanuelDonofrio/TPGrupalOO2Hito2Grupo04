package com.epicentroGourmet.repositories;

import com.epicentroGourmet.entities.Empleado;
import com.epicentroGourmet.entities.Cajero;
import com.epicentroGourmet.entities.Cocinero;
import com.epicentroGourmet.entities.UnidadVenta;
import com.epicentroGourmet.entities.UserRole;
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

    List<Empleado> findByUnidadVentaACargoAndUsuarioUserRoleRoleAndUsuarioEnabled(
            UnidadVenta unidadVenta, UserRole.RolEnum role, boolean enabled);

    Optional<Empleado> findByDniAndUnidadVentaACargoAndUsuarioUserRoleRoleAndUsuarioEnabled(
            Long dni, UnidadVenta unidadVenta, UserRole.RolEnum role, boolean enabled);

    @Query("""
            select e from Empleado e
            where e.unidadVentaACargo = :unidadVenta
              and e.usuario.userRole.role = :role
              and e.usuario.enabled = :enabled
              and type(e) = Cocinero
              and treat(e as Cocinero).especialidad = :especialidad
            """)
    List<Empleado> findCocinerosByEspecialidad(
            @Param("unidadVenta") UnidadVenta unidadVenta,
            @Param("role") UserRole.RolEnum role,
            @Param("enabled") boolean enabled,
            @Param("especialidad") Cocinero.EspecialidadCocinaEnum especialidad);

    @Query("""
            select e from Empleado e
            where e.unidadVentaACargo = :unidadVenta
              and e.usuario.userRole.role = :role
              and e.usuario.enabled = :enabled
              and type(e) = Cajero
              and treat(e as Cajero).turno = :turno
            """)
    List<Empleado> findCajerosByTurno(
            @Param("unidadVenta") UnidadVenta unidadVenta,
            @Param("role") UserRole.RolEnum role,
            @Param("enabled") boolean enabled,
            @Param("turno") Cajero.turnoEnum turno);
}