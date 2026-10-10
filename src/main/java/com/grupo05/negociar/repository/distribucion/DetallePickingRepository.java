package com.grupo05.negociar.repository.distribucion;

import com.grupo05.negociar.model.entidades.distribucion.DetallePicking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallePickingRepository extends JpaRepository<DetallePicking, Long> {

    List<DetallePicking> findByPicking_IdPicking(Long idPicking);

    List<DetallePicking> findByExistencia_IdExistencia(Long idExistencia);
}
