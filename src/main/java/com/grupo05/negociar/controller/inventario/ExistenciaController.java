package com.grupo05.negociar.controller.inventario;

import com.grupo05.negociar.service.inventario.ExistenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/inventario")
@RequiredArgsConstructor
public class ExistenciaController {

    private final ExistenciaService existenciaService;
    private final com.grupo05.negociar.service.catalogo.ProductoService productoService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("existencias", existenciaService.listarTodas());
        model.addAttribute("productos", productoService.listarActivos());
        return "inventario/existencias";
    }

    @GetMapping("/producto/{id}")
    public String detallePorProducto(@PathVariable Long id, Model model) {
        model.addAttribute("existencias", existenciaService.consultarStockPorProducto(id));
        model.addAttribute("productos", productoService.listarActivos());
        model.addAttribute("idProductoSeleccionado", id);
        return "inventario/existencias";
    }
}
