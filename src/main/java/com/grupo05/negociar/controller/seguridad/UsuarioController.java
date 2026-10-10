package com.grupo05.negociar.controller.seguridad;

import com.grupo05.negociar.common.exception.ReglaNegocioException;
import com.grupo05.negociar.model.entidades.seguridad.Usuario;
import com.grupo05.negociar.service.seguridad.RolService;
import com.grupo05.negociar.service.seguridad.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolService rolService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("roles", rolService.listarActivos());
        model.addAttribute("usuario", new Usuario());
        return "seguridad/usuarios";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Usuario usuario,
                          @RequestParam Long idRol,
                          RedirectAttributes flash) {
        try {
            usuarioService.registrar(usuario, idRol);
            flash.addFlashAttribute("exito", "Usuario registrado exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id,
                            @ModelAttribute Usuario usuario,
                            @RequestParam Long idRol,
                            RedirectAttributes flash) {
        try {
            usuarioService.modificar(id, usuario, idRol);
            flash.addFlashAttribute("exito", "Usuario modificado exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam boolean estado,
                                RedirectAttributes flash) {
        try {
            usuarioService.cambiarEstado(id, estado);
            flash.addFlashAttribute("exito", "Estado del usuario actualizado");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios";
    }
}
