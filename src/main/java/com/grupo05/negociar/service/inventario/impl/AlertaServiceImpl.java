package com.grupo05.negociar.service.inventario.impl;

import com.grupo05.negociar.model.entidades.catalogo.Producto;
import com.grupo05.negociar.model.entidades.inventario.Existencia;
import com.grupo05.negociar.model.entidades.inventario.Lote;
import com.grupo05.negociar.repository.catalogo.ProductoRepository;
import com.grupo05.negociar.repository.inventario.ExistenciaRepository;
import com.grupo05.negociar.repository.inventario.LoteRepository;
import com.grupo05.negociar.service.inventario.AlertaService;
import com.grupo05.negociar.service.inventario.dto.AlertaStockDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertaServiceImpl implements AlertaService {

    private final ProductoRepository productoRepository;
    private final ExistenciaRepository existenciaRepository;
    private final LoteRepository loteRepository;

    @Override
    public List<AlertaStockDTO> obtenerAlertasBajoStock() {
        List<Producto> productosActivos = productoRepository.findByEstadoTrue();
        List<AlertaStockDTO> alertas = new ArrayList<>();

        for (Producto producto : productosActivos) {
            List<Existencia> existencias = existenciaRepository.findByProducto_IdProducto(producto.getIdProducto());
            long stockTotal = existencias.stream()
                    .mapToLong(e -> e.getStockFisico() != null ? e.getStockFisico() : 0L)
                    .sum();

            int minimo = producto.getStockMinimo() != null ? producto.getStockMinimo() : 0;
            if (stockTotal <= minimo) {
                String nivel = stockTotal == 0 ? "CRITICO" : "ALERTA";
                alertas.add(new AlertaStockDTO(producto, stockTotal, minimo, nivel));
            }
        }

        return alertas;
    }

    @Override
    public List<Lote> obtenerLotesPorVencer(int diasAnticipacion) {
        LocalDate fechaLimite = LocalDate.now().plusDays(diasAnticipacion);
        return loteRepository.findByFechaVencimientoLessThanEqualAndEstadoTrue(fechaLimite);
    }

    @Override
    public long contarAlertasTotales() {
        int bajoStock = obtenerAlertasBajoStock().size();
        int porVencer = obtenerLotesPorVencer(30).size();
        return bajoStock + porVencer;
    }
}
