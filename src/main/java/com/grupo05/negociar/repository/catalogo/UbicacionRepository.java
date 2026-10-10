package com.grupo05.negociar.repository.catalogo;

import com.grupo05.negociar.model.entidades.catalogo.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Long> {

    Optional<Ubicacion> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<Ubicacion> findByEstadoTrue();

    List<Ubicacion> findByTipo(String tipo);
}
