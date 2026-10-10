package com.grupo05.negociar.service.seguridad.impl;

import com.grupo05.negociar.common.exception.ReglaNegocioException;
import com.grupo05.negociar.model.entidades.seguridad.Rol;
import com.grupo05.negociar.model.entidades.seguridad.Usuario;
import com.grupo05.negociar.repository.seguridad.RolRepository;
import com.grupo05.negociar.repository.seguridad.UsuarioRepository;
import com.grupo05.negociar.service.seguridad.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    @Override
    public Usuario registrar(Usuario usuario, Long idRol) {
        if (usuario.getCorreo() == null || usuario.getCorreo().trim().isEmpty()) {
            throw new ReglaNegocioException("El correo electrónico es obligatorio");
        }
        if (usuarioRepository.existsByCorreo(usuario.getCorreo().trim())) {
            throw new ReglaNegocioException("El correo ya se encuentra registrado");
        }

        Rol rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new ReglaNegocioException("El rol seleccionado no existe"));
        if (!Boolean.TRUE.equals(rol.getEstado())) {
            throw new ReglaNegocioException("No se puede asignar un rol inactivo");
        }

        usuario.setRol(rol);
        usuario.setCorreo(usuario.getCorreo().trim().toLowerCase());
        usuario.setPasswordHash(hashearPassword(usuario.getPasswordHash()));
        usuario.setEstado(true);
        usuario.setFechaCreacion(OffsetDateTime.now());

        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario modificar(Long id, Usuario usuario, Long idRol) {
        Usuario existente = obtenerPorId(id);

        if (usuario.getCorreo() != null && !usuario.getCorreo().trim().equalsIgnoreCase(existente.getCorreo())) {
            if (usuarioRepository.existsByCorreo(usuario.getCorreo().trim())) {
                throw new ReglaNegocioException("El correo ya está en uso por otro usuario");
            }
            existente.setCorreo(usuario.getCorreo().trim().toLowerCase());
        }

        if (idRol != null) {
            Rol rol = rolRepository.findById(idRol)
                    .orElseThrow(() -> new ReglaNegocioException("El rol seleccionado no existe"));
            existente.setRol(rol);
        }

        existente.setNombres(usuario.getNombres());
        existente.setApellidos(usuario.getApellidos());

        if (usuario.getPasswordHash() != null && !usuario.getPasswordHash().trim().isEmpty()) {
            existente.setPasswordHash(hashearPassword(usuario.getPasswordHash()));
        }

        return usuarioRepository.save(existente);
    }

    @Override
    public void cambiarEstado(Long id, boolean estado) {
        Usuario usuario = obtenerPorId(id);
        usuario.setEstado(estado);
        usuarioRepository.save(usuario);
    }

    @Override
    public Usuario autenticar(String correo, String password) {
        if (correo == null || password == null) {
            throw new ReglaNegocioException("Credenciales incompletas");
        }
        Usuario usuario = usuarioRepository.findByCorreo(correo.trim().toLowerCase())
                .orElseThrow(() -> new ReglaNegocioException("Usuario o contraseña incorrectos"));

        if (!Boolean.TRUE.equals(usuario.getEstado())) {
            throw new ReglaNegocioException("El usuario se encuentra inactivo");
        }
        if (!Boolean.TRUE.equals(usuario.getRol().getEstado())) {
            throw new ReglaNegocioException("El rol del usuario está inactivo");
        }

        String hashIngresado = hashearPassword(password);
        if (!hashIngresado.equals(usuario.getPasswordHash())) {
            throw new ReglaNegocioException("Usuario o contraseña incorrectos");
        }

        usuario.setUltimoAcceso(OffsetDateTime.now());
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("Usuario no encontrado con ID: " + id));
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    public List<Usuario> listarActivos() {
        return usuarioRepository.findByEstadoTrue();
    }

    private String hashearPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new ReglaNegocioException("La contraseña no puede estar vacía");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error en algoritmo de hash", e);
        }
    }
}
