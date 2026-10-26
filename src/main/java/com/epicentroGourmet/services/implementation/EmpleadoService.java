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
import com.epicentroGourmet.services.negocio.PoliticaSalarial;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
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
        return empleadoRepository.findByUnidadVentaACargoAndUsuarioUserRoleRoleAndUsuarioEnabled(
                unidadVenta, UserRole.RolEnum.ROLE_EMPLEADO, true);
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

        // LIQUIDACION DE HABERES
        double plusAntiguedad = PoliticaSalarial.PLUS_ANTIGUEDAD;
        double sueldoBase = PoliticaSalarial.SUELDO_BASE;

        double sueldo = sueldoBase + (plusAntiguedad * antiguedad);

        long cantidadDiasFestival = unidadVenta.getFestivales().stream()
                .filter(festival -> festival.getFechaInicio() != null
                        && festival.getFechaFin() != null)
                .mapToLong(festival -> ChronoUnit.DAYS.between(
                        festival.getFechaInicio(), festival.getFechaFin()) + 1)
                .findFirst()
                .orElse(0L);

        if ("COCINERO".equals(form.getTipo())) {

            double plusCategoria;
            if (Cocinero.EspecialidadCocinaEnum.COCINERO == form.getEspecialidad()) {
                plusCategoria = PoliticaSalarial.PLUS_COCINERO;
            } else if (Cocinero.EspecialidadCocinaEnum.COCINERO_AYUDANTE == form.getEspecialidad()) {
                plusCategoria = PoliticaSalarial.PLUS_COCINERO_AYUDANTE;
            } else if (Cocinero.EspecialidadCocinaEnum.LAVAPLATOS == form.getEspecialidad()) {
                plusCategoria = PoliticaSalarial.PLUS_LAVAPLATOS;
            } else {
                throw new IllegalArgumentException("Especialidad de cocinero inválida.");
            }

            sueldo = sueldo + plusCategoria;
            sueldo = sueldo + (sueldo * calcularPorcentajeFestival(cantidadDiasFestival));

            empleado = new Cocinero(user, form.getFechaIngreso(), antiguedad,
                    sueldo, form.getNombre(), form.getApellido(),
                    form.getFechaNacimiento(), form.getDni(), form.getEspecialidad(),
                    plusCategoria);

        } else if ("CAJERO".equals(form.getTipo())) {

            sueldo = sueldo + (sueldo * calcularPorcentajeFestival(cantidadDiasFestival));

            empleado = new Cajero(user, form.getFechaIngreso(), antiguedad,
                    sueldo, form.getNombre(), form.getApellido(),
                    form.getFechaNacimiento(), form.getDni(), form.getTurno());

        } else {
            throw new IllegalArgumentException("Tipo de empleado inválido.");
        }

        empleado.setUnidadVentaACargo(unidadVenta);
        user.setEmpleado(empleado);

        return empleadoRepository.save(empleado);

    }

    @Transactional
    public boolean eliminar(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un empleado con el ID " + id + "."));

        empleado.getUsuario().setEnabled(false);
        userRepository.save(empleado.getUsuario());
        return true;
    }

    private double calcularPorcentajeFestival(long cantidadDiasFestival) {
        if (cantidadDiasFestival <= 0) {
            return 0;
        }

        //Se divide la duracion del festival en bloques de 15
        //ceil redondea hacia arriba aunque no se llegue a 15
        //y por cada bloque de 15, se aplica un 50%

        // 15 días / 15 = 1    → ceil = 1 → 1 × 0,5 = 0,5  → 50%
        // 16 días / 15 = 1,06 → ceil = 2 → 2 × 0,5 = 1,0  → 100%
        // 30 días / 15 = 2    → ceil = 2 → 2 × 0,5 = 1,0  → 100%
        // 31 días / 15 = 2,06 → ceil = 3 → 3 × 0,5 = 1,5  → 150%
        return Math.ceil(cantidadDiasFestival / PoliticaSalarial.DIAS_BLOQUE_FESTIVAL) * PoliticaSalarial.PORCENTAJE_BLOQUE_FESTIVAL;
    }

}
