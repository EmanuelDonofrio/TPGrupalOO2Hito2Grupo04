
package com.epicentroGourmet.controllers;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.epicentroGourmet.entities.CierreCaja;
import com.epicentroGourmet.entities.Pedido;
import com.epicentroGourmet.services.implementation.CierreCajaService;

@Controller
@RequestMapping("/empleado/caja")
@PreAuthorize("hasRole('EMPLEADO')")
public class CierreCajaController {

    private final CierreCajaService cierreCajaService;

    public CierreCajaController(CierreCajaService cierreCajaService) {
        this.cierreCajaService = cierreCajaService;
    }

    @GetMapping("/cerrar")
    public ModelAndView mostrarCierre(Authentication authentication) {

        ModelAndView mav =
            new ModelAndView("empleado/cierre-caja");

        try {
            boolean abierta = cierreCajaService
                .cajaAbiertaParaCajero(authentication.getName());

            mav.addObject("cajaAbierta", abierta);

            List<Pedido> pedidos =
                cierreCajaService.obtenerPedidosPendientes(
                    authentication.getName()
                );

            double total = pedidos.stream()
                .mapToDouble(Pedido::getTotal)
                .sum();

            mav.addObject("pedidos", pedidos);
            mav.addObject("cantidadPedidos", pedidos.size());
            mav.addObject("total", total);

        } catch (IllegalArgumentException e) {

            mav.addObject("error", e.getMessage());
            mav.addObject("pedidos", List.of());
            mav.addObject("cantidadPedidos", 0);
            mav.addObject("total", 0.0);
            mav.addObject("cajaAbierta", false);
        }

        return mav;
    }

    @PostMapping("/confirmar")
    public String confirmarCierre(
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            CierreCaja cierre =
                cierreCajaService.cerrarCaja(authentication.getName());

            redirectAttributes.addFlashAttribute(
                "exito",
                "Caja cerrada correctamente. Total: $"
                    + cierre.getTotalRecaudado()
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                "error",
                e.getMessage()
            );
        }

        return "redirect:/empleado/caja/cerrar";
    }

    @PostMapping("/abrir")
    public String abrirCaja(
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            cierreCajaService.abrirCaja(authentication.getName());

            redirectAttributes.addFlashAttribute(
                "exito",
                "Caja abierta correctamente. Ya podés generar pedidos."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                "error",
                e.getMessage()
            );
        }

        return "redirect:/empleado/caja/cerrar";
    }
    

}

