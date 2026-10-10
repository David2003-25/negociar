package com.grupo05.negociar.service.auditoria.impl;

import com.grupo05.negociar.model.entidades.inventario.Existencia;
import com.grupo05.negociar.model.entidades.movimiento.DetalleMovimiento;
import com.grupo05.negociar.model.entidades.movimiento.MovimientoInventario;
import com.grupo05.negociar.repository.inventario.ExistenciaRepository;
import com.grupo05.negociar.repository.movimiento.DetalleMovimientoRepository;
import com.grupo05.negociar.repository.movimiento.MovimientoInventarioRepository;
import com.grupo05.negociar.service.auditoria.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final ExistenciaRepository existenciaRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final DetalleMovimientoRepository detalleMovimientoRepository;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public byte[] exportarExistenciasCsv() {
        List<Existencia> existencias = existenciaRepository.findAll();
        StringBuilder csv = new StringBuilder();

        // Cabecera
        csv.append("ID,Codigo_Producto,Producto,Presentacion,Lote,Vencimiento,Ubicacion,Stock_Fisico,Stock_Reservado,Stock_Disponible\n");

        for (Existencia e : existencias) {
            String vto = e.getLote().getFechaVencimiento() != null ? e.getLote().getFechaVencimiento().toString() : "N/A";
            long disponible = e.getStockFisico() - e.getStockReservado();

            csv.append(e.getIdExistencia()).append(",")
               .append(sanitizarCsv(e.getProducto().getCodigo())).append(",")
               .append(sanitizarCsv(e.getProducto().getNombre())).append(",")
               .append(sanitizarCsv(e.getProducto().getUnidadPresentacion())).append(",")
               .append(sanitizarCsv(e.getLote().getCodigoLote())).append(",")
               .append(vto).append(",")
               .append(sanitizarCsv(e.getUbicacion().getCodigo())).append(",")
               .append(e.getStockFisico()).append(",")
               .append(e.getStockReservado()).append(",")
               .append(disponible).append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] exportarMovimientosCsv(OffsetDateTime desde, OffsetDateTime hasta) {
        List<MovimientoInventario> movimientos;
        if (desde != null && hasta != null) {
            movimientos = movimientoRepository.findByFechaMovimientoBetween(desde, hasta);
        } else {
            movimientos = movimientoRepository.findAll();
        }

        StringBuilder csv = new StringBuilder();
        // Cabecera
        csv.append("ID_Movimiento,Tipo,Fecha,Estado,Documento,Usuario,Producto,Lote,Ubicacion,Operacion,Cantidad\n");

        for (MovimientoInventario m : movimientos) {
            String usuario = m.getUsuario() != null ? m.getUsuario().getNombres() + " " + m.getUsuario().getApellidos() : "SISTEMA";
            String fecha = m.getFechaMovimiento() != null ? m.getFechaMovimiento().format(FORMATO_FECHA) : "";

            List<DetalleMovimiento> detalles = detalleMovimientoRepository.findByMovimiento_IdMovimiento(m.getIdMovimiento());
            for (DetalleMovimiento d : detalles) {
                csv.append(m.getIdMovimiento()).append(",")
                   .append(m.getTipoMovimiento()).append(",")
                   .append(fecha).append(",")
                   .append(m.getEstado()).append(",")
                   .append(sanitizarCsv(m.getDocumentoReferencia())).append(",")
                   .append(sanitizarCsv(usuario)).append(",")
                   .append(sanitizarCsv(d.getExistencia().getProducto().getNombre())).append(",")
                   .append(sanitizarCsv(d.getExistencia().getLote().getCodigoLote())).append(",")
                   .append(sanitizarCsv(d.getExistencia().getUbicacion().getCodigo())).append(",")
                   .append(d.getOperacion()).append(",")
                   .append(d.getCantidad()).append("\n");
            }
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String sanitizarCsv(String valor) {
        if (valor == null) return "";
        String v = valor.trim();
        // RF11: Neutralizar fórmulas (=, +, -, @)
        if (v.startsWith("=") || v.startsWith("+") || v.startsWith("-") || v.startsWith("@")) {
            v = "'" + v;
        }
        if (v.contains(",") || v.contains("\"") || v.contains("\n")) {
            v = "\"" + v.replace("\"", "\"\"") + "\"";
        }
        return v;
    }
}
