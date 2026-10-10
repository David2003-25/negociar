package com.grupo05.negociar.controller.inventario;

import com.grupo05.negociar.common.exception.ReglaNegocioException;
import com.grupo05.negociar.model.entidades.seguridad.Usuario;
import com.grupo05.negociar.service.inventario.LoteService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/lotes")
@RequiredArgsConstructor
public class LoteController {

    private final LoteService loteService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lotes", loteService.listarTodos());
        return "inventario/lotes";
    }

    @PostMapping("/{id}/bloquear")
    public String bloquear(@PathVariable Long id,
                           @RequestParam String motivo,
                           HttpSession session,
                           RedirectAttributes flash) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
            loteService.cambiarEstado(id, false, motivo, usuario.getIdUsuario());
            flash.addFlashAttribute("exito", "Lote bloqueado exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/lotes";
    }

    @PostMapping("/{id}/liberar")
    public String liberar(@PathVariable Long id,
                          @RequestParam String motivo,
                          HttpSession session,
                          RedirectAttributes flash) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
            loteService.cambiarEstado(id, true, motivo, usuario.getIdUsuario());
            flash.addFlashAttribute("exito", "Lote liberado exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/lotes";
    }
}
