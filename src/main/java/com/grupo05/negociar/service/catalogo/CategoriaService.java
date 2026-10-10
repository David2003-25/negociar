package com.grupo05.negociar.service.catalogo;

import com.grupo05.negociar.model.entidades.catalogo.Categoria;

import java.util.List;

public interface CategoriaService {

    Categoria registrar(Categoria categoria);

    Categoria modificar(Long id, Categoria categoria);

    void desactivar(Long id);

    void activar(Long id);

    Categoria obtenerPorId(Long id);

    List<Categoria> listarActivas();

    List<Categoria> listarTodas();
}
