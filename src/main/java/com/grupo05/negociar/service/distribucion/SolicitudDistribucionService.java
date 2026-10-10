package com.grupo05.negociar.service.distribucion;

import com.grupo05.negociar.model.entidades.distribucion.SolicitudDistribucion;
import com.grupo05.negociar.service.distribucion.dto.LineaItemSolicitudDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SolicitudDistribucionService {

    SolicitudDistribucion registrar(SolicitudDistribucion solicitud, List<LineaItemSolicitudDTO> lineas);

    void cancelar(Long idSolicitud, Long idUsuario);

    SolicitudDistribucion obtenerPorId(Long id);

    SolicitudDistribucion obtenerPorCodigo(String codigo);

    List<SolicitudDistribucion> listarTodas();

    Page<SolicitudDistribucion> listarPaginado(Pageable pageable);
}
