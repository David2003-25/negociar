package com.grupo05.negociar.controller.catalogo;

import com.grupo05.negociar.common.exception.ReglaNegocioException;
import com.grupo05.negociar.model.entidades.catalogo.Categoria;
import com.grupo05.negociar.service.catalogo.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("categoria", new Categoria());
        return "catalogo/categorias";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Categoria categoria, RedirectAttributes flash) {
        try {
            categoriaService.registrar(categoria);
            flash.addFlashAttribute("exito", "Categoría registrada exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/categorias";
    }

    @PostMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id,
                            @ModelAttribute Categoria categoria,
                            RedirectAttributes flash) {
        try {
            categoriaService.modificar(id, categoria);
            flash.addFlashAttribute("exito", "Categoría modificada exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/categorias";
    }

    @PostMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            categoriaService.desactivar(id);
            flash.addFlashAttribute("exito", "Categoría desactivada exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/categorias";
    }

    @PostMapping("/activar/{id}")
    public String activar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            categoriaService.activar(id);
            flash.addFlashAttribute("exito", "Categoría activada exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/categorias";
    }
}
