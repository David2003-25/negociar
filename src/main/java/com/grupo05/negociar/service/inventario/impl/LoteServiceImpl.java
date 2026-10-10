package com.grupo05.negociar.service.inventario.impl;

import com.grupo05.negociar.common.exception.ReglaNegocioException;
import com.grupo05.negociar.model.entidades.auditoria.AuditoriaEvento;
import com.grupo05.negociar.model.entidades.catalogo.Producto;
import com.grupo05.negociar.model.entidades.inventario.Lote;
import com.grupo05.negociar.model.entidades.seguridad.Usuario;
import com.grupo05.negociar.repository.auditoria.AuditoriaEventoRepository;
import com.grupo05.negociar.repository.catalogo.ProductoRepository;
import com.grupo05.negociar.repository.inventario.LoteRepository;
import com.grupo05.negociar.repository.seguridad.UsuarioRepository;
import com.grupo05.negociar.service.inventario.LoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoteServiceImpl implements LoteService {

    private final LoteRepository loteRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaEventoRepository auditoriaEventoRepository;

    @Override
    public Lote registrarOObtener(Long idProducto, String codigoLote, LocalDate fechaFab, LocalDate fechaVto, String observacion) {
        if (codigoLote == null || codigoLote.trim().isEmpty()) {
            throw new ReglaNegocioException("El código de lote es obligatorio");
        }
        if (codigoLote.trim().length() > 80) {
            throw new ReglaNegocioException("El código de lote no debe exceder 80 caracteres");
        }

        String codigoLimpio = codigoLote.trim().toUpperCase();

        Optional<Lote> loteExistente = loteRepository.findByProducto_IdProductoAndCodigoLote(idProducto, codigoLimpio);
        if (loteExistente.isPresent()) {
            // RF05: No cambiar fechas de un lote ya recibido
            return loteExistente.get();
        }

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ReglaNegocioException("Producto no encontrado con ID: " + idProducto));

        if (fechaVto != null && fechaVto.isBefore(LocalDate.now())) {
            throw new ReglaNegocioException("El lote ya se encuentra vencido con fecha: " + fechaVto);
        }

        if (fechaFab != null && fechaVto != null && fechaFab.isAfter(fechaVto)) {
            throw new ReglaNegocioException("La fecha de fabricación no puede ser posterior al vencimiento");
        }

        Lote nuevoLote = new Lote();
        nuevoLote.setProducto(producto);
        nuevoLote.setCodigoLote(codigoLimpio);
        nuevoLote.setFechaFabricacion(fechaFab);
        nuevoLote.setFechaVencimiento(fechaVto);
        nuevoLote.setFechaRecepcion(OffsetDateTime.now());
        nuevoLote.setObservacion(observacion);
        nuevoLote.setEstado(true);

        return loteRepository.save(nuevoLote);
    }

    @Override
    public void cambiarEstado(Long idLote, boolean estado, String motivo, Long idUsuario) {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new ReglaNegocioException("El motivo para cambiar el estado del lote es obligatorio");
        }

        Lote lote = obtenerPorId(idLote);
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ReglaNegocioException("Usuario autorizante no encontrado con ID: " + idUsuario));

        boolean estadoAnterior = Boolean.TRUE.equals(lote.getEstado());
        lote.setEstado(estado);
        loteRepository.save(lote);

        // Registro de auditoría
        AuditoriaEvento evento = new AuditoriaEvento();
        evento.setUsuario(usuario);
        evento.setEntidad("Lote");
        evento.setIdRegistro(lote.getIdLote().toString());
        evento.setAccion(estado ? "LIBERAR_LOTE" : "BLOQUEAR_LOTE");
        evento.setDetalle("Motivo: " + motivo.trim());
        evento.setDatosAnteriores("{\"estado\":" + estadoAnterior + "}");
        evento.setDatosNuevos("{\"estado\":" + estado + "}");
        evento.setFechaEvento(OffsetDateTime.now());
        auditoriaEventoRepository.save(evento);
    }

    @Override
    public Lote obtenerPorId(Long id) {
        return loteRepository.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("Lote no encontrado con ID: " + id));
    }

    @Override
    public List<Lote> listarPorProducto(Long idProducto) {
        return loteRepository.findByProducto_IdProductoAndEstadoTrue(idProducto);
    }

    @Override
    public List<Lote> listarLotesProximosAVencer(LocalDate fechaLimite) {
        return loteRepository.findByFechaVencimientoLessThanEqualAndEstadoTrue(fechaLimite);
    }

    @Override
    public List<Lote> listarTodos() {
        return loteRepository.findAll();
    }
}
