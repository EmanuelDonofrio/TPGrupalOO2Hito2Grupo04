package com.epicentroGourmet.controllers;

import com.epicentroGourmet.dto.EmpleadoDTOForm;
import com.epicentroGourmet.entities.*;
import com.epicentroGourmet.helpers.ViewRouteHelper;
import com.epicentroGourmet.repositories.IFestivalRepository;
import com.epicentroGourmet.repositories.IUserRepository;
import com.epicentroGourmet.services.IFestivalService;
import com.epicentroGourmet.services.implementation.EmpleadoService;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

import java.util.List;

@Controller
@PreAuthorize("hasRole('EMPLEADO')")
@RequestMapping("/empleado")
public class EmpleadoController {

    private final IUserRepository userRepository;
    private final IFestivalService festivalService;

    public EmpleadoController(IUserRepository userRepository, IFestivalService festivalService) {
        this.userRepository = userRepository;
        this.festivalService = festivalService;
    }

    // 1. Ver su información personal, unidad y festivales
    @GetMapping("/mi-informacion")
    public ModelAndView verMiInformacion(Authentication authentication){
        ModelAndView mav = new ModelAndView("empleado/informacion");
        User user = userRepository.findByUsername(authentication.getName());

        if(user != null && user.getEmpleado() != null){
            Empleado empleado = user.getEmpleado();
            UnidadVenta unidad = empleado.getUnidadVentaACargo();

            mav.addObject("empleado", empleado);
            mav.addObject("unidad", unidad);

            if (unidad != null){
                List<Festival> festivales = festivalService.traerFestivalesPorUnidad(unidad.getIdUnidadVenta());
                mav.addObject("festivales", festivales);
            }else{
                mav.addObject("festivales", List.of());
            }
        }
        return mav;
    }


}
