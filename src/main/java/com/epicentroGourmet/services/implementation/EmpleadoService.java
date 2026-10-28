package com.epicentroGourmet.services.implementation;
import com.epicentroGourmet.dto.EmpleadoDTOForm;
import com.epicentroGourmet.dto.ReporteVentaDTO;
import com.epicentroGourmet.entities.*;
import com.epicentroGourmet.repositories.IEmpleadoRepository;
import com.epicentroGourmet.repositories.IUserRepository;
import com.epicentroGourmet.repositories.IUserRoleRepository;
import com.epicentroGourmet.services.negocio.PoliticaSalarial;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

@Service("empleadoService")
public class EmpleadoService {

    private static final String CARACTERES_CONTRASENA =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LONGITUD_CONTRASENA = 10;
    private final SecureRandom generadorAleatorio = new SecureRandom();

    private final IEmpleadoRepository empleadoRepository;
    private final IUserRepository userRepository;
    private final IUserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public EmpleadoService(IEmpleadoRepository empleadoRepository, IUserRepository userRepository,
                           IUserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder,
                           EmailService emailService) {
        this.empleadoRepository = empleadoRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public Empleado obtenerEmpleado(String username) {

        return empleadoRepository.findByUsuarioUsername(username)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe un empleado asociado al usuario " + username));

    }

    public List<Empleado> obtenerPorUnidadVenta(UnidadVenta unidadVenta, boolean enabled) {
        if (unidadVenta == null) {
            return List.of();
        }
        return empleadoRepository.findByUnidadVentaACargoAndUsuarioUserRoleRoleAndUsuarioEnabled(
                unidadVenta, UserRole.RolEnum.ROLE_EMPLEADO, enabled);
    }

    public String generarContrasenaAleatoria() {
        StringBuilder contrasena = new StringBuilder(LONGITUD_CONTRASENA);

        for (int i = 0; i < LONGITUD_CONTRASENA; i++) {
            int indice = generadorAleatorio.nextInt(CARACTERES_CONTRASENA.length());
            contrasena.append(CARACTERES_CONTRASENA.charAt(indice));
        }

        return contrasena.toString();
    }

    @Transactional
    public Empleado crear(EmpleadoDTOForm form, String usernameResponsable) {

        if (form == null) {
            throw new IllegalArgumentException("Los datos del empleado son obligatorios.");
        }

        if (userRepository.existsByUsername(form.getUsername())) {
            throw new IllegalArgumentException("El username ya está en uso.");
        }

        LocalDate fechaActual = LocalDate.now();

        if (form.getFechaIngreso() == null
                || form.getFechaIngreso().isAfter(fechaActual)) {
            throw new IllegalArgumentException(
                    "La fecha de ingreso no puede ser nula ni futura.");
        }

        if (form.getFechaNacimiento() == null
                || form.getFechaNacimiento().isAfter(fechaActual)) {
            throw new IllegalArgumentException(
                    "La fecha de nacimiento no puede ser nula ni futura.");
        }

        if (Period.between(form.getFechaNacimiento(), fechaActual).getYears() < 18) {
            throw new IllegalArgumentException(
                    "El empleado debe ser mayor de 18 años.");
        }

        if (form.getFechaIngreso().isBefore(form.getFechaNacimiento())) {
            throw new IllegalArgumentException(
                    "La fecha de ingreso no puede ser anterior a la fecha de nacimiento.");
        }

        Empleado responsable = obtenerEmpleado(usernameResponsable);
        UnidadVenta unidadVenta = responsable.getUnidadVentaACargo();

        if (unidadVenta == null) {
            throw new IllegalStateException(
                    "El responsable no tiene una unidad de venta asignada.");
        }

        UserRole userRole = userRoleRepository.findByRole(UserRole.RolEnum.ROLE_EMPLEADO)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe el registro de user_role ROLE_EMPLEADO."));

        String contrasena = generarContrasenaAleatoria();
        User user = userRepository.save(
                new User(form.getUsername(), passwordEncoder.encode(contrasena), true, userRole));

        Empleado empleado;
        int antiguedad = Period.between(form.getFechaIngreso(), LocalDate.now()).getYears();


        double sueldoBase = PoliticaSalarial.SUELDO_BASE;

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

        //ENVIO DE MAIL
        emailService.enviarMail(
                "TP_OO2_GRUPO04@gmail.com",
                "aza004aa@gmail.com",
                "Epicentro Gourmet - Credenciales de acceso",
                "Hola " + form.getNombre() + " " + form.getApellido() + ",\n\n"
                + "¡Te damos la bienvenida a Epicentro Gourmet!\n\n"
                + "Tu cuenta fue dada de alta en el sistema. A continuación, "
                + "encontrarás tus datos de acceso:\n\n"
                + "  Usuario: " + form.getUsername() + "\n"
                + "  Contraseña: " + contrasena + "\n\n"
        );

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

    public boolean habilitar(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException());

        empleado.getUsuario().setEnabled(true);

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

        double sueldo = PoliticaSalarial.SUELDO_BASE;

        // Cocineros: sueldo base + plus por categoría
        if (empleado instanceof Cocinero cocinero) {

            switch (cocinero.getEspecialidad()) {
                case COCINERO:
                    sueldo += PoliticaSalarial.PLUS_COCINERO;
                    break;

                case COCINERO_AYUDANTE:
                    sueldo += PoliticaSalarial.PLUS_COCINERO_AYUDANTE;
                    break;

                case LAVAPLATOS:
                    sueldo += PoliticaSalarial.PLUS_LAVAPLATOS;
                    break;
            }

        // Cajeros: sueldo base + antigüedad
        } else if (empleado instanceof Cajero) {

            sueldo += PoliticaSalarial.PLUS_ANTIGUEDAD
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
    
    public double calculoDeCanon(UnidadVenta unidadVenta) {

        if (unidadVenta == null) {
            throw new IllegalArgumentException("La unidad de venta es obligatoria.");
        }

        UnidadVenta unidadVentaReal = Hibernate.unproxy(unidadVenta, UnidadVenta.class);
        Festival festival = unidadVenta.getFestivales().stream()
                .findFirst() //Teniendo en cuenta que solo hay un unico festival como dice la consigna
                .orElse(null);

        //De base se arranca con este valor, luego se suma dependiendo la superficie que usa, si usa conex electrica, montaje
        double canon = festival.getCosto().getCostoBase();

        if (unidadVentaReal instanceof FoodTruck foodTruck) {
            canon += foodTruck.getSuperficie() * festival.getCosto().getCostoSuperficie();
            if (foodTruck.isConexionElectrica()) {
                canon += festival.getCosto().getCostoElectricidad();
            }
        } else if (unidadVentaReal instanceof PuestoDesarmable puestoDesarmable) {
            canon +=  (puestoDesarmable.getSuperficie() *  festival.getCosto().getCostoSuperficie()) - (puestoDesarmable.getTiempoMontaje() *  festival.getCosto().getCostoMontaje());
        }

        return canon;
    }

    @Transactional(readOnly = true)
    public ReporteVentaDTO reporteDeRecaudacion(UnidadVenta unidadVenta) {
        if (unidadVenta == null) {
            throw new IllegalArgumentException("La unidad de venta es obligatoria.");
        }

        Festival festival = unidadVenta.getFestivales().stream()
                .findFirst() //Teniendo en cuenta que solo hay un unico festival como dice la consigna
                .orElse(null);

        String nombreFestival = festival != null
                ? festival.getNombre()
                : "Sin festival asignado";

        BigDecimal recaudacionTotal = festival == null
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(
                        festival.getUnidadesDeVenta().stream()
                                .flatMap(unidad -> unidad.getPedidos().stream())
                                .mapToDouble(Pedido::getTotal)
                                .sum()
                );

        return new ReporteVentaDTO(nombreFestival, recaudacionTotal);
    }

}
