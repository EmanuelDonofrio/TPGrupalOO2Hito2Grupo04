package com.epicentroGourmet.controllers;
import com.epicentroGourmet.entities.Empleado;
import com.epicentroGourmet.entities.UnidadVenta;
import com.epicentroGourmet.services.implementation.EmpleadoService;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@PreAuthorize("hasRole('RESPONSABLE')")
@RequestMapping("/responsable")
public class ResponsableController {

    private final EmpleadoService empleadoService;

    public ResponsableController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping("/")
    public ModelAndView responsable(Authentication authentication) {

        Empleado empleado = empleadoService.obtenerEmpleado(authentication.getName());

        ModelAndView modelAndView = new ModelAndView("responsable/responsable");
        modelAndView.addObject("username", authentication.getName());
        modelAndView.addObject("empleado", empleado);
        modelAndView.addObject("unidadVentaACargo", empleado.getUnidadVentaACargo());
        modelAndView.addObject("festivales", empleado.getUnidadVentaACargo() != null
                ? empleado.getUnidadVentaACargo().getFestivales()
                : java.util.Collections.emptySet());

        return modelAndView;

    }


}
