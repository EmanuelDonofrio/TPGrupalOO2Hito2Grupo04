package com.epicentroGourmet.controllers;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@PreAuthorize("hasRole('RESPONSABLE')")
@RequestMapping("/plato")
public class PlatoController {
}
