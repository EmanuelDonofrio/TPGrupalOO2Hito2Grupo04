package com.epicentroGourmet.controllers;
import com.epicentroGourmet.dto.EmpleadoDTOForm;
import com.epicentroGourmet.entities.Cajero;
import com.epicentroGourmet.entities.Cocinero;
import com.epicentroGourmet.entities.Empleado;
import com.epicentroGourmet.entities.Plato;
import com.epicentroGourmet.helpers.ViewRouteHelper;
import com.epicentroGourmet.services.implementation.EmpleadoService;
import com.epicentroGourmet.services.implementation.PlatoService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@PreAuthorize("hasRole('RESPONSABLE')")
@RequestMapping("/responsable")
public class ResponsableController {

    private final EmpleadoService empleadoService;
    private final PlatoService platoService;

    public ResponsableController(EmpleadoService empleadoService, PlatoService platoService) {
        this.empleadoService = empleadoService;
        this.platoService = platoService;
    }

    @GetMapping("/")
    public ModelAndView responsable(Authentication authentication) {

        Empleado empleado = empleadoService.obtenerEmpleado(authentication.getName());

        List<Empleado> empleados = empleadoService.obtenerPorUnidadVenta(
                empleado.getUnidadVentaACargo());

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
                responsable.getUnidadVentaACargo());

        ModelAndView modelAndView = new ModelAndView(ViewRouteHelper.RESPONSABLE_EMPLEADOS);
        modelAndView.addObject("empleados", empleados);
        return modelAndView;
    }

    @GetMapping("/empleados/nuevo")
    public ModelAndView crearEmpleado() {
        ModelAndView modelAndView = new ModelAndView("empleado/nuevo");
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

        ModelAndView modelAndView = new ModelAndView("empleado/nuevo");
        modelAndView.addObject("especialidades", Cocinero.EspecialidadCocinaEnum.values());
        modelAndView.addObject("turnos", Cajero.turnoEnum.values());

        if (bindingResult.hasErrors()) {
            return modelAndView;
        }

        empleadoService.crear(form, authentication.getName());
        redirectAttributes.addFlashAttribute("mensaje", "Empleado creado correctamente.");
        return new ModelAndView(ViewRouteHelper.ROUTE_RESPONSABLE_EMPLEADOS);
    }

    @PostMapping("/empleados/eliminar/{id}")
    public ModelAndView eliminarEmpleado(@PathVariable("id") Long id,
                                         RedirectAttributes redirectAttributes) {
        empleadoService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensaje",
                "Empleado habilitado correctamente.");
        return new ModelAndView(ViewRouteHelper.ROUTE_RESPONSABLE_EMPLEADOS);
    }


    @GetMapping("/platos")
    public ModelAndView verPlatos(Authentication authentication) {
        Empleado responsable = empleadoService.obtenerEmpleado(authentication.getName());
        List<Plato> platos = platoService.obtenerPorUnidadVenta(
                responsable.getUnidadVentaACargo());

        ModelAndView modelAndView = new ModelAndView(ViewRouteHelper.RESPONSABLE_PLATOS);
        modelAndView.addObject("platos", platos);
        return modelAndView;
    }

}
