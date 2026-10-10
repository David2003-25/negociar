package com.grupo05.negociar.repository.seguridad;

import com.grupo05.negociar.model.entidades.seguridad.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"rol"})
    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"rol"})
    List<Usuario> findAll();

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"rol"})
    List<Usuario> findByEstadoTrue();
}
