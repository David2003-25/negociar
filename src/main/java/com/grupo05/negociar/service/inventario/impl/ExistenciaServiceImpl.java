package com.grupo05.negociar.service.inventario.impl;

import com.grupo05.negociar.common.exception.ReglaNegocioException;
import com.grupo05.negociar.model.entidades.catalogo.Producto;
import com.grupo05.negociar.model.entidades.catalogo.Ubicacion;
import com.grupo05.negociar.model.entidades.inventario.Existencia;
import com.grupo05.negociar.model.entidades.inventario.Lote;
import com.grupo05.negociar.repository.catalogo.ProductoRepository;
import com.grupo05.negociar.repository.catalogo.UbicacionRepository;
import com.grupo05.negociar.repository.inventario.ExistenciaRepository;
import com.grupo05.negociar.repository.inventario.LoteRepository;
import com.grupo05.negociar.service.inventario.ExistenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExistenciaServiceImpl implements ExistenciaService {

    private final ExistenciaRepository existenciaRepository;
    private final ProductoRepository productoRepository;
    private final LoteRepository loteRepository;
    private final UbicacionRepository ubicacionRepository;

    @Override
    public Existencia obtenerOCrear(Long idProducto, Long idLote, Long idUbicacion) {
        return existenciaRepository.findByLote_IdLoteAndUbicacion_IdUbicacion(idLote, idUbicacion)
                .orElseGet(() -> {
                    Producto producto = productoRepository.findById(idProducto)
                            .orElseThrow(() -> new ReglaNegocioException("Producto no encontrado"));
                    Lote lote = loteRepository.findById(idLote)
                            .orElseThrow(() -> new ReglaNegocioException("Lote no encontrado"));
                    Ubicacion ubicacion = ubicacionRepository.findById(idUbicacion)
                            .orElseThrow(() -> new ReglaNegocioException("Ubicación no encontrada"));

                    Existencia nueva = new Existencia();
                    nueva.setProducto(producto);
                    nueva.setLote(lote);
                    nueva.setUbicacion(ubicacion);
                    nueva.setStockFisico(0L);
                    nueva.setStockReservado(0L);
                    nueva.setFechaActualizacion(OffsetDateTime.now());
                    return existenciaRepository.save(nueva);
                });
    }

    @Override
    public Existencia modificarStockFisico(Long idExistencia, long cantidadDelta) {
        Existencia existencia = existenciaRepository.findByIdWithLock(idExistencia)
                .orElseThrow(() -> new ReglaNegocioException("Existencia no encontrada con ID: " + idExistencia));

        long nuevoFisico = existencia.getStockFisico() + cantidadDelta;
        if (nuevoFisico < 0) {
            throw new ReglaNegocioException("Operación rechazada: generaría saldo físico negativo");
        }
        if (nuevoFisico < existencia.getStockReservado()) {
            throw new ReglaNegocioException("Operación rechazada: el stock físico no puede ser menor al reservado (" + existencia.getStockReservado() + ")");
        }

        existencia.setStockFisico(nuevoFisico);
        existencia.setFechaActualizacion(OffsetDateTime.now());
        return existenciaRepository.save(existencia);
    }

    @Override
    public Existencia modificarStockReservado(Long idExistencia, long cantidadDelta) {
        Existencia existencia = existenciaRepository.findByIdWithLock(idExistencia)
                .orElseThrow(() -> new ReglaNegocioException("Existencia no encontrada con ID: " + idExistencia));

        long nuevoReservado = existencia.getStockReservado() + cantidadDelta;
        if (nuevoReservado < 0) {
            throw new ReglaNegocioException("Operación rechazada: el stock reservado no puede ser negativo");
        }
        if (nuevoReservado > existencia.getStockFisico()) {
            throw new ReglaNegocioException("Operación rechazada: el stock reservado (" + nuevoReservado + ") no puede superar el stock físico (" + existencia.getStockFisico() + ")");
        }

        existencia.setStockReservado(nuevoReservado);
        existencia.setFechaActualizacion(OffsetDateTime.now());
        return existenciaRepository.save(existencia);
    }

    @Override
    public List<Existencia> consultarStockPorProducto(Long idProducto) {
        return existenciaRepository.findByProducto_IdProducto(idProducto);
    }

    @Override
    public List<Existencia> consultarStockPorUbicacion(Long idUbicacion) {
        return existenciaRepository.findByUbicacion_IdUbicacion(idUbicacion);
    }

    @Override
    public Existencia obtenerPorId(Long id) {
        return existenciaRepository.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("Existencia no encontrada con ID: " + id));
    }

    @Override
    public List<Existencia> listarTodas() {
        return existenciaRepository.findAll();
    }
}
