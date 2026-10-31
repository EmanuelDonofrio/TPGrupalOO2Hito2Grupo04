package com.epicentroGourmet.services.implementation;

import com.epicentroGourmet.dto.PlatoDTO;
import com.epicentroGourmet.dto.RegistroUnidadVentaDTO;
import com.epicentroGourmet.dto.ReporteVentaDTO;
import com.epicentroGourmet.entities.*;
import com.epicentroGourmet.repositories.*;
import com.epicentroGourmet.services.IUnidadVentaService;
import org.hibernate.Hibernate;
import org.springframework.cglib.core.Local;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UnidadVentaService implements IUnidadVentaService {

    private final IUnidadVentaRepository unidadVentaRepository;
    private final IUserRepository userRepository;
    private final IUserRoleRepository userRoleRepository;
    private final IEmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;

    private final IPedidoRepository pedidoRepository;
    private final IFestivalRepository festivalRepository;
    private final EmpleadoService empleadoService;

    public UnidadVentaService(IUnidadVentaRepository unidadVentaRepository,
                              IUserRepository userRepository,
                              IUserRoleRepository userRoleRepository,
                              IEmpleadoRepository empleadoRepository,
                              PasswordEncoder passwordEncoder,
                              IPedidoRepository pedidoRepository,
                              IFestivalRepository festivalRepository,
                              EmpleadoService empleadoService) {
        this.unidadVentaRepository = unidadVentaRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.empleadoRepository = empleadoRepository;
        this.passwordEncoder = passwordEncoder;
        this.pedidoRepository = pedidoRepository;
        this.festivalRepository = festivalRepository;
        this.empleadoService = empleadoService;
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

    //----- CALCULO DE RENTABILIDAD -------

    // BALANCE (Método Auxiliar Reutilizable)
    private BigDecimal calcularBalance(UnidadVenta unidad, Festival festival, List<Pedido> pedidos) {
        // 1. Pedidos totales (ventas brutas)
        double totalVentas = pedidos.stream()
                .mapToDouble(Pedido::getTotal)
                .sum();

        // 2. Costos de producción de los platos vendidos
        double costoTotalPlatos = pedidos.stream()
                .flatMap(p -> p.getDetallesPedidos().stream())
                .mapToDouble(item -> item.getPlato().getPrecioCosto() * item.getCantidad()) // O getCostoProduccion() según tu clase Plato
                .sum();

        // 3. Canon obligatorio
        double canon = this.calculoDeCanon(unidad);

        // 4. Sueldos obligatorios (Liquidación de haberes caso 4)
        List<Empleado> empleados = empleadoService.obtenerPorUnidadVenta(unidad, true);
        double totalSueldos = empleados.stream()
                .mapToDouble(emp -> empleadoService.calcularSueldoPorFestival(emp, festival))
                .sum();

        // Rentabilidad Neta = (Ventas - Costos de Platos) - Sueldos - Canon
        double rentabilidad = (totalVentas - costoTotalPlatos) - totalSueldos - canon;

        return BigDecimal.valueOf(rentabilidad);
    }

    // Caso de Uso 6: Cálculo de Rentabilidad Neta Total
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularRentabilidadNeta(Long idUnidadVenta, Long idFestival) {
        UnidadVenta unidad = unidadVentaRepository.findById(idUnidadVenta)
                .orElseThrow(() -> new IllegalArgumentException("Unidad de venta no encontrada"));
        Festival festival = festivalRepository.findById(idFestival)
                .orElseThrow(() -> new IllegalArgumentException("Festival no encontrado"));

        // Se usa findByPuesto con la entidad unidad
        List<Pedido> pedidos = pedidoRepository.findByPuesto(unidad);

        return calcularBalance(unidad, festival, pedidos);
    }

    // Caso de Uso 7: Cálculo de Rentabilidad Neta entre fechas
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularRentabilidadNetaEntreFechas(Long idUnidadVenta, Long idFestival, LocalDate fechaDesde, LocalDate fechaHasta) {
        if (fechaDesde == null || fechaHasta == null || fechaHasta.isBefore(fechaDesde)) {
            throw new IllegalArgumentException("El rango de fechas no es válido.");
        }

        UnidadVenta unidad = unidadVentaRepository.findById(idUnidadVenta)
                .orElseThrow(() -> new IllegalArgumentException("Unidad de venta no encontrada"));
        Festival festival = festivalRepository.findById(idFestival)
                .orElseThrow(() -> new IllegalArgumentException("Festival no encontrado"));

        // Conversión de LocalDate a LocalDateTime para abarcar los días completos
        LocalDateTime desde = fechaDesde.atStartOfDay();
        LocalDateTime hasta = fechaHasta.atTime(java.time.LocalTime.MAX);

        // Se usa la consulta adaptada a fechaCreacion y puesto
        List<Pedido> pedidos = pedidoRepository.findByPuestoAndFechaCreacionBetween(unidad, desde, hasta);

        return calcularBalance(unidad, festival, pedidos);
    }

    // Caso de Uso 8: Plato Estrella
    @Override
    @Transactional(readOnly = true)
    public PlatoDTO obtenerPlatoEstrella(Long idUnidadVenta, Long idFestival){
        UnidadVenta unidad = unidadVentaRepository.findById(idUnidadVenta)
                .orElseThrow(() -> new IllegalArgumentException("Unidad de venta no encontrada"));

        // Validamos que exista el festival
        festivalRepository.findById(idFestival)
                .orElseThrow(() -> new IllegalArgumentException("Festival no encontrado"));

        // 1. Obtenemos todos los pedidos del puesto
        List<Pedido> pedidos = pedidoRepository.findByPuesto(unidad);

        if (pedidos == null || pedidos.isEmpty()){
            return null;
        }

        // 2. Agrupamos los platos y sumamos las cantidades pedidas
        Map<Plato, Long> conteoPorPlato = pedidos.stream()
                .flatMap(p -> p.getDetallesPedidos().stream())
                .filter(d -> d.getPlato() != null)
                .collect(Collectors.groupingBy(
                        DetallePedido::getPlato,
                        Collectors.summingLong(DetallePedido::getCantidad)
                ));

        if(conteoPorPlato.isEmpty()){
            return null;
        }

        // 3. Buscamos el plato con amyor cantidad acumulada
        Map.Entry<Plato, Long> platoGanador = conteoPorPlato.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
        if(platoGanador == null){
            return null;
        }

        Plato plato = platoGanador.getKey();
        long totalVendido = platoGanador.getValue();

        // 4. Finalmente retornamos el PlatoDTO con sus datos exactos

        return new PlatoDTO(
                plato.getIdPlato(),
                plato.getNombre(),
                plato.getPrecioVenta(),
                plato.getPrecioCosto(),
                totalVendido
        );


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