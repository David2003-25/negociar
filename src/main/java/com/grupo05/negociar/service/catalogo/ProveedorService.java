package com.grupo05.negociar.service.catalogo;

import com.grupo05.negociar.model.entidades.catalogo.ProductoProveedor;
import com.grupo05.negociar.model.entidades.catalogo.Proveedor;

import java.math.BigDecimal;
import java.util.List;

public interface ProveedorService {

    Proveedor registrar(Proveedor proveedor);

    Proveedor modificar(Long id, Proveedor proveedor);

    void cambiarEstado(Long id, boolean estado);

    Proveedor obtenerPorId(Long id);

    List<Proveedor> listarActivos();

    List<Proveedor> listarTodos();

    ProductoProveedor asociarProducto(Long idProveedor, Long idProducto, BigDecimal precioRef, String codigoProv);

    List<ProductoProveedor> listarProductosPorProveedor(Long idProveedor);
}
