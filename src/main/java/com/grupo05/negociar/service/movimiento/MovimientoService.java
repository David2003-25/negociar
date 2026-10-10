package com.grupo05.negociar.service.movimiento;

import com.grupo05.negociar.model.entidades.movimiento.MovimientoInventario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface MovimientoService {

    MovimientoInventario registrarEntrada(Long idProducto, Long idProveedor, String codigoLote,
                                         LocalDate fechaFab, LocalDate fechaVto,
                                         Long idUbicacion, long cantidad,
                                         String documentoReferencia, String observacion,
                                         Long idUsuario);

    MovimientoInventario registrarSalidaDirectaFEFO(Long idProducto, Long idUbicacion, long cantidadTotal,
                                                    String documentoReferencia, String observacion,
                                                    Long idUsuario);

    Page<MovimientoInventario> listarHistorial(Pageable pageable);

    MovimientoInventario obtenerPorId(Long id);
}
