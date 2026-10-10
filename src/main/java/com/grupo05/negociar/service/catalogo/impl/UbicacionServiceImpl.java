package com.grupo05.negociar.service.catalogo.impl;

import com.grupo05.negociar.common.exception.ReglaNegocioException;
import com.grupo05.negociar.model.entidades.catalogo.Ubicacion;
import com.grupo05.negociar.model.enums.EstadoReserva;
import com.grupo05.negociar.repository.catalogo.UbicacionRepository;
import com.grupo05.negociar.repository.inventario.ExistenciaRepository;
import com.grupo05.negociar.repository.inventario.ReservaInventarioRepository;
import com.grupo05.negociar.service.catalogo.UbicacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UbicacionServiceImpl implements UbicacionService {

    private final UbicacionRepository ubicacionRepository;
    private final ExistenciaRepository existenciaRepository;
    private final ReservaInventarioRepository reservaInventarioRepository;

    private static final Set<String> TIPOS_VALIDOS = Set.of(
            "ALMACEN", "ZONA", "ESTANTERIA", "PASILLO", "RECEPCION", "DESPACHO"
    );

    @Override
    public Ubicacion registrar(Ubicacion ubicacion) {
        validarUbicacion(ubicacion);

        String codigo = ubicacion.getCodigo().trim().toUpperCase();
        if (ubicacionRepository.existsByCodigo(codigo)) {
            throw new ReglaNegocioException("El código de ubicación " + codigo + " ya existe");
        }

        ubicacion.setCodigo(codigo);
        ubicacion.setNombre(ubicacion.getNombre().trim());
        ubicacion.setTipo(ubicacion.getTipo().trim().toUpperCase());
        ubicacion.setEstado(true);
        ubicacion.setFechaCreacion(OffsetDateTime.now());
        return ubicacionRepository.save(ubicacion);
    }

    @Override
    public Ubicacion modificar(Long id, Ubicacion ubicacion) {
        validarUbicacion(ubicacion);
        Ubicacion existente = obtenerPorId(id);

        String nuevoCodigo = ubicacion.getCodigo().trim().toUpperCase();
        if (!nuevoCodigo.equals(existente.getCodigo())) {
            if (ubicacionRepository.existsByCodigo(nuevoCodigo)) {
                throw new ReglaNegocioException("El código de ubicación " + nuevoCodigo + " ya está en uso");
            }
            existente.setCodigo(nuevoCodigo);
        }

        existente.setNombre(ubicacion.getNombre().trim());
        existente.setTipo(ubicacion.getTipo().trim().toUpperCase());
        existente.setDescripcion(ubicacion.getDescripcion());
        return ubicacionRepository.save(existente);
    }

    @Override
    public void desactivar(Long id) {
        Ubicacion ubicacion = obtenerPorId(id);

        // RF04: No desactivar con saldo físico o reservas activas
        if (existenciaRepository.existsByUbicacion_IdUbicacionAndStockFisicoGreaterThan(id, 0L)) {
            throw new ReglaNegocioException("No se puede desactivar la ubicación porque tiene existencias con saldo físico");
        }

        if (reservaInventarioRepository.existsByExistencia_Ubicacion_IdUbicacionAndEstado(id, EstadoReserva.ACTIVA)) {
            throw new ReglaNegocioException("No se puede desactivar la ubicación porque tiene reservas de inventario activas");
        }

        ubicacion.setEstado(false);
        ubicacionRepository.save(ubicacion);
    }

    @Override
    public void activar(Long id) {
        Ubicacion ubicacion = obtenerPorId(id);
        ubicacion.setEstado(true);
        ubicacionRepository.save(ubicacion);
    }

    @Override
    public Ubicacion obtenerPorId(Long id) {
        return ubicacionRepository.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("Ubicación no encontrada con ID: " + id));
    }

    @Override
    public List<Ubicacion> listarActivas() {
        return ubicacionRepository.findByEstadoTrue();
    }

    @Override
    public List<Ubicacion> listarTodas() {
        return ubicacionRepository.findAll();
    }

    @Override
    public List<Ubicacion> listarPorTipo(String tipo) {
        return ubicacionRepository.findByTipo(tipo);
    }

    private void validarUbicacion(Ubicacion ubicacion) {
        if (ubicacion.getCodigo() == null || ubicacion.getCodigo().trim().isEmpty()) {
            throw new ReglaNegocioException("El código de ubicación es obligatorio");
        }
        if (ubicacion.getCodigo().trim().length() > 30) {
            throw new ReglaNegocioException("El código de ubicación no debe exceder 30 caracteres");
        }
        if (ubicacion.getNombre() == null || ubicacion.getNombre().trim().isEmpty()) {
            throw new ReglaNegocioException("El nombre de la ubicación es obligatorio");
        }
        if (ubicacion.getNombre().trim().length() > 100) {
            throw new ReglaNegocioException("El nombre no debe exceder 100 caracteres");
        }
        if (ubicacion.getTipo() == null || !TIPOS_VALIDOS.contains(ubicacion.getTipo().trim().toUpperCase())) {
            throw new ReglaNegocioException("El tipo de ubicación debe ser uno de: " + TIPOS_VALIDOS);
        }
    }
}
