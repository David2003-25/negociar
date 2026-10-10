package com.grupo05.negociar.repository.distribucion;

import com.grupo05.negociar.model.entidades.distribucion.SolicitudDistribucion;
import com.grupo05.negociar.model.enums.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudDistribucionRepository extends JpaRepository<SolicitudDistribucion, Long> {

    Optional<SolicitudDistribucion> findByCodigoSolicitud(String codigoSolicitud);

    boolean existsByCodigoSolicitud(String codigoSolicitud);

    List<SolicitudDistribucion> findByEstado(EstadoSolicitud estado);
}
