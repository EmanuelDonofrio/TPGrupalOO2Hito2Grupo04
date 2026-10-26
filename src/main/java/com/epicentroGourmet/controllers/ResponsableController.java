package com.epicentroGourmet.controllers;
import com.epicentroGourmet.entities.Empleado;
import com.epicentroGourmet.entities.Plato;
import com.epicentroGourmet.helpers.ViewRouteHelper;
import com.epicentroGourmet.services.implementation.EmpleadoService;
import com.epicentroGourmet.services.implementation.PlatoService;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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
