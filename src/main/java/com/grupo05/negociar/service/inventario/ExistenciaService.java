package com.grupo05.negociar.service.inventario;

import com.grupo05.negociar.model.entidades.inventario.Existencia;

import java.util.List;

public interface ExistenciaService {

    Existencia obtenerOCrear(Long idProducto, Long idLote, Long idUbicacion);

    Existencia modificarStockFisico(Long idExistencia, long cantidadDelta);

    Existencia modificarStockReservado(Long idExistencia, long cantidadDelta);

    List<Existencia> consultarStockPorProducto(Long idProducto);

    List<Existencia> consultarStockPorUbicacion(Long idUbicacion);

    Existencia obtenerPorId(Long id);

    List<Existencia> listarTodas();
}
