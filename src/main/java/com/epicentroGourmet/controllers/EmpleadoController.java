package com.epicentroGourmet.controllers;

import java.time.LocalDate;
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
import java.util.Map;
import java.util.HashMap;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.epicentroGourmet.services.implementation.ReciboSueldoPdfService;


import java.util.List;

@Controller
@PreAuthorize("hasRole('EMPLEADO')")
@RequestMapping("/empleado")
public class EmpleadoController {

    private final IUserRepository userRepository;
    private final IFestivalService festivalService;
    private final EmpleadoService empleadoService;
    private final ReciboSueldoPdfService reciboSueldoPdfService;

    public EmpleadoController(IUserRepository userRepository, IFestivalService festivalService, EmpleadoService empleadoService, ReciboSueldoPdfService reciboSueldoPdfService) {
        this.userRepository = userRepository;
        this.festivalService = festivalService;
        this.empleadoService = empleadoService;
        this.reciboSueldoPdfService = reciboSueldoPdfService;
    }

    // 1. Ver su información personal, unidad y festivales
    @GetMapping("/mi-informacion")
    public ModelAndView verMiInformacion(Authentication authentication){
        ModelAndView mav = new ModelAndView("empleado/informacion");
        User user = userRepository.findByUsername(authentication.getName());

        if(user != null && user.getEmpleado() != null){
            Empleado empleado = user.getEmpleado();
            UnidadVenta unidad = empleado.getUnidadVentaACargo();
            mav.addObject("hoy", LocalDate.now());
            mav.addObject("empleado", empleado);
            mav.addObject("unidad", unidad);

            if (unidad != null){
            	List<Festival> festivales = festivalService.traerFestivalesPorUnidad(unidad.getIdUnidadVenta());

            	mav.addObject("festivales", festivales);
            	Map<Long, Double> sueldosPorFestival = new HashMap<>();

            	for (Festival festival : festivales) {
            	    double sueldo = empleadoService.calcularSueldoPorFestival(empleado, festival);
            	    sueldosPorFestival.put(festival.getIdFestival(), sueldo);
            	}

            	mav.addObject("sueldosPorFestival", sueldosPorFestival);

            	// Buscar un festival vigente
            	LocalDate hoy = LocalDate.now();

            	Festival festivalActual = festivales.stream().filter(f -> f.getFechaInicio() != null
            	                && f.getFechaFin() != null
            	                && !hoy.isBefore(f.getFechaInicio())
            	                && !hoy.isAfter(f.getFechaFin())).findFirst().orElse(null);
            	mav.addObject("festivalActual", festivalActual);
            }else{
                mav.addObject("festivales", List.of());
            }
        }
        return mav;
    }
    @GetMapping("/recibo/{idFestival}")
    @ResponseBody
    public ResponseEntity<byte[]> descargarRecibo(
            @PathVariable Long idFestival,
            Authentication authentication) {

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null || user.getEmpleado() == null) {
            return ResponseEntity.notFound().build();
        }

        Empleado empleado = user.getEmpleado();
        UnidadVenta unidad = empleado.getUnidadVentaACargo();

        if (unidad == null) {
            return ResponseEntity.notFound().build();
        }


        Festival festival = festivalService.traerFestivalesPorUnidad(unidad.getIdUnidadVenta()).stream().filter(f -> f.getIdFestival().equals(idFestival)).findFirst().orElse(null);

        if (festival == null) {
            return ResponseEntity.notFound().build();
        }
        
        LocalDate hoy = LocalDate.now();

        if (festival.getFechaFin() == null ||
                !festival.getFechaFin().isBefore(hoy)) {
            return ResponseEntity.status(403).build();
        }

        byte[] pdf = reciboSueldoPdfService.generarRecibo(
                empleado, festival);

        return ResponseEntity.ok().header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"recibo-festival-"
                            + idFestival + ".pdf\"").contentType(MediaType.APPLICATION_PDF).body(pdf);
    }
}
