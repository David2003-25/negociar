package com.grupo05.negociar.repository.movimiento;

import com.grupo05.negociar.model.entidades.movimiento.MovimientoInventario;
import com.grupo05.negociar.model.enums.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findByTipoMovimiento(TipoMovimiento tipoMovimiento);

    List<MovimientoInventario> findByFechaMovimientoBetween(OffsetDateTime desde, OffsetDateTime hasta);
}
