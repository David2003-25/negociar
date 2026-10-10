package com.grupo05.negociar.repository.distribucion;

import com.grupo05.negociar.model.entidades.distribucion.DetalleSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetalleSolicitudRepository extends JpaRepository<DetalleSolicitud, Long> {

    List<DetalleSolicitud> findBySolicitud_IdSolicitud(Long idSolicitud);

    Optional<DetalleSolicitud> findBySolicitud_IdSolicitudAndProducto_IdProducto(Long idSolicitud, Long idProducto);
}
