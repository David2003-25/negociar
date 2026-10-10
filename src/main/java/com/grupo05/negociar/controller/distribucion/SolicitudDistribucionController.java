package com.grupo05.negociar.controller.distribucion;

import com.grupo05.negociar.common.exception.ReglaNegocioException;
import com.grupo05.negociar.model.entidades.distribucion.SolicitudDistribucion;
import com.grupo05.negociar.model.entidades.seguridad.Usuario;
import com.grupo05.negociar.service.catalogo.ProductoService;
import com.grupo05.negociar.service.distribucion.SolicitudDistribucionService;
import com.grupo05.negociar.service.distribucion.dto.LineaItemSolicitudDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/solicitudes")
@RequiredArgsConstructor
public class SolicitudDistribucionController {

    private final SolicitudDistribucionService solicitudService;
    private final ProductoService productoService;

    @GetMapping
    public String listar(@PageableDefault(size = 10) Pageable pageable, Model model) {
        model.addAttribute("pagina", solicitudService.listarPaginado(pageable));
        return "distribucion/solicitudes";
    }

    @GetMapping("/nueva")
    public String formularioNueva(Model model) {
        model.addAttribute("solicitud", new SolicitudDistribucion());
        model.addAttribute("productos", productoService.listarActivos());
        return "distribucion/solicitud-form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute SolicitudDistribucion solicitud,
                          @RequestParam List<Long> idProducto,
                          @RequestParam List<Long> cantidad,
                          @RequestParam(required = false) List<String> observacionLinea,
                          RedirectAttributes flash) {
        try {
            List<LineaItemSolicitudDTO> lineas = new java.util.ArrayList<>();
            for (int i = 0; i < idProducto.size(); i++) {
                LineaItemSolicitudDTO linea = new LineaItemSolicitudDTO();
                linea.setIdProducto(idProducto.get(i));
                linea.setCantidad(cantidad.get(i));
                if (observacionLinea != null && i < observacionLinea.size()) {
                    linea.setObservacion(observacionLinea.get(i));
                }
                lineas.add(linea);
            }
            solicitudService.registrar(solicitud, lineas);
            flash.addFlashAttribute("exito", "Solicitud de distribución registrada exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/solicitudes";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id,
                           HttpSession session,
                           RedirectAttributes flash) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
            solicitudService.cancelar(id, usuario.getIdUsuario());
            flash.addFlashAttribute("exito", "Solicitud cancelada exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/solicitudes";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("solicitud", solicitudService.obtenerPorId(id));
        return "distribucion/solicitud-detalle";
    }
}
