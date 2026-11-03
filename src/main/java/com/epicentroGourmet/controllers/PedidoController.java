package com.epicentroGourmet.controllers;

import com.epicentroGourmet.entities.Cajero;
import com.epicentroGourmet.entities.Empleado;
import com.epicentroGourmet.entities.UnidadVenta;
import com.epicentroGourmet.entities.User;
import com.epicentroGourmet.repositories.IPlatoRepository;
import com.epicentroGourmet.repositories.IUserRepository;
import com.epicentroGourmet.services.implementation.PedidoService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/empleado/pedidos")
@PreAuthorize("hasRole('EMPLEADO')")
public class PedidoController {

    private final IUserRepository userRepository;
    private final IPlatoRepository platoRepository;
    private final PedidoService pedidoService;

    public PedidoController(
            IUserRepository userRepository,
            IPlatoRepository platoRepository,
            PedidoService pedidoService) {

        this.userRepository = userRepository;
        this.platoRepository = platoRepository;
        this.pedidoService = pedidoService;
    }

    @GetMapping("/nuevo")
    public ModelAndView nuevoPedido(Authentication authentication) {

        User user = userRepository.findByUsername(
                authentication.getName());

        if (user == null ||
                !(user.getEmpleado() instanceof Cajero)) {
            return new ModelAndView("redirect:/empleado/mi-informacion");
        }

        Empleado empleado = user.getEmpleado();
        UnidadVenta unidad = empleado.getUnidadVentaACargo();

        if (unidad == null) {
            ModelAndView mav = new ModelAndView("empleado/nuevo-pedido");
            mav.addObject("error",
                    "No tenés una unidad de venta asignada");
            mav.addObject("platos", List.of());
            return mav;
        }

        ModelAndView mav =
                new ModelAndView("empleado/nuevo-pedido");

        mav.addObject("unidad", unidad);
        mav.addObject("platos",
                platoRepository.findByUnidadVenta(unidad));

        return mav;
    }

    @PostMapping("/crear")
    public String crearPedido(
            @RequestParam(name = "idsPlatos", required = false)
            List<Long> idsPlatos,

            @RequestParam(name = "cantidades", required = false)
            List<Integer> cantidades,

            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            pedidoService.crearPedido(
                    authentication.getName(),
                    idsPlatos,
                    cantidades
            );

            redirectAttributes.addFlashAttribute(
                    "exito", "Pedido creado correctamente");

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/empleado/pedidos/nuevo";
    }
}