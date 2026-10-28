package com.epicentroGourmet.services.implementation;

import com.epicentroGourmet.dto.RegistroUnidadVentaDTO;
import com.epicentroGourmet.entities.Empleado;
import com.epicentroGourmet.entities.FoodTruck;
import com.epicentroGourmet.entities.PuestoDesarmable;
import com.epicentroGourmet.entities.UnidadVenta;
import com.epicentroGourmet.entities.User;
import com.epicentroGourmet.entities.UserRole;
import com.epicentroGourmet.repositories.IEmpleadoRepository;
import com.epicentroGourmet.repositories.IUnidadVentaRepository;
import com.epicentroGourmet.repositories.IUserRepository;
import com.epicentroGourmet.repositories.IUserRoleRepository;
import com.epicentroGourmet.services.IUnidadVentaService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.epicentroGourmet.repositories.RankingUnidadProjection;

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

}