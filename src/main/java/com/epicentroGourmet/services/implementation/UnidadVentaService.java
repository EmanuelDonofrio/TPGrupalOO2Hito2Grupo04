package com.epicentroGourmet.services.implementation;

import com.epicentroGourmet.dto.RegistroUnidadVentaDTO;
import com.epicentroGourmet.dto.ReporteVentaDTO;
import com.epicentroGourmet.entities.*;
import com.epicentroGourmet.repositories.IEmpleadoRepository;
import com.epicentroGourmet.repositories.IUnidadVentaRepository;
import com.epicentroGourmet.repositories.IUserRepository;
import com.epicentroGourmet.repositories.IUserRoleRepository;
import com.epicentroGourmet.services.IUnidadVentaService;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.epicentroGourmet.repositories.RankingUnidadProjection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class UnidadVentaService implements IUnidadVentaService {

    private final IUnidadVentaRepository unidadVentaRepository;
    private final IUserRepository userRepository;
    private final IUserRoleRepository userRoleRepository;
    private final IEmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;

    public UnidadVentaService(IUnidadVentaRepository unidadVentaRepository,
                              IUserRepository userRepository,
                              IUserRoleRepository userRoleRepository,
                              IEmpleadoRepository empleadoRepository,
                              PasswordEncoder passwordEncoder) {
        this.unidadVentaRepository = unidadVentaRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.empleadoRepository = empleadoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void registrarUnidadConResponsable(RegistroUnidadVentaDTO dto) throws Exception {
        // 1. Obtener o crear el Rol ROLE_RESPONSABLE
        UserRole roleResponsable = userRoleRepository.findByRole(UserRole.RolEnum.ROLE_RESPONSABLE)
                .orElseGet(() -> {
                    UserRole nuevoRol = new UserRole();
                    nuevoRol.setRole(UserRole.RolEnum.ROLE_RESPONSABLE);
                    return userRoleRepository.save(nuevoRol);
                });

        // 2. Crear y guardar el Usuario
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEnabled(true);
        user.setUserRole(roleResponsable);

        user = userRepository.save(user);

        // 3. Crear el Empleado Responsable
        Empleado responsable = new Empleado();
        responsable.setNombre(dto.getNombreResponsable());
        responsable.setApellido(dto.getApellidoResponsable());
        responsable.setDni(dto.getDniResponsable());
        responsable.setFechaNacimiento(dto.getFechaNacimiento());
        responsable.setFechaIngreso(LocalDate.now());
        responsable.setAntiguedad(0);
        responsable.setSueldoBase(100000.0);
        responsable.setUsuario(user);




        responsable = empleadoRepository.save(responsable);

        // 4. Instanciar subtipo de UnidadVenta
        UnidadVenta unidad;
        if ("FOOD_TRUCK".equalsIgnoreCase(dto.getTipoUnidad())) {
            FoodTruck ft = new FoodTruck();
            ft.setPatente(dto.getPatente());
            ft.setConexionElectrica(Boolean.TRUE.equals(dto.getConexionElectrica()));
            unidad = ft;
        } else {
            PuestoDesarmable pd = new PuestoDesarmable();
            pd.setCantidadCarpas(dto.getCantCarpas() != null ? dto.getCantCarpas() : 1);
            pd.setTiempoMontaje(dto.getTiempoMontajeMin() != null ? dto.getTiempoMontajeMin() : 0);
            unidad = pd;
        }

        unidad.setNombre(dto.getNombreComercial());
        unidad.setSuperficie(dto.getSuperficie());
        unidad.setCodigo(Long.parseLong(dto.getCodigo()));
        unidad.setResponsable(responsable);

        // Guardamos la unidad de venta
        unidad = unidadVentaRepository.save(unidad);

        // 5. Vinculamos la unidad creada al responsable ( clave foránea id_unidad_venta)
        responsable.setUnidadVentaACargo(unidad);
        empleadoRepository.save(responsable);
    }

    @Override
    public List<UnidadVenta> listarTodas() {
        return unidadVentaRepository.findAll();
    }

    public List<UnidadVenta> listarPorFestival(Long idFestival){
        return unidadVentaRepository.findByFestivales_IdFestival(idFestival);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RankingUnidadProjection> obtenerRankingPorFestival(Long idFestival) {
        return unidadVentaRepository.obtenerRankingPorFestival(idFestival);
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