package com.grupo05.negociar.repository.movimiento;

import com.grupo05.negociar.model.entidades.movimiento.AjusteTecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AjusteTecnicoRepository extends JpaRepository<AjusteTecnico, Long> {

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"movimiento", "usuario"})
    List<AjusteTecnico> findAll();

    Optional<AjusteTecnico> findByMovimiento_IdMovimiento(Long idMovimiento);

    List<AjusteTecnico> findByUsuario_IdUsuario(Long idUsuario);
}
