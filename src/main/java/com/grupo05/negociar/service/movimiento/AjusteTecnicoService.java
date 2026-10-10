package com.grupo05.negociar.service.movimiento;

import com.grupo05.negociar.model.entidades.movimiento.AjusteTecnico;

import java.util.List;

public interface AjusteTecnicoService {

    AjusteTecnico registrarAjuste(Long idExistencia, long cantidadConteo, String motivo,
                                  String observacion, Long idUsuarioSolicitante, Long idUsuarioAutorizante);

    AjusteTecnico obtenerPorId(Long id);

    List<AjusteTecnico> listarTodos();
}
