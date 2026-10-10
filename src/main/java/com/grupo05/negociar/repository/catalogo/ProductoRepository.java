package com.grupo05.negociar.repository.catalogo;

import com.grupo05.negociar.model.entidades.catalogo.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<Producto> findByEstadoTrue();

    Page<Producto> findByEstadoTrue(Pageable pageable);

    List<Producto> findByCategoria_IdCategoria(Long idCategoria);
}
