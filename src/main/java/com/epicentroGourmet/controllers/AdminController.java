package com.epicentroGourmet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.epicentroGourmet.services.IFestivalService;
import com.epicentroGourmet.services.implementation.UnidadVentaService;

@Controller
@RequestMapping("/administrador")
public class AdminController {

    
	private final IFestivalService festivalService;
	private final UnidadVentaService unidadVentaService;

	public AdminController(IFestivalService festivalService,UnidadVentaService unidadVentaService) {
		this.festivalService = festivalService;
		this.unidadVentaService = unidadVentaService;
	}

    // VISTA PRINCIPAL ADMINISTRADOR
	@GetMapping("/home_admin")
	public ModelAndView verHomeAdmin() {
		ModelAndView mav = new ModelAndView("administrador/home_admin");
		mav.addObject("festivales", festivalService.getAll());
		return mav;
	}

	//VER UNIDADES DE VENTA POR FESTIVAL
	@GetMapping("/{id}/unidades")
	public ModelAndView verUnidadesFestival(@PathVariable Long id) {

		ModelAndView mav = new ModelAndView("administrador/unidadesVenta");

		mav.addObject("unidades", unidadVentaService.listarPorFestival(id));

		return mav;
	}
}
