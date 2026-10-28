package com.epicentroGourmet.services.implementation;
import com.epicentroGourmet.dto.EmpleadoDTOForm;
import com.epicentroGourmet.entities.Festival;
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
    private static final double SUELDO_BASE = 100000;
    private static final double PLUS_ANTIGUEDAD = 5000;
    private static final double PLUS_COCINERO = 150000;
    private static final double PLUS_AYUDANTE = 100000;
    private static final double PLUS_LAVAPLATOS = 50000;
    
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
        double sueldoBase = SUELDO_BASE;	

        if ("COCINERO".equals(form.getTipo())) {

        	double plusCategoria;

        	if (Cocinero.EspecialidadCocinaEnum.COCINERO == form.getEspecialidad()) {
        	    plusCategoria = PLUS_COCINERO;
        	} else if (Cocinero.EspecialidadCocinaEnum.COCINERO_AYUDANTE == form.getEspecialidad()) {
        	    plusCategoria = PLUS_AYUDANTE;
        	} else if (Cocinero.EspecialidadCocinaEnum.LAVAPLATOS == form.getEspecialidad()) {
        	    plusCategoria = PLUS_LAVAPLATOS;
        	} else {
        	    throw new IllegalArgumentException("Especialidad de cocinero inválida.");
        	}

        	empleado = new Cocinero(user, form.getFechaIngreso(), antiguedad, sueldoBase, form.getNombre(), form.getApellido(), form.getFechaNacimiento(), form.getDni(), form.getEspecialidad(),
        	        plusCategoria
        	);

        } else if ("CAJERO".equals(form.getTipo())) {

           

        	empleado = new Cajero(user, form.getFechaIngreso(), antiguedad, sueldoBase, form.getNombre(),  form.getApellido(),form.getFechaNacimiento(),form.getDni(), form.getTurno()
        	);

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
        return Math.ceil(cantidadDiasFestival / 15.0) * 0.5;
    }
    
    public double calcularSueldoPorFestival(
            Empleado empleado, Festival festival) {

        if (empleado == null || festival == null) {
            throw new IllegalArgumentException(
                    "El empleado y el festival son obligatorios");
        }

        if (festival.getFechaInicio() == null
                || festival.getFechaFin() == null
                || festival.getFechaFin().isBefore(festival.getFechaInicio())) {
            throw new IllegalArgumentException(
                    "Las fechas del festival no son válidas");
        }

        double sueldo = SUELDO_BASE;

        // Cocineros: sueldo base + plus por categoría
        if (empleado instanceof Cocinero cocinero) {

            switch (cocinero.getEspecialidad()) {
                case COCINERO:
                    sueldo += PLUS_COCINERO;
                    break;

                case COCINERO_AYUDANTE:
                    sueldo += PLUS_AYUDANTE;
                    break;

                case LAVAPLATOS:
                    sueldo += PLUS_LAVAPLATOS;
                    break;
            }

        // Cajeros: sueldo base + antigüedad
        } else if (empleado instanceof Cajero) {

            sueldo += PLUS_ANTIGUEDAD
                    * empleado.calcularAntiguedad();

        } else {
            throw new IllegalArgumentException(
                    "Tipo de empleado no válido");
        }

        // Cantidad de días del festival
        long diasFestival = ChronoUnit.DAYS.between(festival.getFechaInicio(),festival.getFechaFin()) + 1;

        // Porcentaje según la duración
        double porcentaje = calcularPorcentajeFestival(diasFestival);

        return sueldo * porcentaje;
    }
    
  

}
