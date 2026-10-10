package com.grupo05.negociar.repository.movimiento;

import com.grupo05.negociar.model.entidades.movimiento.DetalleMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleMovimientoRepository extends JpaRepository<DetalleMovimiento, Long> {

    List<DetalleMovimiento> findByMovimiento_IdMovimiento(Long idMovimiento);
}
