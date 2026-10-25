package com.epicentroGourmet.controllers;

import com.epicentroGourmet.dto.RegistroUnidadVentaDTO;
import com.epicentroGourmet.services.IUnidadVentaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/unidad-venta")
public class UnidadVentaController {

    private final IUnidadVentaService unidadVentaService;

    public UnidadVentaController(IUnidadVentaService unidadVentaService){
        this.unidadVentaService = unidadVentaService;
    }

    //Muestra el formulario vacío
    @GetMapping("/registrar")
    public String mostrarFormulario(Model model){
        model.addAttribute("unidadDTO", new RegistroUnidadVentaDTO());
        return "unidadVenta/registrarUnidadVenta";
    }

    // Procesa el envío del formulario con validaciones
    @PostMapping("/registrar")
    public String procesarRegistro(@Valid @ModelAttribute("unidadDTO") RegistroUnidadVentaDTO dto,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes,
                                   Model model){
        if(bindingResult.hasErrors()){
            return "unidadVenta/registrarUnidadVenta";
        }
        try{
            unidadVentaService.registrarUnidadConResponsable(dto);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Unidad de venta y usuario del responsable registrados con éxito! Ya puede iniciar sesión.");
            return "redirect:/login";
        }catch (Exception e){
            model.addAttribute("error", "Error al procesar el registro: " + e.getMessage());
            return "unidadVenta/registrar";
        }
    }
}
