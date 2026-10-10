package com.grupo05.negociar.controller.movimiento;

import com.grupo05.negociar.common.exception.ReglaNegocioException;
import com.grupo05.negociar.model.entidades.seguridad.Usuario;
import com.grupo05.negociar.service.catalogo.ProductoService;
import com.grupo05.negociar.service.catalogo.ProveedorService;
import com.grupo05.negociar.service.catalogo.UbicacionService;
import com.grupo05.negociar.service.movimiento.MovimientoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;
    private final ProductoService productoService;
    private final ProveedorService proveedorService;
    private final UbicacionService ubicacionService;

    @GetMapping
    public String listar(@PageableDefault(size = 10) Pageable pageable, Model model) {
        model.addAttribute("pagina", movimientoService.listarHistorial(pageable));
        return "movimiento/movimientos";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("movimiento", movimientoService.obtenerPorId(id));
        return "movimiento/movimiento-detalle";
    }

    @GetMapping("/entrada")
    public String formularioEntrada(Model model) {
        model.addAttribute("productos", productoService.listarActivos());
        model.addAttribute("proveedores", proveedorService.listarActivos());
        model.addAttribute("ubicaciones", ubicacionService.listarActivas());
        return "movimiento/entrada-form";
    }

    @PostMapping("/entrada")
    public String registrarEntrada(@RequestParam Long idProducto,
                                   @RequestParam Long idProveedor,
                                   @RequestParam String codigoLote,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFab,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaVto,
                                   @RequestParam Long idUbicacion,
                                   @RequestParam long cantidad,
                                   @RequestParam(required = false) String documentoReferencia,
                                   @RequestParam(required = false) String observacion,
                                   HttpSession session,
                                   RedirectAttributes flash) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
            movimientoService.registrarEntrada(idProducto, idProveedor, codigoLote,
                    fechaFab, fechaVto, idUbicacion, cantidad,
                    documentoReferencia, observacion, usuario.getIdUsuario());
            flash.addFlashAttribute("exito", "Entrada registrada exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/movimientos";
    }

    @GetMapping("/salida")
    public String formularioSalida(Model model) {
        model.addAttribute("productos", productoService.listarActivos());
        model.addAttribute("ubicaciones", ubicacionService.listarActivas());
        return "movimiento/salida-form";
    }

    @PostMapping("/salida")
    public String registrarSalida(@RequestParam Long idProducto,
                                  @RequestParam Long idUbicacion,
                                  @RequestParam long cantidadTotal,
                                  @RequestParam(required = false) String documentoReferencia,
                                  @RequestParam(required = false) String observacion,
                                  HttpSession session,
                                  RedirectAttributes flash) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
            movimientoService.registrarSalidaDirectaFEFO(idProducto, idUbicacion, cantidadTotal,
                    documentoReferencia, observacion, usuario.getIdUsuario());
            flash.addFlashAttribute("exito", "Salida registrada exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/movimientos";
    }
}
