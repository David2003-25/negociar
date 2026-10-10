package com.grupo05.negociar.controller.catalogo;

import com.grupo05.negociar.common.exception.ReglaNegocioException;
import com.grupo05.negociar.model.entidades.catalogo.Ubicacion;
import com.grupo05.negociar.service.catalogo.UbicacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ubicaciones")
@RequiredArgsConstructor
public class UbicacionController {

    private final UbicacionService ubicacionService;

    @GetMapping
    public String listar(@RequestParam(required = false) String tipo, Model model) {
        if (tipo != null && !tipo.isBlank()) {
            model.addAttribute("ubicaciones", ubicacionService.listarPorTipo(tipo));
        } else {
            model.addAttribute("ubicaciones", ubicacionService.listarTodas());
        }
        model.addAttribute("ubicacion", new Ubicacion());
        return "catalogo/ubicaciones";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Ubicacion ubicacion, RedirectAttributes flash) {
        try {
            ubicacionService.registrar(ubicacion);
            flash.addFlashAttribute("exito", "Ubicación registrada exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ubicaciones";
    }

    @PostMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id,
                            @ModelAttribute Ubicacion ubicacion,
                            RedirectAttributes flash) {
        try {
            ubicacionService.modificar(id, ubicacion);
            flash.addFlashAttribute("exito", "Ubicación modificada exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ubicaciones";
    }

    @PostMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            ubicacionService.desactivar(id);
            flash.addFlashAttribute("exito", "Ubicación desactivada exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ubicaciones";
    }

    @PostMapping("/activar/{id}")
    public String activar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            ubicacionService.activar(id);
            flash.addFlashAttribute("exito", "Ubicación activada exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ubicaciones";
    }
}
