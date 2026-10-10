package com.grupo05.negociar.repository.distribucion;

import com.grupo05.negociar.model.entidades.distribucion.Picking;
import com.grupo05.negociar.model.enums.EstadoPicking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PickingRepository extends JpaRepository<Picking, Long> {

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"solicitud"})
    List<Picking> findAll();

    Optional<Picking> findByCodigoPicking(String codigoPicking);

    boolean existsByCodigoPicking(String codigoPicking);

    List<Picking> findBySolicitud_IdSolicitud(Long idSolicitud);

    List<Picking> findByEstado(EstadoPicking estado);
}
