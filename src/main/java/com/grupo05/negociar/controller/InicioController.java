package com.grupo05.negociar.controller;

import com.grupo05.negociar.service.catalogo.ProductoService;
import com.grupo05.negociar.service.inventario.AlertaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class InicioController {

    private final AlertaService alertaService;
    private final ProductoService productoService;

    @GetMapping("/")
    public String redirigirInicio() {
        return "redirect:/inicio";
    }

    @GetMapping("/inicio")
    public String mostrarInicio(Model model) {
        model.addAttribute("alertasStock", alertaService.obtenerAlertasBajoStock());
        model.addAttribute("lotesPorVencer", alertaService.obtenerLotesPorVencer(30));
        model.addAttribute("totalProductosActivos", productoService.listarActivos().size());
        model.addAttribute("totalAlertas", alertaService.contarAlertasTotales());
        return "inicio";
    }
}
