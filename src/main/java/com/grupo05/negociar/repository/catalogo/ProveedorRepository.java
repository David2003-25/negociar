package com.grupo05.negociar.repository.catalogo;

import com.grupo05.negociar.model.entidades.catalogo.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    Optional<Proveedor> findByRuc(String ruc);

    boolean existsByRuc(String ruc);

    List<Proveedor> findByEstadoTrue();
}
