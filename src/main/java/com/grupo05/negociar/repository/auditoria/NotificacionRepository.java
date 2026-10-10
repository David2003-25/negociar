package com.grupo05.negociar.repository.auditoria;

import com.grupo05.negociar.model.entidades.auditoria.Notificacion;
import com.grupo05.negociar.model.enums.EstadoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByEstado(EstadoNotificacion estado);

    List<Notificacion> findByProducto_IdProducto(Long idProducto);
}
