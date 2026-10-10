package com.grupo05.negociar.service.auditoria;

import com.grupo05.negociar.model.entidades.auditoria.AuditoriaEvento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuditoriaService {

    void registrar(Long idUsuario, String entidad, String idRegistro, String accion,
                   String detalle, String datosAnteriores, String datosNuevos);

    Page<AuditoriaEvento> listarPaginado(Pageable pageable);

    List<AuditoriaEvento> listarPorEntidadYRegistro(String entidad, String idRegistro);
}
