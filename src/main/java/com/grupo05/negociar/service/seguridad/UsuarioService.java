package com.grupo05.negociar.service.seguridad;

import com.grupo05.negociar.model.entidades.seguridad.Usuario;

import java.util.List;

public interface UsuarioService {

    Usuario registrar(Usuario usuario, Long idRol);

    Usuario modificar(Long id, Usuario usuario, Long idRol);

    void cambiarEstado(Long id, boolean estado);

    Usuario autenticar(String correo, String password);

    Usuario obtenerPorId(Long id);

    List<Usuario> listarTodos();

    List<Usuario> listarActivos();
}
