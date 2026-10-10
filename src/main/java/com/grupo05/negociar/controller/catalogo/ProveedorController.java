package com.grupo05.negociar.controller.catalogo;

import com.grupo05.negociar.common.exception.ReglaNegocioException;
import com.grupo05.negociar.model.entidades.catalogo.Proveedor;
import com.grupo05.negociar.service.catalogo.ProductoService;
import com.grupo05.negociar.service.catalogo.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;
    private final ProductoService productoService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("proveedores", proveedorService.listarTodos());
        model.addAttribute("productos", productoService.listarActivos());
        model.addAttribute("proveedor", new Proveedor());
        return "catalogo/proveedores";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Proveedor proveedor, RedirectAttributes flash) {
        try {
            proveedorService.registrar(proveedor);
            flash.addFlashAttribute("exito", "Proveedor registrado exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/proveedores";
    }

    @PostMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id,
                            @ModelAttribute Proveedor proveedor,
                            RedirectAttributes flash) {
        try {
            proveedorService.modificar(id, proveedor);
            flash.addFlashAttribute("exito", "Proveedor modificado exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/proveedores";
    }

    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam boolean estado,
                                RedirectAttributes flash) {
        try {
            proveedorService.cambiarEstado(id, estado);
            flash.addFlashAttribute("exito", "Estado del proveedor actualizado");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/proveedores";
    }

    @PostMapping("/asociar-producto")
    public String asociarProducto(@RequestParam Long idProveedor,
                                  @RequestParam Long idProducto,
                                  @RequestParam(required = false) BigDecimal precioRef,
                                  @RequestParam(required = false) String codigoProv,
                                  RedirectAttributes flash) {
        try {
            proveedorService.asociarProducto(idProveedor, idProducto, precioRef, codigoProv);
            flash.addFlashAttribute("exito", "Producto asociado al proveedor exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/proveedores";
    }
}
