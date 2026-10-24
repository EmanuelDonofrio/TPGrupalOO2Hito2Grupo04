package com.epicentroGourmet.controllers;

import com.epicentroGourmet.dto.EmpleadoDTOForm;
import com.epicentroGourmet.entities.Cajero;
import com.epicentroGourmet.entities.Cocinero;
import com.epicentroGourmet.helpers.ViewRouteHelper;
import com.epicentroGourmet.services.implementation.EmpleadoService;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

@Controller
@PreAuthorize("hasRole('RESPONSABLE')")
@RequestMapping("/empleado")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping("/nuevo")
    public ModelAndView crearEmpleado() {
        ModelAndView modelAndView = new ModelAndView("empleado/nuevo");
        modelAndView.addObject("empleado", new EmpleadoDTOForm());
        modelAndView.addObject("especialidades", Cocinero.EspecialidadCocinaEnum.values());
        modelAndView.addObject("turnos", Cajero.turnoEnum.values());
        return modelAndView;
    }

    @PostMapping("/create")
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

    @PostMapping("/eliminar/{id}")
    public ModelAndView eliminarEmpleado(@PathVariable("id") Long id,
                                         RedirectAttributes redirectAttributes) {
        empleadoService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensaje",
                "Empleado habilitado correctamente.");
        return new ModelAndView(ViewRouteHelper.ROUTE_RESPONSABLE_EMPLEADOS);
    }
}
