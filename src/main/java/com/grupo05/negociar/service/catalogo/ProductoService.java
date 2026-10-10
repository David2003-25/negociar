package com.grupo05.negociar.service.catalogo;

import com.grupo05.negociar.model.entidades.catalogo.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductoService {

    Producto registrar(Producto producto, Long idCategoria);

    Producto modificar(Long id, Producto producto, Long idCategoria);

    void cambiarEstado(Long id, boolean estado);

    Producto obtenerPorId(Long id);

    Producto obtenerPorCodigo(String codigo);

    List<Producto> listarActivos();

    List<Producto> listarTodos();

    Page<Producto> listarPaginado(Pageable pageable);

    List<Producto> listarPorCategoria(Long idCategoria);
}
