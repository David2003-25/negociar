package com.grupo05.negociar.service.catalogo.impl;

import com.grupo05.negociar.common.exception.ReglaNegocioException;
import com.grupo05.negociar.model.entidades.catalogo.Categoria;
import com.grupo05.negociar.repository.catalogo.CategoriaRepository;
import com.grupo05.negociar.service.catalogo.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Override
    public Categoria registrar(Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            throw new ReglaNegocioException("El nombre de la categoría es obligatorio");
        }
        if (categoria.getNombre().trim().length() > 100) {
            throw new ReglaNegocioException("El nombre de la categoría no debe exceder 100 caracteres");
        }
        if (categoriaRepository.existsByNombre(categoria.getNombre().trim())) {
            throw new ReglaNegocioException("Ya existe una categoría con ese nombre");
        }

        categoria.setNombre(categoria.getNombre().trim());
        categoria.setEstado(true);
        categoria.setFechaCreacion(OffsetDateTime.now());
        return categoriaRepository.save(categoria);
    }

    @Override
    public Categoria modificar(Long id, Categoria categoria) {
        Categoria existente = obtenerPorId(id);

        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            throw new ReglaNegocioException("El nombre de la categoría es obligatorio");
        }
        if (categoria.getNombre().trim().length() > 100) {
            throw new ReglaNegocioException("El nombre de la categoría no debe exceder 100 caracteres");
        }

        String nuevoNombre = categoria.getNombre().trim();
        if (!nuevoNombre.equalsIgnoreCase(existente.getNombre())) {
            if (categoriaRepository.existsByNombre(nuevoNombre)) {
                throw new ReglaNegocioException("Ya existe una categoría con el nombre: " + nuevoNombre);
            }
            existente.setNombre(nuevoNombre);
        }

        existente.setDescripcion(categoria.getDescripcion());
        return categoriaRepository.save(existente);
    }

    @Override
    public void desactivar(Long id) {
        Categoria categoria = obtenerPorId(id);
        categoria.setEstado(false);
        categoriaRepository.save(categoria);
    }

    @Override
    public void activar(Long id) {
        Categoria categoria = obtenerPorId(id);
        categoria.setEstado(true);
        categoriaRepository.save(categoria);
    }

    @Override
    public Categoria obtenerPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("Categoría no encontrada con ID: " + id));
    }

    @Override
    public List<Categoria> listarActivas() {
        return categoriaRepository.findByEstadoTrue();
    }

    @Override
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }
}
