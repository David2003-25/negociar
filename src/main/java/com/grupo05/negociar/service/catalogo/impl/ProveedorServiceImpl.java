package com.grupo05.negociar.service.catalogo.impl;

import com.grupo05.negociar.common.exception.ReglaNegocioException;
import com.grupo05.negociar.model.entidades.catalogo.Producto;
import com.grupo05.negociar.model.entidades.catalogo.ProductoProveedor;
import com.grupo05.negociar.model.entidades.catalogo.Proveedor;
import com.grupo05.negociar.repository.catalogo.ProductoProveedorRepository;
import com.grupo05.negociar.repository.catalogo.ProductoRepository;
import com.grupo05.negociar.repository.catalogo.ProveedorRepository;
import com.grupo05.negociar.service.catalogo.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;
    private final ProductoProveedorRepository productoProveedorRepository;

    @Override
    public Proveedor registrar(Proveedor proveedor) {
        validarDatosProveedor(proveedor);

        String ruc = proveedor.getRuc().trim();
        if (proveedorRepository.existsByRuc(ruc)) {
            throw new ReglaNegocioException("El RUC " + ruc + " ya se encuentra registrado");
        }

        proveedor.setRuc(ruc);
        proveedor.setRazonSocial(proveedor.getRazonSocial().trim());
        proveedor.setEstado(true);
        proveedor.setFechaCreacion(OffsetDateTime.now());
        return proveedorRepository.save(proveedor);
    }

    @Override
    public Proveedor modificar(Long id, Proveedor proveedor) {
        validarDatosProveedor(proveedor);
        Proveedor existente = obtenerPorId(id);

        String nuevoRuc = proveedor.getRuc().trim();
        if (!nuevoRuc.equals(existente.getRuc())) {
            if (proveedorRepository.existsByRuc(nuevoRuc)) {
                throw new ReglaNegocioException("El RUC " + nuevoRuc + " ya está asignado a otro proveedor");
            }
            existente.setRuc(nuevoRuc);
        }

        existente.setRazonSocial(proveedor.getRazonSocial().trim());
        existente.setTelefono(proveedor.getTelefono());
        existente.setCorreo(proveedor.getCorreo());
        existente.setDireccion(proveedor.getDireccion());
        return proveedorRepository.save(existente);
    }

    @Override
    public void cambiarEstado(Long id, boolean estado) {
        Proveedor proveedor = obtenerPorId(id);
        proveedor.setEstado(estado);
        proveedorRepository.save(proveedor);
    }

    @Override
    public Proveedor obtenerPorId(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("Proveedor no encontrado con ID: " + id));
    }

    @Override
    public List<Proveedor> listarActivos() {
        return proveedorRepository.findByEstadoTrue();
    }

    @Override
    public List<Proveedor> listarTodos() {
        return proveedorRepository.findAll();
    }

    @Override
    public ProductoProveedor asociarProducto(Long idProveedor, Long idProducto, BigDecimal precioRef, String codigoProv) {
        Proveedor proveedor = obtenerPorId(idProveedor);
        if (!Boolean.TRUE.equals(proveedor.getEstado())) {
            throw new ReglaNegocioException("No se puede asociar productos a un proveedor inactivo");
        }

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ReglaNegocioException("Producto no encontrado con ID: " + idProducto));

        ProductoProveedor asociacion = productoProveedorRepository
                .findByProducto_IdProductoAndProveedor_IdProveedor(idProducto, idProveedor)
                .orElse(new ProductoProveedor());

        asociacion.setProveedor(proveedor);
        asociacion.setProducto(producto);
        asociacion.setPrecioReferencia(precioRef);
        asociacion.setCodigoProductoProveedor(codigoProv);
        asociacion.setEstado(true);
        if (asociacion.getIdProductoProveedor() == null) {
            asociacion.setFechaRegistro(OffsetDateTime.now());
        }

        return productoProveedorRepository.save(asociacion);
    }

    @Override
    public List<ProductoProveedor> listarProductosPorProveedor(Long idProveedor) {
        return productoProveedorRepository.findByProveedor_IdProveedorAndEstadoTrue(idProveedor);
    }

    private void validarDatosProveedor(Proveedor proveedor) {
        if (proveedor.getRazonSocial() == null || proveedor.getRazonSocial().trim().isEmpty()) {
            throw new ReglaNegocioException("La razón social es obligatoria");
        }
        if (proveedor.getRazonSocial().trim().length() > 150) {
            throw new ReglaNegocioException("La razón social no debe exceder 150 caracteres");
        }
        if (proveedor.getRuc() == null || proveedor.getRuc().trim().length() != 11) {
            throw new ReglaNegocioException("El RUC debe tener exactamente 11 dígitos");
        }
        if (!proveedor.getRuc().trim().matches("\\d{11}")) {
            throw new ReglaNegocioException("El RUC debe contener solo números");
        }
    }
}
