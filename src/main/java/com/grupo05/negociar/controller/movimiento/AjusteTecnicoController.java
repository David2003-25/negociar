package com.grupo05.negociar.controller.movimiento;

import com.grupo05.negociar.common.exception.ReglaNegocioException;
import com.grupo05.negociar.model.entidades.seguridad.Usuario;
import com.grupo05.negociar.service.movimiento.AjusteTecnicoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ajustes")
@RequiredArgsConstructor
public class AjusteTecnicoController {

    private final AjusteTecnicoService ajusteTecnicoService;
    private final com.grupo05.negociar.service.inventario.ExistenciaService existenciaService;
    private final com.grupo05.negociar.service.seguridad.UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ajustes", ajusteTecnicoService.listarTodos());
        return "movimiento/ajustes";
    }

    @GetMapping("/solicitar")
    public String formularioSolicitar(Model model) {
        model.addAttribute("existencias", existenciaService.listarTodas());
        model.addAttribute("usuarios", usuarioService.listarActivos());
        return "movimiento/ajuste-form";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam Long idExistencia,
                          @RequestParam long cantidadConteo,
                          @RequestParam String motivo,
                          @RequestParam(required = false) String observacion,
                          @RequestParam Long idUsuarioAutorizante,
                          HttpSession session,
                          RedirectAttributes flash) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
            ajusteTecnicoService.registrarAjuste(idExistencia, cantidadConteo, motivo,
                    observacion, usuario.getIdUsuario(), idUsuarioAutorizante);
            flash.addFlashAttribute("exito", "Ajuste técnico registrado exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ajustes";
    }
}
