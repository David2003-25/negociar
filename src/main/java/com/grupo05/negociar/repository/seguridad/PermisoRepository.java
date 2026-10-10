package com.grupo05.negociar.repository.seguridad;

import com.grupo05.negociar.model.entidades.seguridad.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {

    Optional<Permiso> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
