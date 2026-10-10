package com.grupo05.negociar.repository.inventario;

import com.grupo05.negociar.model.entidades.inventario.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {

    Optional<Lote> findByProducto_IdProductoAndCodigoLote(Long idProducto, String codigoLote);

    boolean existsByProducto_IdProductoAndCodigoLote(Long idProducto, String codigoLote);

    List<Lote> findByProducto_IdProductoAndEstadoTrue(Long idProducto);

    List<Lote> findByFechaVencimientoLessThanEqualAndEstadoTrue(LocalDate fecha);
}
