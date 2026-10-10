package com.grupo05.negociar.controller.inventario;

import com.grupo05.negociar.service.inventario.AlertaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService alertaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("alertasStock", alertaService.obtenerAlertasBajoStock());
        model.addAttribute("lotesPorVencer", alertaService.obtenerLotesPorVencer(30));
        return "inventario/alertas";
    }
}
