package com.epicentroGourmet.services.implementation;
import com.epicentroGourmet.dto.EmpleadoDTOForm;
import com.epicentroGourmet.entities.Cajero;
import com.epicentroGourmet.entities.Cocinero;
import com.epicentroGourmet.entities.Empleado;
import com.epicentroGourmet.entities.UnidadVenta;
import com.epicentroGourmet.entities.User;
import com.epicentroGourmet.entities.UserRole;
import com.epicentroGourmet.repositories.IEmpleadoRepository;
import com.epicentroGourmet.repositories.IUserRepository;
import com.epicentroGourmet.repositories.IUserRoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service("empleadoService")
public class EmpleadoService {

    private final IEmpleadoRepository empleadoRepository;
    private final IUserRepository userRepository;
    private final IUserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public EmpleadoService(IEmpleadoRepository empleadoRepository, IUserRepository userRepository,
                           IUserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder) {
        this.empleadoRepository = empleadoRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Empleado obtenerEmpleado(String username) {

        return empleadoRepository.findByUsuarioUsername(username)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe un empleado asociado al usuario " + username));

    }

    public List<Empleado> obtenerPorUnidadVenta(UnidadVenta unidadVenta) {
        if (unidadVenta == null) {
            return List.of();
        }
        return empleadoRepository.findByUnidadVentaACargoAndUsuarioUserRoleRole(
                unidadVenta, UserRole.RolEnum.ROLE_EMPLEADO);
    }

    @Transactional
    public Empleado crear(EmpleadoDTOForm form, String usernameResponsable) {

        if (userRepository.existsByUsername(form.getUsername())) {
            throw new IllegalArgumentException("El username ya está en uso.");
        }

        Empleado responsable = obtenerEmpleado(usernameResponsable);
        UnidadVenta unidadVenta = responsable.getUnidadVentaACargo();

        if (unidadVenta == null) {
            throw new IllegalStateException(
                    "El responsable no tiene una unidad de venta asignada.");
        }

        if (form.getFechaIngreso() == null || form.getFechaIngreso().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de ingreso no puede ser nula ni futura.");
        }

        UserRole userRole = userRoleRepository.findByRole(UserRole.RolEnum.ROLE_EMPLEADO)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe el registro de user_role ROLE_EMPLEADO."));

        User user = userRepository.save(
                new User(form.getUsername(), passwordEncoder.encode(form.getPassword()), true, userRole));

        Empleado empleado;
        int antiguedad = Period.between(form.getFechaIngreso(), LocalDate.now()).getYears();

        if ("COCINERO".equals(form.getTipo())) {

            empleado = new Cocinero(user, form.getFechaIngreso(), antiguedad,
                    form.getSueldoBase(), form.getNombre(), form.getApellido(),
                    form.getFechaNacimiento(), form.getDni(), form.getEspecialidad(),
                    form.getPlusCategoria());

        } else if ("CAJERO".equals(form.getTipo())) {

            empleado = new Cajero(user, form.getFechaIngreso(), antiguedad,
                    form.getSueldoBase(), form.getNombre(), form.getApellido(),
                    form.getFechaNacimiento(), form.getDni(), form.getTurno());

        } else {
            throw new IllegalArgumentException("Tipo de empleado inválido.");
        }

        empleado.setUnidadVentaACargo(unidadVenta);
        user.setEmpleado(empleado);

        return empleadoRepository.save(empleado);

    }

}
