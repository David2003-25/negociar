package com.grupo05.negociar.service.movimiento.impl;

import com.grupo05.negociar.common.exception.ReglaNegocioException;
import com.grupo05.negociar.model.entidades.inventario.Existencia;
import com.grupo05.negociar.model.entidades.movimiento.AjusteTecnico;
import com.grupo05.negociar.model.entidades.movimiento.DetalleMovimiento;
import com.grupo05.negociar.model.entidades.movimiento.MovimientoInventario;
import com.grupo05.negociar.model.entidades.seguridad.Usuario;
import com.grupo05.negociar.model.enums.EstadoMovimiento;
import com.grupo05.negociar.model.enums.OperacionDetalle;
import com.grupo05.negociar.model.enums.TipoMovimiento;
import com.grupo05.negociar.repository.movimiento.AjusteTecnicoRepository;
import com.grupo05.negociar.repository.movimiento.DetalleMovimientoRepository;
import com.grupo05.negociar.repository.movimiento.MovimientoInventarioRepository;
import com.grupo05.negociar.repository.seguridad.UsuarioRepository;
import com.grupo05.negociar.service.inventario.ExistenciaService;
import com.grupo05.negociar.service.movimiento.AjusteTecnicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AjusteTecnicoServiceImpl implements AjusteTecnicoService {

    private final AjusteTecnicoRepository ajusteTecnicoRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final DetalleMovimientoRepository detalleMovimientoRepository;
    private final ExistenciaService existenciaService;
    private final UsuarioRepository usuarioRepository;

    @Override
    public AjusteTecnico registrarAjuste(Long idExistencia, long cantidadConteo, String motivo,
                                         String observacion, Long idUsuarioSolicitante, Long idUsuarioAutorizante) {
        if (cantidadConteo < 0) {
            throw new ReglaNegocioException("El conteo físico no puede ser un valor negativo");
        }
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new ReglaNegocioException("El motivo del ajuste técnico es obligatorio");
        }
        if (idUsuarioSolicitante != null && idUsuarioSolicitante.equals(idUsuarioAutorizante)) {
            throw new ReglaNegocioException("El usuario autorizante debe ser distinto al usuario solicitante");
        }

        Usuario usuarioAutorizante = usuarioRepository.findById(idUsuarioAutorizante)
                .orElseThrow(() -> new ReglaNegocioException("Usuario autorizante no encontrado"));

        Existencia existencia = existenciaService.obtenerPorId(idExistencia);
        long stockAnterior = existencia.getStockFisico();

        if (cantidadConteo < existencia.getStockReservado()) {
            throw new ReglaNegocioException("El nuevo conteo (" + cantidadConteo + ") no puede ser menor al stock reservado actual (" + existencia.getStockReservado() + ")");
        }

        long diferencia = cantidadConteo - stockAnterior;
        if (diferencia == 0) {
            throw new ReglaNegocioException("No existe diferencia entre el conteo ingresado y el stock actual (" + stockAnterior + ")");
        }

        // 1. Cabecera de movimiento tipo AJUSTE
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setTipoMovimiento(TipoMovimiento.AJUSTE);
        movimiento.setEstado(EstadoMovimiento.CONFIRMADO);
        movimiento.setFechaMovimiento(OffsetDateTime.now());
        movimiento.setDocumentoReferencia("AJUSTE-TEC-" + System.currentTimeMillis());
        movimiento.setObservacion(observacion);
        movimiento.setUsuario(usuarioAutorizante);

        MovimientoInventario movimientoGuardado = movimientoInventarioRepository.save(movimiento);

        // 2. Modificación de existencia con bloqueo
        existenciaService.modificarStockFisico(idExistencia, diferencia);

        // 3. Detalle de movimiento
        DetalleMovimiento detalle = new DetalleMovimiento();
        detalle.setMovimiento(movimientoGuardado);
        detalle.setExistencia(existencia);
        detalle.setOperacion(diferencia > 0 ? OperacionDetalle.SUMA : OperacionDetalle.RESTA);
        detalle.setCantidad(Math.abs(diferencia));
        detalleMovimientoRepository.save(detalle);

        // 4. Registro de Ajuste Técnico
        AjusteTecnico ajuste = new AjusteTecnico();
        ajuste.setMovimiento(movimientoGuardado);
        ajuste.setUsuario(usuarioAutorizante);
        ajuste.setMotivo(motivo.trim());
        ajuste.setCantidadAnterior(stockAnterior);
        ajuste.setCantidadNueva(cantidadConteo);
        ajuste.setFechaAjuste(OffsetDateTime.now());
        ajuste.setObservacion(observacion);

        return ajusteTecnicoRepository.save(ajuste);
    }

    @Override
    public AjusteTecnico obtenerPorId(Long id) {
        return ajusteTecnicoRepository.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("Ajuste técnico no encontrado con ID: " + id));
    }

    @Override
    public List<AjusteTecnico> listarTodos() {
        return ajusteTecnicoRepository.findAll();
    }
}
