package com.epicentroGourmet.controllers;
import com.epicentroGourmet.entities.Empleado;
import com.epicentroGourmet.services.implementation.EmpleadoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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

        ModelAndView mv = new ModelAndView("empleado/nuevo");
        mv.addObject("empleado", new Empleado());

        return mv;

    }

}
