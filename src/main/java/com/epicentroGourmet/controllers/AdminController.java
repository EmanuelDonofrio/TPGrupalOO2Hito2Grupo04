package com.epicentroGourmet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.PostMapping;

import com.epicentroGourmet.services.IFestivalService;
import com.epicentroGourmet.services.implementation.UnidadVentaService;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.epicentroGourmet.dto.RegistroUnidadVentaDTO;

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

		mav.addObject("festival", festivalService.getAll().stream()
			.filter(f -> f.getIdFestival().equals(id))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("Festival no encontrado")));

		return mav;
	}

	//VER RANKING DE UNIDADES DE VENTA POR FESTIVAL
	
	@GetMapping("/{id}/ranking")
	public ModelAndView verRankingUnidades(@PathVariable Long id) {

		ModelAndView mav = new ModelAndView("administrador/rankingUnidades");

		// Obtener el festival seleccionado
		mav.addObject("festival", festivalService.getAll()
				.stream()
				.filter(f -> f.getIdFestival().equals(id))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Festival no encontrado")));

		// Por ahora, el ranking se conectará en el siguiente paso
		mav.addObject(
			"ranking",
			unidadVentaService.obtenerRankingPorFestival(id)
		);

		return mav;
	}

	//VOLVER A FESTIVALES DESDE RANING
	@GetMapping("/{id}/volver")
	public ModelAndView volverAFestivales(@PathVariable Long id) {
		ModelAndView mav = new ModelAndView("redirect:/administrador/home_admin");
		return mav;
	}

	// VER UNIDADES CON MAYOR CANON POR FESTIVAL
	@GetMapping("/{id}/mayores-canon")
	public ModelAndView verMayoresCanon(
			@PathVariable Long id,
			@RequestParam(defaultValue = "3") int cantidad) {

		ModelAndView mav = new ModelAndView("administrador/mayoresCanon");

		mav.addObject("festival", festivalService.getAll()
				.stream()
				.filter(f -> f.getIdFestival().equals(id))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Festival no encontrado")));

		mav.addObject("unidades",
				unidadVentaService.obtenerUnidadesConMayorCanon(id, cantidad));

		mav.addObject("cantidad", cantidad);

		return mav;
	}

	@GetMapping("/{id}/asignar-unidades")
	public ModelAndView verUnidadesDisponibles(@PathVariable Long id) {

		ModelAndView mav = new ModelAndView("administrador/asignarUnidades");

		mav.addObject("festival", festivalService.getAll().stream()
				.filter(f -> f.getIdFestival().equals(id))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Festival no encontrado")));

		mav.addObject("unidades",
				unidadVentaService.obtenerUnidadesDisponiblesParaFestival(id));

		return mav;
	}

	@PostMapping("/{id}/asignar-unidades")
	public String asignarUnidades(
			@PathVariable Long id,
			@RequestParam(name = "unidadesSeleccionadas", required = false) List<Long> idsUnidades) {

		unidadVentaService.asignarUnidadesAFestival(id, idsUnidades);

		return "redirect:/administrador/" + id + "/unidades";
	}

	// MOSTRAR FORMULARIO PARA CREAR UNA UNIDAD DESDE UN FESTIVAL
	@GetMapping("/{id}/crear-unidad")
	public ModelAndView mostrarFormularioCrearUnidad(@PathVariable Long id) {

		ModelAndView mav = new ModelAndView("administrador/registrarUnidadFestival");

		mav.addObject("festival", festivalService.getAll().stream()
				.filter(f -> f.getIdFestival().equals(id))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Festival no encontrado")));

		mav.addObject("unidadDTO", new RegistroUnidadVentaDTO());

		return mav;
	}

	// PROCESAR CREACIÓN DE UNIDAD Y ASOCIARLA AL FESTIVAL
	@PostMapping("/{id}/crear-unidad")
	public String crearUnidadParaFestival(
			@PathVariable Long id,
			@Valid @ModelAttribute("unidadDTO") RegistroUnidadVentaDTO dto,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {

		// Si hay errores de validación, vuelve al formulario
		if (bindingResult.hasErrors()) {
			model.addAttribute("festival", festivalService.getAll().stream()
					.filter(f -> f.getIdFestival().equals(id))
					.findFirst()
					.orElseThrow(() -> new RuntimeException("Festival no encontrado")));

			return "administrador/registrarUnidadFestival";
		}

		try {
			unidadVentaService.registrarUnidadConResponsableEnFestival(dto, id);

			redirectAttributes.addFlashAttribute(
					"mensajeExito",
					"La unidad de venta y su responsable fueron registrados y asociados al festival correctamente."
			);

			return "redirect:/administrador/" + id + "/unidades";

		} catch (IllegalArgumentException | IllegalStateException e) {

			model.addAttribute("festival", festivalService.getAll().stream()
					.filter(f -> f.getIdFestival().equals(id))
					.findFirst()
					.orElseThrow(() -> new RuntimeException("Festival no encontrado")));

			model.addAttribute("error", e.getMessage());

			return "administrador/registrarUnidadFestival";

		} catch (Exception e) {

			model.addAttribute("festival", festivalService.getAll().stream()
					.filter(f -> f.getIdFestival().equals(id))
					.findFirst()
					.orElseThrow(() -> new RuntimeException("Festival no encontrado")));

			model.addAttribute("error", "Error al registrar la unidad: " + e.getMessage());

			return "administrador/registrarUnidadFestival";
		}
	}

}
