package com.grupo05.negociar.service.distribucion.impl;

import com.grupo05.negociar.common.exception.ReglaNegocioException;
import com.grupo05.negociar.model.entidades.catalogo.Producto;
import com.grupo05.negociar.model.entidades.distribucion.DetallePicking;
import com.grupo05.negociar.model.entidades.distribucion.DetalleSolicitud;
import com.grupo05.negociar.model.entidades.distribucion.Picking;
import com.grupo05.negociar.model.entidades.distribucion.SolicitudDistribucion;
import com.grupo05.negociar.model.entidades.inventario.ReservaInventario;
import com.grupo05.negociar.model.enums.EstadoPicking;
import com.grupo05.negociar.model.enums.EstadoReserva;
import com.grupo05.negociar.model.enums.EstadoSolicitud;
import com.grupo05.negociar.repository.catalogo.ProductoRepository;
import com.grupo05.negociar.repository.distribucion.DetallePickingRepository;
import com.grupo05.negociar.repository.distribucion.DetalleSolicitudRepository;
import com.grupo05.negociar.repository.distribucion.PickingRepository;
import com.grupo05.negociar.repository.distribucion.SolicitudDistribucionRepository;
import com.grupo05.negociar.repository.inventario.ReservaInventarioRepository;
import com.grupo05.negociar.service.distribucion.SolicitudDistribucionService;
import com.grupo05.negociar.service.distribucion.dto.LineaItemSolicitudDTO;
import com.grupo05.negociar.service.inventario.ExistenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SolicitudDistribucionServiceImpl implements SolicitudDistribucionService {

    private final SolicitudDistribucionRepository solicitudRepository;
    private final DetalleSolicitudRepository detalleSolicitudRepository;
    private final ProductoRepository productoRepository;
    private final PickingRepository pickingRepository;
    private final DetallePickingRepository detallePickingRepository;
    private final ReservaInventarioRepository reservaInventarioRepository;
    private final ExistenciaService existenciaService;

    @Override
    public SolicitudDistribucion registrar(SolicitudDistribucion solicitud, List<LineaItemSolicitudDTO> lineas) {
        if (solicitud.getDestino() == null || solicitud.getDestino().trim().isEmpty()) {
            throw new ReglaNegocioException("El destino de la distribución es obligatorio");
        }
        if (solicitud.getDestino().trim().length() > 200) {
            throw new ReglaNegocioException("El destino no debe exceder 200 caracteres");
        }
        if (lineas == null || lineas.isEmpty()) {
            throw new ReglaNegocioException("La solicitud debe contener al menos un producto");
        }

        String codigo = (solicitud.getCodigoSolicitud() != null && !solicitud.getCodigoSolicitud().trim().isEmpty())
                ? solicitud.getCodigoSolicitud().trim().toUpperCase()
                : "SOL-" + System.currentTimeMillis();

        if (solicitudRepository.existsByCodigoSolicitud(codigo)) {
            throw new ReglaNegocioException("El código de solicitud " + codigo + " ya existe");
        }

        solicitud.setCodigoSolicitud(codigo);
        solicitud.setDestino(solicitud.getDestino().trim());
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        solicitud.setFechaSolicitud(OffsetDateTime.now());

        SolicitudDistribucion guardada = solicitudRepository.save(solicitud);

        // RF18: Una sola línea por producto
        Set<Long> productosProcesados = new HashSet<>();
        List<DetalleSolicitud> detalles = new ArrayList<>();

        for (LineaItemSolicitudDTO item : lineas) {
            if (item.getCantidad() == null || item.getCantidad() <= 0) {
                throw new ReglaNegocioException("La cantidad solicitada debe ser un entero positivo");
            }
            if (!productosProcesados.add(item.getIdProducto())) {
                throw new ReglaNegocioException("No se permite registrar más de una línea para el mismo producto (ID: " + item.getIdProducto() + ")");
            }

            Producto producto = productoRepository.findById(item.getIdProducto())
                    .orElseThrow(() -> new ReglaNegocioException("Producto no encontrado con ID: " + item.getIdProducto()));

            DetalleSolicitud detalle = new DetalleSolicitud();
            detalle.setSolicitud(guardada);
            detalle.setProducto(producto);
            detalle.setCantidadSolicitada(item.getCantidad());
            detalle.setObservacion(item.getObservacion());
            detalles.add(detalle);
        }

        detalleSolicitudRepository.saveAll(detalles);
        return guardada;
    }

    @Override
    public void cancelar(Long idSolicitud, Long idUsuario) {
        SolicitudDistribucion solicitud = obtenerPorId(idSolicitud);
        if (solicitud.getEstado() == EstadoSolicitud.COMPLETADA) {
            throw new ReglaNegocioException("No se puede cancelar una solicitud que ya fue completada");
        }
        if (solicitud.getEstado() == EstadoSolicitud.CANCELADA) {
            throw new ReglaNegocioException("La solicitud ya se encuentra cancelada");
        }

        // RF18: Liberar reservas activas y cancelar pickings asociados
        List<Picking> pickings = pickingRepository.findBySolicitud_IdSolicitud(idSolicitud);
        for (Picking picking : pickings) {
            if (picking.getEstado() != EstadoPicking.CONFIRMADO) {
                List<DetallePicking> detalles = detallePickingRepository.findByPicking_IdPicking(picking.getIdPicking());
                for (DetallePicking dp : detalles) {
                    List<ReservaInventario> reservas = reservaInventarioRepository.findByDetallePicking_IdDetallePicking(dp.getIdDetallePicking());
                    for (ReservaInventario reserva : reservas) {
                        if (reserva.getEstado() == EstadoReserva.ACTIVA) {
                            reserva.setEstado(EstadoReserva.LIBERADA);
                            reserva.setFechaCierre(OffsetDateTime.now());
                            reservaInventarioRepository.save(reserva);

                            // Descontar reservado en la existencia
                            existenciaService.modificarStockReservado(reserva.getExistencia().getIdExistencia(), -reserva.getCantidad());
                        }
                    }
                }
                picking.setEstado(EstadoPicking.CANCELADO);
                pickingRepository.save(picking);
            }
        }

        solicitud.setEstado(EstadoSolicitud.CANCELADA);
        solicitudRepository.save(solicitud);
    }

    @Override
    public SolicitudDistribucion obtenerPorId(Long id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("Solicitud no encontrada con ID: " + id));
    }

    @Override
    public SolicitudDistribucion obtenerPorCodigo(String codigo) {
        return solicitudRepository.findByCodigoSolicitud(codigo)
                .orElseThrow(() -> new ReglaNegocioException("Solicitud no encontrada con código: " + codigo));
    }

    @Override
    public List<SolicitudDistribucion> listarTodas() {
        return solicitudRepository.findAll();
    }

    @Override
    public Page<SolicitudDistribucion> listarPaginado(Pageable pageable) {
        return solicitudRepository.findAll(pageable);
    }
}
