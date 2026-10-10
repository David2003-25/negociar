package com.grupo05.negociar.service.movimiento.impl;

import com.grupo05.negociar.common.exception.ReglaNegocioException;
import com.grupo05.negociar.model.entidades.catalogo.Proveedor;
import com.grupo05.negociar.model.entidades.inventario.Existencia;
import com.grupo05.negociar.model.entidades.inventario.Lote;
import com.grupo05.negociar.model.entidades.movimiento.DetalleMovimiento;
import com.grupo05.negociar.model.entidades.movimiento.MovimientoInventario;
import com.grupo05.negociar.model.entidades.seguridad.Usuario;
import com.grupo05.negociar.model.enums.EstadoMovimiento;
import com.grupo05.negociar.model.enums.OperacionDetalle;
import com.grupo05.negociar.model.enums.TipoMovimiento;
import com.grupo05.negociar.repository.catalogo.ProveedorRepository;
import com.grupo05.negociar.repository.inventario.ExistenciaRepository;
import com.grupo05.negociar.repository.movimiento.DetalleMovimientoRepository;
import com.grupo05.negociar.repository.movimiento.MovimientoInventarioRepository;
import com.grupo05.negociar.repository.seguridad.UsuarioRepository;
import com.grupo05.negociar.service.inventario.ExistenciaService;
import com.grupo05.negociar.service.inventario.LoteService;
import com.grupo05.negociar.service.movimiento.MovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final DetalleMovimientoRepository detalleMovimientoRepository;
    private final ExistenciaRepository existenciaRepository;
    private final ExistenciaService existenciaService;
    private final LoteService loteService;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public MovimientoInventario registrarEntrada(Long idProducto, Long idProveedor, String codigoLote,
                                                 LocalDate fechaFab, LocalDate fechaVto,
                                                 Long idUbicacion, long cantidad,
                                                 String documentoReferencia, String observacion,
                                                 Long idUsuario) {
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad a ingresar debe ser un entero positivo mayor a cero");
        }
        if (documentoReferencia != null && documentoReferencia.length() > 100) {
            throw new ReglaNegocioException("El documento de referencia no debe exceder 100 caracteres");
        }

        Usuario usuario = idUsuario != null ? usuarioRepository.findById(idUsuario).orElse(null) : null;
        Proveedor proveedor = idProveedor != null ? proveedorRepository.findById(idProveedor).orElse(null) : null;

        // RF05 & RF15: Validación y registro/obtención de lote
        Lote lote = loteService.registrarOObtener(idProducto, codigoLote, fechaFab, fechaVto, observacion);

        // RF07: Obtener o crear existencia y actualizar stock atómicamente
        Existencia existencia = existenciaService.obtenerOCrear(idProducto, lote.getIdLote(), idUbicacion);
        existenciaService.modificarStockFisico(existencia.getIdExistencia(), cantidad);

        // Cabecera de movimiento
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setTipoMovimiento(TipoMovimiento.ENTRADA);
        movimiento.setEstado(EstadoMovimiento.CONFIRMADO);
        movimiento.setFechaMovimiento(OffsetDateTime.now());
        movimiento.setDocumentoReferencia(documentoReferencia);
        movimiento.setObservacion(observacion);
        movimiento.setUsuario(usuario);
        movimiento.setProveedor(proveedor);

        MovimientoInventario guardado = movimientoInventarioRepository.save(movimiento);

        // Detalle de movimiento
        DetalleMovimiento detalle = new DetalleMovimiento();
        detalle.setMovimiento(guardado);
        detalle.setExistencia(existencia);
        detalle.setOperacion(OperacionDetalle.SUMA);
        detalle.setCantidad(cantidad);

        detalleMovimientoRepository.save(detalle);

        return guardado;
    }

    @Override
    public MovimientoInventario registrarSalidaDirectaFEFO(Long idProducto, Long idUbicacion, long cantidadTotal,
                                                           String documentoReferencia, String observacion,
                                                           Long idUsuario) {
        if (cantidadTotal <= 0) {
            throw new ReglaNegocioException("La cantidad a retirar debe ser un entero positivo mayor a cero");
        }

        // RF06: Selección FEFO/FIFO de stock disponible libre
        List<Existencia> existenciasElegibles = existenciaRepository.findStockDisponibleFefoFifo(idProducto, idUbicacion);

        long stockLibreTotal = existenciasElegibles.stream()
                .mapToLong(e -> e.getStockFisico() - e.getStockReservado())
                .sum();

        if (stockLibreTotal < cantidadTotal) {
            throw new ReglaNegocioException("Stock insuficiente para realizar la salida. Disponible: " + stockLibreTotal + ", Solicitado: " + cantidadTotal);
        }

        Usuario usuario = idUsuario != null ? usuarioRepository.findById(idUsuario).orElse(null) : null;

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setTipoMovimiento(TipoMovimiento.SALIDA);
        movimiento.setEstado(EstadoMovimiento.CONFIRMADO);
        movimiento.setFechaMovimiento(OffsetDateTime.now());
        movimiento.setDocumentoReferencia(documentoReferencia);
        movimiento.setObservacion(observacion);
        movimiento.setUsuario(usuario);

        MovimientoInventario guardado = movimientoInventarioRepository.save(movimiento);

        long pendientePorDescontar = cantidadTotal;
        List<DetalleMovimiento> detalles = new ArrayList<>();

        for (Existencia existencia : existenciasElegibles) {
            if (pendientePorDescontar == 0) break;

            long disponibleEnLote = existencia.getStockFisico() - existencia.getStockReservado();
            long aDescontar = Math.min(disponibleEnLote, pendientePorDescontar);

            // Modificación atómica
            existenciaService.modificarStockFisico(existencia.getIdExistencia(), -aDescontar);

            DetalleMovimiento detalle = new DetalleMovimiento();
            detalle.setMovimiento(guardado);
            detalle.setExistencia(existencia);
            detalle.setOperacion(OperacionDetalle.RESTA);
            detalle.setCantidad(aDescontar);
            detalles.add(detalle);

            pendientePorDescontar -= aDescontar;
        }

        detalleMovimientoRepository.saveAll(detalles);
        return guardado;
    }

    @Override
    public Page<MovimientoInventario> listarHistorial(Pageable pageable) {
        return movimientoInventarioRepository.findAll(pageable);
    }

    @Override
    public MovimientoInventario obtenerPorId(Long id) {
        return movimientoInventarioRepository.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("Movimiento no encontrado con ID: " + id));
    }
}
