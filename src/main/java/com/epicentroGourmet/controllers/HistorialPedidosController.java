package com.epicentroGourmet.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.epicentroGourmet.services.implementation.PedidoService;

@Controller
public class HistorialPedidosController {

    private final PedidoService pedidoService;

    public HistorialPedidosController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/empleado/historial-pedidos")
    public ModelAndView verHistorial(Principal principal) {

        ModelAndView mav =
                new ModelAndView("empleado/historial-pedidos");

        mav.addObject("pedidos",
                pedidoService.obtenerHistorialPedidos(
                        principal.getName()));

        return mav;
    }
}