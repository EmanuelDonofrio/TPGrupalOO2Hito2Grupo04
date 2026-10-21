package com.epicentroGourmet.controllers;

import com.epicentroGourmet.services.IFestivalService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.epicentroGourmet.helpers.ViewRouteHelper;
import com.epicentroGourmet.services.implementation.UnidadVentaService;

@Controller
@RequestMapping("/")
public class HomeController {

	private final IFestivalService festivalService;
	private final UnidadVentaService unidadVentaService;

	public HomeController(IFestivalService festivalService,UnidadVentaService unidadVentaService) {
		this.festivalService = festivalService;
		this.unidadVentaService = unidadVentaService;
	}

	// PORTADA (al iniciar)
	@GetMapping({"", "/", "/inicio"})
	public ModelAndView inicio(){
		ModelAndView modelAndView = new ModelAndView(ViewRouteHelper.INICIO);
		modelAndView.addObject("festivales", festivalService.getAll());
		return modelAndView;
	}

	// Panel POST-LOGIN (Dashboard general por roles)
	@GetMapping("/index")
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView(ViewRouteHelper.INDEX);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication != null ? authentication.getPrincipal() : null;
		if (principal instanceof User user) {
			modelAndView.addObject("username", user.getUsername());
		} else {
			modelAndView.addObject("username", "anonymous");
		}

		return modelAndView;
	}



	// VISTA PRINCIPAL ADMINISTRADOR
	@GetMapping("/admin/festivales")
	public ModelAndView verHomeAdmin() {
		ModelAndView mav = new ModelAndView("home/home_admin");
		mav.addObject("unidades", unidadVentaService.listarTodas());
		return mav;
	}
	
}
