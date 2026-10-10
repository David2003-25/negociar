package com.grupo05.negociar.controller.distribucion;

import com.grupo05.negociar.common.exception.ReglaNegocioException;
import com.grupo05.negociar.model.entidades.seguridad.Usuario;
import com.grupo05.negociar.service.distribucion.PickingService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pickings")
@RequiredArgsConstructor
public class PickingController {

    private final PickingService pickingService;
    private final com.grupo05.negociar.service.inventario.ExistenciaService existenciaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pickings", pickingService.listarTodos());
        return "distribucion/pickings";
    }

    @PostMapping("/crear")
    public String crear(@RequestParam Long idSolicitud,
                        @RequestParam String responsable,
                        RedirectAttributes flash) {
        try {
            pickingService.crearPicking(idSolicitud, responsable);
            flash.addFlashAttribute("exito", "Orden de picking creada exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/pickings";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("picking", pickingService.obtenerPorId(id));
        model.addAttribute("existencias", existenciaService.listarTodas());
        return "distribucion/picking-detalle";
    }

    @PostMapping("/{id}/recoger")
    public String recoger(@PathVariable Long id,
                          @RequestParam Long idDetalleSolicitud,
                          @RequestParam Long idExistencia,
                          @RequestParam long cantidadRecogida,
                          RedirectAttributes flash) {
        try {
            pickingService.registrarRecogidaYReserva(id, idDetalleSolicitud, idExistencia, cantidadRecogida);
            flash.addFlashAttribute("exito", "Recogida registrada exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/pickings/" + id;
    }

    @PostMapping("/{id}/despachar")
    public String despachar(@PathVariable Long id,
                            HttpSession session,
                            RedirectAttributes flash) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
            pickingService.confirmarDespacho(id, usuario.getIdUsuario());
            flash.addFlashAttribute("exito", "Despacho confirmado exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/pickings";
    }

    @PostMapping("/{id}/liberar")
    public String liberar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            pickingService.liberarReservas(id);
            flash.addFlashAttribute("exito", "Reservas liberadas exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/pickings/" + id;
    }
}
