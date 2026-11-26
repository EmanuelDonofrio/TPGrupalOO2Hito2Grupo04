package com.epicentroGourmet.controllers.api;

import com.epicentroGourmet.services.implementation.UnidadVentaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/unidades-venta")
public class UnidadVentaRestController {

    private final UnidadVentaService unidadVentaService;

    public UnidadVentaRestController(UnidadVentaService unidadVentaService) {
        this.unidadVentaService = unidadVentaService;
    }
 /*post y get*/
}
