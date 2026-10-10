package com.grupo05.negociar.repository.inventario;

import com.grupo05.negociar.model.entidades.inventario.ReservaInventario;
import com.grupo05.negociar.model.enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaInventarioRepository extends JpaRepository<ReservaInventario, Long> {

    List<ReservaInventario> findByExistencia_IdExistenciaAndEstado(Long idExistencia, EstadoReserva estado);

    List<ReservaInventario> findByDetallePicking_IdDetallePicking(Long idDetallePicking);

    boolean existsByExistencia_Ubicacion_IdUbicacionAndEstado(Long idUbicacion, EstadoReserva estado);
}
