package com.grupo05.negociar.service.inventario;

import com.grupo05.negociar.model.entidades.inventario.Lote;

import java.time.LocalDate;
import java.util.List;

public interface LoteService {

    Lote registrarOObtener(Long idProducto, String codigoLote, LocalDate fechaFab, LocalDate fechaVto, String observacion);

    void cambiarEstado(Long idLote, boolean estado, String motivo, Long idUsuario);

    Lote obtenerPorId(Long id);

    List<Lote> listarPorProducto(Long idProducto);

    List<Lote> listarLotesProximosAVencer(LocalDate fechaLimite);

    List<Lote> listarTodos();
}
