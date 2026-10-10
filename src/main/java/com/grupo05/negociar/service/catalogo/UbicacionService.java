package com.grupo05.negociar.service.catalogo;

import com.grupo05.negociar.model.entidades.catalogo.Ubicacion;

import java.util.List;

public interface UbicacionService {

    Ubicacion registrar(Ubicacion ubicacion);

    Ubicacion modificar(Long id, Ubicacion ubicacion);

    void desactivar(Long id);

    void activar(Long id);

    Ubicacion obtenerPorId(Long id);

    List<Ubicacion> listarActivas();

    List<Ubicacion> listarTodas();

    List<Ubicacion> listarPorTipo(String tipo);
}
