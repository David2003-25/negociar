package com.grupo05.negociar.repository.catalogo;

import com.grupo05.negociar.model.entidades.catalogo.ProductoProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoProveedorRepository extends JpaRepository<ProductoProveedor, Long> {

    Optional<ProductoProveedor> findByProducto_IdProductoAndProveedor_IdProveedor(Long idProducto, Long idProveedor);

    boolean existsByProducto_IdProductoAndProveedor_IdProveedor(Long idProducto, Long idProveedor);

    List<ProductoProveedor> findByProveedor_IdProveedorAndEstadoTrue(Long idProveedor);

    List<ProductoProveedor> findByProducto_IdProductoAndEstadoTrue(Long idProducto);
}
