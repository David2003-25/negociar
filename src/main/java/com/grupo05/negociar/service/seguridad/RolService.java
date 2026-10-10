package com.grupo05.negociar.service.seguridad;

import com.grupo05.negociar.model.entidades.seguridad.Rol;

import java.util.List;

public interface RolService {

    List<Rol> listarActivos();

    List<Rol> listarTodos();

    Rol obtenerPorId(Long id);
}
