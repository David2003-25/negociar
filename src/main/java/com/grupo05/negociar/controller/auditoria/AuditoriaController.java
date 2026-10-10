package com.grupo05.negociar.controller.auditoria;

import com.grupo05.negociar.service.auditoria.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auditoria")
@RequiredArgsConstructor
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @GetMapping
    public String listar(@PageableDefault(size = 20) Pageable pageable, Model model) {
        model.addAttribute("pagina", auditoriaService.listarPaginado(pageable));
        return "auditoria/auditoria";
    }
}
