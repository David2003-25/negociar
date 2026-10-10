package com.grupo05.negociar.service.catalogo.impl;

import com.grupo05.negociar.common.exception.ReglaNegocioException;
import com.grupo05.negociar.model.entidades.catalogo.Categoria;
import com.grupo05.negociar.model.entidades.catalogo.Producto;
import com.grupo05.negociar.repository.catalogo.CategoriaRepository;
import com.grupo05.negociar.repository.catalogo.ProductoRepository;
import com.grupo05.negociar.repository.inventario.LoteRepository;
import com.grupo05.negociar.service.catalogo.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final LoteRepository loteRepository;

    @Override
    public Producto registrar(Producto producto, Long idCategoria) {
        validarDatosProducto(producto);

        String codigo = producto.getCodigo().trim().toUpperCase();
        if (productoRepository.existsByCodigo(codigo)) {
            throw new ReglaNegocioException("El código de producto " + codigo + " ya existe");
        }

        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new ReglaNegocioException("La categoría seleccionada no existe"));
        if (!Boolean.TRUE.equals(categoria.getEstado())) {
            throw new ReglaNegocioException("No se puede asignar una categoría inactiva");
        }

        producto.setCodigo(codigo);
        producto.setNombre(producto.getNombre().trim());
        producto.setUnidadPresentacion(producto.getUnidadPresentacion().trim());
        producto.setCategoria(categoria);
        producto.setEstado(true);
        producto.setFechaCreacion(OffsetDateTime.now());

        return productoRepository.save(producto);
    }

    @Override
    public Producto modificar(Long id, Producto producto, Long idCategoria) {
        validarDatosProducto(producto);
        Producto existente = obtenerPorId(id);

        String nuevoCodigo = producto.getCodigo().trim().toUpperCase();
        if (!nuevoCodigo.equals(existente.getCodigo())) {
            if (productoRepository.existsByCodigo(nuevoCodigo)) {
                throw new ReglaNegocioException("El código " + nuevoCodigo + " ya está asignado a otro producto");
            }
            existente.setCodigo(nuevoCodigo);
        }

        // RF01: Con historial de lotes/movimientos no se puede cambiar la unidad de presentación
        String nuevaPresentacion = producto.getUnidadPresentacion().trim();
        if (!nuevaPresentacion.equalsIgnoreCase(existente.getUnidadPresentacion())) {
            boolean tieneHistorial = !loteRepository.findByProducto_IdProductoAndEstadoTrue(id).isEmpty();
            if (tieneHistorial) {
                throw new ReglaNegocioException("No se puede cambiar la unidad de presentación porque el producto tiene historial registrado");
            }
            existente.setUnidadPresentacion(nuevaPresentacion);
        }

        if (idCategoria != null && !idCategoria.equals(existente.getCategoria().getIdCategoria())) {
            Categoria categoria = categoriaRepository.findById(idCategoria)
                    .orElseThrow(() -> new ReglaNegocioException("Categoría no encontrada"));
            existente.setCategoria(categoria);
        }

        existente.setNombre(producto.getNombre().trim());
        existente.setDescripcion(producto.getDescripcion());
        existente.setStockMinimo(producto.getStockMinimo());

        return productoRepository.save(existente);
    }

    @Override
    public void cambiarEstado(Long id, boolean estado) {
        Producto producto = obtenerPorId(id);
        producto.setEstado(estado);
        productoRepository.save(producto);
    }

    @Override
    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("Producto no encontrado con ID: " + id));
    }

    @Override
    public Producto obtenerPorCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ReglaNegocioException("Producto no encontrado con código: " + codigo));
    }

    @Override
    public List<Producto> listarActivos() {
        return productoRepository.findByEstadoTrue();
    }

    @Override
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @Override
    public Page<Producto> listarPaginado(Pageable pageable) {
        return productoRepository.findByEstadoTrue(pageable);
    }

    @Override
    public List<Producto> listarPorCategoria(Long idCategoria) {
        return productoRepository.findByCategoria_IdCategoria(idCategoria);
    }

    private void validarDatosProducto(Producto producto) {
        if (producto.getCodigo() == null || producto.getCodigo().trim().isEmpty()) {
            throw new ReglaNegocioException("El código de producto es obligatorio");
        }
        if (producto.getCodigo().trim().length() > 50) {
            throw new ReglaNegocioException("El código no debe exceder 50 caracteres");
        }
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new ReglaNegocioException("El nombre del producto es obligatorio");
        }
        if (producto.getNombre().trim().length() > 150) {
            throw new ReglaNegocioException("El nombre no debe exceder 150 caracteres");
        }
        if (producto.getUnidadPresentacion() == null || producto.getUnidadPresentacion().trim().isEmpty()) {
            throw new ReglaNegocioException("La unidad de presentación es obligatoria");
        }
        if (producto.getUnidadPresentacion().trim().length() > 80) {
            throw new ReglaNegocioException("La unidad de presentación no debe exceder 80 caracteres");
        }
        if (producto.getStockMinimo() == null || producto.getStockMinimo() < 0) {
            throw new ReglaNegocioException("El stock mínimo no puede ser negativo");
        }
    }
}
