package com.grupo05.negociar.controller.catalogo;

import com.grupo05.negociar.common.exception.ReglaNegocioException;
import com.grupo05.negociar.model.entidades.catalogo.Producto;
import com.grupo05.negociar.service.catalogo.CategoriaService;
import com.grupo05.negociar.service.catalogo.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    @GetMapping
    public String listar(@PageableDefault(size = 10) Pageable pageable, Model model) {
        model.addAttribute("pagina", productoService.listarPaginado(pageable));
        model.addAttribute("categorias", categoriaService.listarActivas());
        return "catalogo/productos";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.listarActivas());
        return "catalogo/producto-form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto,
                          @RequestParam Long idCategoria,
                          RedirectAttributes flash) {
        try {
            productoService.registrar(producto, idCategoria);
            flash.addFlashAttribute("exito", "Producto registrado exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/productos";
    }

    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoService.obtenerPorId(id));
        model.addAttribute("categorias", categoriaService.listarActivas());
        return "catalogo/producto-form";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute Producto producto,
                             @RequestParam Long idCategoria,
                             RedirectAttributes flash) {
        try {
            productoService.modificar(id, producto, idCategoria);
            flash.addFlashAttribute("exito", "Producto modificado exitosamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/productos";
    }

    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam boolean estado,
                                RedirectAttributes flash) {
        try {
            productoService.cambiarEstado(id, estado);
            flash.addFlashAttribute("exito", "Estado del producto actualizado");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/productos";
    }
}
