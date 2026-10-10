package com.grupo05.negociar.repository.inventario;

import com.grupo05.negociar.model.entidades.inventario.Existencia;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExistenciaRepository extends JpaRepository<Existencia, Long> {

    Optional<Existencia> findByLote_IdLoteAndUbicacion_IdUbicacion(Long idLote, Long idUbicacion);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Existencia e WHERE e.idExistencia = :idExistencia")
    Optional<Existencia> findByIdWithLock(@Param("idExistencia") Long idExistencia);

    @Query("SELECT e FROM Existencia e JOIN e.lote l " +
           "WHERE e.producto.idProducto = :idProducto " +
           "AND e.ubicacion.idUbicacion = :idUbicacion " +
           "AND l.estado = true " +
           "AND (l.fechaVencimiento IS NULL OR l.fechaVencimiento > CURRENT_DATE) " +
           "AND (e.stockFisico - e.stockReservado) > 0 " +
           "ORDER BY l.fechaVencimiento ASC NULLS LAST, l.fechaRecepcion ASC")
    List<Existencia> findStockDisponibleFefoFifo(@Param("idProducto") Long idProducto, @Param("idUbicacion") Long idUbicacion);

    List<Existencia> findByProducto_IdProducto(Long idProducto);

    List<Existencia> findByUbicacion_IdUbicacion(Long idUbicacion);

    boolean existsByUbicacion_IdUbicacionAndStockFisicoGreaterThan(Long idUbicacion, Long saldo);
}
