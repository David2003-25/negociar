package com.grupo05.negociar.service.inventario;

import com.grupo05.negociar.model.entidades.inventario.Lote;
import com.grupo05.negociar.service.inventario.dto.AlertaStockDTO;

import java.util.List;

public interface AlertaService {

    List<AlertaStockDTO> obtenerAlertasBajoStock();

    List<Lote> obtenerLotesPorVencer(int diasAnticipacion);

    long contarAlertasTotales();
}
