package com.grupo05.negociar.service.distribucion;

import com.grupo05.negociar.model.entidades.distribucion.Picking;

import java.util.List;

public interface PickingService {

    Picking crearPicking(Long idSolicitud, String responsable);

    void registrarRecogidaYReserva(Long idPicking, Long idDetalleSolicitud, Long idExistencia, long cantidadRecogida);

    void confirmarDespacho(Long idPicking, Long idUsuario);

    void liberarReservas(Long idPicking);

    Picking obtenerPorId(Long id);

    List<Picking> listarPorSolicitud(Long idSolicitud);

    List<Picking> listarTodos();
}
