package com.grupo05.negociar.service.distribucion.impl;

import com.grupo05.negociar.common.exception.ReglaNegocioException;
import com.grupo05.negociar.model.entidades.distribucion.DetallePicking;
import com.grupo05.negociar.model.entidades.distribucion.DetalleSolicitud;
import com.grupo05.negociar.model.entidades.distribucion.Picking;
import com.grupo05.negociar.model.entidades.distribucion.SolicitudDistribucion;
import com.grupo05.negociar.model.entidades.inventario.Existencia;
import com.grupo05.negociar.model.entidades.inventario.ReservaInventario;
import com.grupo05.negociar.model.entidades.movimiento.DetalleMovimiento;
import com.grupo05.negociar.model.entidades.movimiento.MovimientoInventario;
import com.grupo05.negociar.model.entidades.seguridad.Usuario;
import com.grupo05.negociar.model.enums.*;
import com.grupo05.negociar.repository.distribucion.DetallePickingRepository;
import com.grupo05.negociar.repository.distribucion.DetalleSolicitudRepository;
import com.grupo05.negociar.repository.distribucion.PickingRepository;
import com.grupo05.negociar.repository.distribucion.SolicitudDistribucionRepository;
import com.grupo05.negociar.repository.inventario.ExistenciaRepository;
import com.grupo05.negociar.repository.inventario.ReservaInventarioRepository;
import com.grupo05.negociar.repository.movimiento.DetalleMovimientoRepository;
import com.grupo05.negociar.repository.movimiento.MovimientoInventarioRepository;
import com.grupo05.negociar.repository.seguridad.UsuarioRepository;
import com.grupo05.negociar.service.distribucion.PickingService;
import com.grupo05.negociar.service.inventario.ExistenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PickingServiceImpl implements PickingService {

    private final PickingRepository pickingRepository;
    private final DetallePickingRepository detallePickingRepository;
    private final SolicitudDistribucionRepository solicitudRepository;
    private final DetalleSolicitudRepository detalleSolicitudRepository;
    private final ExistenciaRepository existenciaRepository;
    private final ExistenciaService existenciaService;
    private final ReservaInventarioRepository reservaInventarioRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final DetalleMovimientoRepository detalleMovimientoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public Picking crearPicking(Long idSolicitud, String responsable) {
        SolicitudDistribucion solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new ReglaNegocioException("Solicitud no encontrada con ID: " + idSolicitud));

        if (solicitud.getEstado() == EstadoSolicitud.COMPLETADA || solicitud.getEstado() == EstadoSolicitud.CANCELADA) {
            throw new ReglaNegocioException("No se puede iniciar picking sobre una solicitud " + solicitud.getEstado());
        }

        Picking picking = new Picking();
        picking.setCodigoPicking("PICK-" + System.currentTimeMillis());
        picking.setSolicitud(solicitud);
        picking.setResponsable(responsable);
        picking.setEstado(EstadoPicking.PENDIENTE);
        picking.setFechaInicio(OffsetDateTime.now());

        solicitud.setEstado(EstadoSolicitud.EN_PROCESO);
        solicitudRepository.save(solicitud);

        return pickingRepository.save(picking);
    }

    @Override
    public void registrarRecogidaYReserva(Long idPicking, Long idDetalleSolicitud, Long idExistencia, long cantidadRecogida) {
        if (cantidadRecogida <= 0) {
            throw new ReglaNegocioException("La cantidad recogida debe ser mayor a cero");
        }

        Picking picking = obtenerPorId(idPicking);
        if (picking.getEstado() == EstadoPicking.CONFIRMADO || picking.getEstado() == EstadoPicking.CANCELADO) {
            throw new ReglaNegocioException("No se puede agregar recogidas a un picking en estado " + picking.getEstado());
        }

        DetalleSolicitud detalleSolicitud = detalleSolicitudRepository.findById(idDetalleSolicitud)
                .orElseThrow(() -> new ReglaNegocioException("Línea de solicitud no encontrada"));

        if (!detalleSolicitud.getSolicitud().getIdSolicitud().equals(picking.getSolicitud().getIdSolicitud())) {
            throw new ReglaNegocioException("La línea no pertenece a la solicitud asociada al picking");
        }

        Existencia existencia = existenciaRepository.findById(idExistencia)
                .orElseThrow(() -> new ReglaNegocioException("Existencia no encontrada"));

        if (!existencia.getProducto().getIdProducto().equals(detalleSolicitud.getProducto().getIdProducto())) {
            throw new ReglaNegocioException("El producto de la existencia no coincide con el producto solicitado");
        }

        // RF19: Validar disponible libre
        long disponibleLibre = existencia.getStockFisico() - existencia.getStockReservado();
        if (disponibleLibre < cantidadRecogida) {
            throw new ReglaNegocioException("Stock libre insuficiente en la ubicación elegida. Libre: " + disponibleLibre + ", Solicitado: " + cantidadRecogida);
        }

        // 1. Crear detalle de picking
        DetallePicking detallePicking = new DetallePicking();
        detallePicking.setPicking(picking);
        detallePicking.setDetalleSolicitud(detalleSolicitud);
        detallePicking.setExistencia(existencia);
        detallePicking.setProducto(existencia.getProducto());
        detallePicking.setCantidadRequerida(detalleSolicitud.getCantidadSolicitada());
        detallePicking.setCantidadRecogida(cantidadRecogida);
        detallePicking.setEstado(EstadoDetallePicking.RECOGIDO);

        DetallePicking detalleGuardado = detallePickingRepository.save(detallePicking);

        // 2. Incrementar stock reservado en existencia
        existenciaService.modificarStockReservado(idExistencia, cantidadRecogida);

        // 3. Crear registro de reserva activa
        ReservaInventario reserva = new ReservaInventario();
        reserva.setExistencia(existencia);
        reserva.setDetallePicking(detalleGuardado);
        reserva.setCantidad(cantidadRecogida);
        reserva.setEstado(EstadoReserva.ACTIVA);
        reserva.setFechaReserva(OffsetDateTime.now());

        reservaInventarioRepository.save(reserva);

        picking.setEstado(EstadoPicking.EN_PROCESO);
        pickingRepository.save(picking);
    }

    @Override
    public void confirmarDespacho(Long idPicking, Long idUsuario) {
        Picking picking = obtenerPorId(idPicking);
        if (picking.getEstado() != EstadoPicking.EN_PROCESO && picking.getEstado() != EstadoPicking.PENDIENTE) {
            throw new ReglaNegocioException("Solo se pueden despachar órdenes de picking en proceso o pendientes");
        }

        List<DetallePicking> detalles = detallePickingRepository.findByPicking_IdPicking(idPicking);
        if (detalles.isEmpty()) {
            throw new ReglaNegocioException("El picking no tiene unidades recogidas para despachar");
        }

        Usuario usuario = idUsuario != null ? usuarioRepository.findById(idUsuario).orElse(null) : null;

        // Cabecera de movimiento SALIDA por despacho
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setTipoMovimiento(TipoMovimiento.SALIDA);
        movimiento.setEstado(EstadoMovimiento.CONFIRMADO);
        movimiento.setFechaMovimiento(OffsetDateTime.now());
        movimiento.setDocumentoReferencia("DESPACHO-" + picking.getCodigoPicking());
        movimiento.setObservacion("Despacho asociado a orden " + picking.getCodigoPicking());
        movimiento.setUsuario(usuario);

        MovimientoInventario movimientoGuardado = movimientoInventarioRepository.save(movimiento);

        List<DetalleMovimiento> detallesMovimiento = new ArrayList<>();

        for (DetallePicking dp : detalles) {
            List<ReservaInventario> reservas = reservaInventarioRepository.findByDetallePicking_IdDetallePicking(dp.getIdDetallePicking());
            for (ReservaInventario reserva : reservas) {
                if (reserva.getEstado() == EstadoReserva.ACTIVA) {
                    // RF20: Revalidar caducidad del lote al despachar
                    Existencia existencia = reserva.getExistencia();
                    if (existencia.getLote().getFechaVencimiento() != null &&
                            existencia.getLote().getFechaVencimiento().isBefore(LocalDate.now())) {
                        throw new ReglaNegocioException("No se puede despachar el lote " + existencia.getLote().getCodigoLote() + " porque está vencido");
                    }

                    // Pasar reserva de ACTIVA a CONSUMIDA
                    reserva.setEstado(EstadoReserva.CONSUMIDA);
                    reserva.setFechaCierre(OffsetDateTime.now());
                    reservaInventarioRepository.save(reserva);

                    // Descontar saldo físico y saldo reservado
                    existenciaService.modificarStockFisico(existencia.getIdExistencia(), -reserva.getCantidad());
                    existenciaService.modificarStockReservado(existencia.getIdExistencia(), -reserva.getCantidad());

                    // Registrar línea de detalle de movimiento
                    DetalleMovimiento dm = new DetalleMovimiento();
                    dm.setMovimiento(movimientoGuardado);
                    dm.setExistencia(existencia);
                    dm.setOperacion(OperacionDetalle.RESTA);
                    dm.setCantidad(reserva.getCantidad());
                    detallesMovimiento.add(dm);
                }
            }
        }

        detalleMovimientoRepository.saveAll(detallesMovimiento);

        picking.setEstado(EstadoPicking.CONFIRMADO);
        picking.setFechaConfirmacion(OffsetDateTime.now());
        pickingRepository.save(picking);

        SolicitudDistribucion solicitud = picking.getSolicitud();
        solicitud.setEstado(EstadoSolicitud.COMPLETADA);
        solicitudRepository.save(solicitud);
    }

    @Override
    public void liberarReservas(Long idPicking) {
        Picking picking = obtenerPorId(idPicking);
        if (picking.getEstado() == EstadoPicking.CONFIRMADO) {
            throw new ReglaNegocioException("No se pueden liberar reservas de un picking ya confirmado");
        }

        List<DetallePicking> detalles = detallePickingRepository.findByPicking_IdPicking(idPicking);
        for (DetallePicking dp : detalles) {
            List<ReservaInventario> reservas = reservaInventarioRepository.findByDetallePicking_IdDetallePicking(dp.getIdDetallePicking());
            for (ReservaInventario reserva : reservas) {
                if (reserva.getEstado() == EstadoReserva.ACTIVA) {
                    reserva.setEstado(EstadoReserva.LIBERADA);
                    reserva.setFechaCierre(OffsetDateTime.now());
                    reservaInventarioRepository.save(reserva);

                    // RF20: Liberación solo reduce reservado, restituyendo disponibilidad
                    existenciaService.modificarStockReservado(reserva.getExistencia().getIdExistencia(), -reserva.getCantidad());
                }
            }
        }

        picking.setEstado(EstadoPicking.CANCELADO);
        pickingRepository.save(picking);
    }

    @Override
    public Picking obtenerPorId(Long id) {
        return pickingRepository.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("Picking no encontrado con ID: " + id));
    }

    @Override
    public List<Picking> listarPorSolicitud(Long idSolicitud) {
        return pickingRepository.findBySolicitud_IdSolicitud(idSolicitud);
    }

    @Override
    public List<Picking> listarTodos() {
        return pickingRepository.findAll();
    }
}
