package com.epicentroGourmet.controllers;
import com.epicentroGourmet.dto.EmpleadoDTOForm;
import com.epicentroGourmet.dto.PlatoDTO;
import com.epicentroGourmet.dto.ReporteVentaDTO;
import com.epicentroGourmet.entities.*;
import com.epicentroGourmet.helpers.ViewRouteHelper;
import com.epicentroGourmet.services.IUnidadVentaService;
import com.epicentroGourmet.services.implementation.EmailService;
import com.epicentroGourmet.services.implementation.EmpleadoService;
import com.epicentroGourmet.services.implementation.PlatoService;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@PreAuthorize("hasRole('RESPONSABLE')")
@RequestMapping("/responsable")
public class ResponsableController {

    private final EmpleadoService empleadoService;
    private final PlatoService platoService;
    private final IUnidadVentaService unidadVentaService;

    public ResponsableController(EmpleadoService empleadoService, PlatoService platoService, IUnidadVentaService unidadVentaService) {
        this.empleadoService = empleadoService;
        this.platoService = platoService;
        this.unidadVentaService= unidadVentaService;
    }

    @GetMapping("/")
    public ModelAndView responsable(Authentication authentication) {

        Empleado empleado = empleadoService.obtenerEmpleado(authentication.getName());

        List<Empleado> empleados = empleadoService.obtenerPorUnidadVenta(
                empleado.getUnidadVentaACargo(), true);

        ModelAndView modelAndView = new ModelAndView(ViewRouteHelper.RESPONSABLE_INICIO);

        modelAndView.addObject("username", authentication.getName());
        modelAndView.addObject("empleado", empleado);
        modelAndView.addObject("unidadVentaACargo", empleado.getUnidadVentaACargo());
        modelAndView.addObject("festivales", empleado.getUnidadVentaACargo() != null
                ? empleado.getUnidadVentaACargo().getFestivales()
                : java.util.Collections.emptySet());

        modelAndView.addObject("empleados", empleados);

        return modelAndView;

    }

    @GetMapping("/empleados")
    public ModelAndView verEmpleados(Authentication authentication) {
        Empleado responsable = empleadoService.obtenerEmpleado(authentication.getName());
        List<Empleado> empleados = empleadoService.obtenerPorUnidadVenta(
                responsable.getUnidadVentaACargo(), true);

        ModelAndView modelAndView = new ModelAndView(ViewRouteHelper.RESPONSABLE_EMPLEADOS);
        modelAndView.addObject("empleados", empleados);
        return modelAndView;
    }

    @GetMapping("/empleadosDeshabilitados")
    public ModelAndView verEmpleadosDeshabilitados(Authentication authentication) {
        Empleado responsable = empleadoService.obtenerEmpleado(authentication.getName());
        List<Empleado> empleados = empleadoService.obtenerPorUnidadVenta(
                responsable.getUnidadVentaACargo(), false);

        ModelAndView modelAndView = new ModelAndView(ViewRouteHelper.RESPONSABLE_EMPLEADOS_DESHABILITADOS);
        modelAndView.addObject("empleados", empleados);
        return modelAndView;
    }

    @GetMapping("/empleados/nuevo")
    public ModelAndView crearEmpleado() {
        ModelAndView modelAndView = new ModelAndView(ViewRouteHelper.EMPLEADO_NUEVO);
        modelAndView.addObject("empleado", new EmpleadoDTOForm());
        modelAndView.addObject("especialidades", Cocinero.EspecialidadCocinaEnum.values());
        modelAndView.addObject("turnos", Cajero.turnoEnum.values());
        return modelAndView;
    }

    @PostMapping("/empleados/create")
    public ModelAndView guardarEmpleado(@Valid @ModelAttribute("empleado") EmpleadoDTOForm form,
                                        BindingResult bindingResult,
                                        RedirectAttributes redirectAttributes,
                                        Authentication authentication) {

        ModelAndView modelAndView = new ModelAndView(ViewRouteHelper.EMPLEADO_NUEVO);
        modelAndView.addObject("especialidades", Cocinero.EspecialidadCocinaEnum.values());
        modelAndView.addObject("turnos", Cajero.turnoEnum.values());

        if (bindingResult.hasErrors()) {
            return modelAndView;
        }


        try {
            empleadoService.crear(form, authentication.getName());
        } catch (IllegalArgumentException exception) {
            modelAndView.addObject("error", exception.getMessage());
            return modelAndView;
        }

        redirectAttributes.addFlashAttribute("mensaje", "Empleado creado correctamente.");
        return new ModelAndView(ViewRouteHelper.ROUTE_RESPONSABLE_EMPLEADOS);
    }

    @PostMapping("/empleados/eliminar/{id}")
    public ModelAndView eliminarEmpleado(@PathVariable("id") Long id,
                                         RedirectAttributes redirectAttributes) {
        empleadoService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensaje",
                "Empleado dado de baja correctamente.");
        return new ModelAndView(ViewRouteHelper.ROUTE_RESPONSABLE_EMPLEADOS);
    }

    @PostMapping("/empleados/alta/{id}")
    public ModelAndView altaEmpleado(@PathVariable("id") Long id,
                                         RedirectAttributes redirectAttributes) {

        empleadoService.habilitar(id);

        redirectAttributes.addFlashAttribute("mensaje",
                "Empleado habilitado correctamente.");
        return new ModelAndView(ViewRouteHelper.ROUTE_RESPONSABLE_EMPLEADOS_DESHABILITADOS);
    }


    @GetMapping("/platos")
    @Transactional(readOnly = true)
    public ModelAndView verPlatos(Authentication authentication) {
        Empleado responsable = empleadoService.obtenerEmpleado(authentication.getName());
        UnidadVenta unidad = responsable.getUnidadVentaACargo();
        List<Plato> platos = platoService.obtenerPorUnidadVenta(
                responsable.getUnidadVentaACargo());

        ModelAndView modelAndView = new ModelAndView(ViewRouteHelper.RESPONSABLE_PLATOS);
        modelAndView.addObject("platos", platos);

        // Caso de uso: Plato Estrella
        if(unidad != null){
            Festival festival = unidad.getFestivales().stream().findFirst().orElse(null);
            if(festival != null){
                PlatoDTO platoEstrella = unidadVentaService.obtenerPlatoEstrella(
                        unidad.getIdUnidadVenta(), festival.getIdFestival());
                modelAndView.addObject("platoEstrella", platoEstrella);
            }
        }

        return modelAndView;
    }

    @GetMapping("/platos/nuevo")
    public ModelAndView crearPlato() {
        ModelAndView modelAndView = new ModelAndView(ViewRouteHelper.PLATO_NUEVO);
        modelAndView.addObject("plato", new Plato());
        return modelAndView;
    }

    @GetMapping("/platos/editar/{id}")
    public ModelAndView editarPlato(@PathVariable("id") Long id,
                                    Authentication authentication) {
        Empleado responsable = empleadoService.obtenerEmpleado(authentication.getName());
        Plato plato = responsable.getUnidadVentaACargo().getMenu().stream()
                .filter(platoMenu -> platoMenu.getIdPlato().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "El plato no pertenece a la unidad de venta del responsable."));

        ModelAndView modelAndView = new ModelAndView(ViewRouteHelper.PLATO_NUEVO);
        modelAndView.addObject("plato", plato);
        return modelAndView;
    }

    @PostMapping("/platos/create")
    public ModelAndView guardarPlato(@Valid @ModelAttribute("plato") Plato plato,
                                        BindingResult bindingResult,
                                        RedirectAttributes redirectAttributes,
                                        Authentication authentication) {

        ModelAndView modelAndView = new ModelAndView(ViewRouteHelper.PLATO_NUEVO);

        if (bindingResult.hasErrors()) {
            return modelAndView;
        }

        platoService.crear(plato, authentication.getName());
        redirectAttributes.addFlashAttribute("mensaje", "Plato creado correctamente.");
        return new ModelAndView(ViewRouteHelper.ROUTE_RESPONSABLE_PLATOS);
    }

    @PostMapping("/platos/update/{id}")
    public ModelAndView actualizarPlato(@PathVariable("id") Long id,
                                        @Valid @ModelAttribute("plato") Plato plato,
                                        BindingResult bindingResult,
                                        RedirectAttributes redirectAttributes,
                                        Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView(ViewRouteHelper.PLATO_NUEVO);
        plato.setIdPlato(id);

        if (bindingResult.hasErrors()) {
            return modelAndView;
        }

        platoService.modificar(id, plato, authentication.getName());
        redirectAttributes.addFlashAttribute("mensaje", "Plato modificado correctamente.");
        return new ModelAndView(ViewRouteHelper.ROUTE_RESPONSABLE_PLATOS);
    }

    @PostMapping("/platos/eliminar/{id}")
    public ModelAndView eliminarPlato(@PathVariable("id") Long id,
                                         RedirectAttributes redirectAttributes,
                                         Authentication authentication) {
        platoService.eliminar(id, authentication.getName());
        redirectAttributes.addFlashAttribute("mensaje",
                "Plato eliminado correctamente.");
        return new ModelAndView(ViewRouteHelper.ROUTE_RESPONSABLE_PLATOS);
    }

    @GetMapping("/calculosyreportes")
    public ModelAndView calculosyreportes(
            @RequestParam(name = "fechaDesde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(name = "fechaHasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            Authentication authentication) {


        Empleado responsable = empleadoService.obtenerEmpleado(authentication.getName());
        UnidadVenta unidadVenta = responsable.getUnidadVentaACargo();

        ModelAndView modelAndView = new ModelAndView(ViewRouteHelper.RESPONSABLE_CALCULOS);
        modelAndView.addObject("unidadVentaACargo", unidadVenta);

        if(unidadVenta != null) {
            double canon = empleadoService.calculoDeCanon(unidadVenta);
            ReporteVentaDTO reporteVenta = empleadoService.reporteDeRecaudacion(unidadVenta);

            modelAndView.addObject("canon", canon);
            modelAndView.addObject("reporteVenta", reporteVenta);

            Festival festival = unidadVenta.getFestivales().stream().findFirst().orElse(null);

            modelAndView.addObject("festival", festival);

            if(festival != null){
                //Rentabilidad Neta
                BigDecimal rentabilidadNeta = unidadVentaService.calcularRentabilidadNeta(
                        unidadVenta.getIdUnidadVenta(), festival.getIdFestival());
                modelAndView.addObject("rentabilidadNeta", rentabilidadNeta);

                // Rentabilidad neta entre fechas
                if(fechaDesde != null && fechaHasta != null){
                    if(!fechaHasta.isBefore(fechaDesde)){
                        BigDecimal rentabilidadNetaFechas = unidadVentaService.calcularRentabilidadNetaEntreFechas(
                                unidadVenta.getIdUnidadVenta(), festival.getIdFestival(), fechaDesde, fechaHasta);
                        modelAndView.addObject("rentabilidadNetaFechas", rentabilidadNetaFechas);
                        modelAndView.addObject("fechaDesde", fechaDesde);
                        modelAndView.addObject("fechaHasta", fechaHasta);
                    }else{
                        modelAndView.addObject("errorFechas", "La fecha 'Hasta no puede ser anterior a la fecha 'Desde'.");
                    }
                }

            }


        }

        return modelAndView;
    }


}
