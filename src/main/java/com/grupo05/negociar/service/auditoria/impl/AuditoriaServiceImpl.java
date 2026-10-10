package com.grupo05.negociar.service.auditoria.impl;

import com.grupo05.negociar.model.entidades.auditoria.AuditoriaEvento;
import com.grupo05.negociar.model.entidades.seguridad.Usuario;
import com.grupo05.negociar.repository.auditoria.AuditoriaEventoRepository;
import com.grupo05.negociar.repository.seguridad.UsuarioRepository;
import com.grupo05.negociar.service.auditoria.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaEventoRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public void registrar(Long idUsuario, String entidad, String idRegistro, String accion,
                          String detalle, String datosAnteriores, String datosNuevos) {
        Usuario usuario = idUsuario != null ? usuarioRepository.findById(idUsuario).orElse(null) : null;

        AuditoriaEvento evento = new AuditoriaEvento();
        evento.setUsuario(usuario);
        evento.setEntidad(entidad);
        evento.setIdRegistro(idRegistro);
        evento.setAccion(accion);
        evento.setDetalle(detalle);
        evento.setDatosAnteriores(sanitizar(datosAnteriores));
        evento.setDatosNuevos(sanitizar(datosNuevos));
        evento.setFechaEvento(OffsetDateTime.now());

        auditoriaRepository.save(evento);
    }

    @Override
    public Page<AuditoriaEvento> listarPaginado(Pageable pageable) {
        return auditoriaRepository.findAll(pageable);
    }

    @Override
    public List<AuditoriaEvento> listarPorEntidadYRegistro(String entidad, String idRegistro) {
        return auditoriaRepository.findByEntidadAndIdRegistro(entidad, idRegistro);
    }

    private String sanitizar(String json) {
        if (json == null) return null;
        // RF13: Evitar almacenar contraseñas o tokens en auditoría
        return json.replaceAll("(?i)\"password\"\\s*:\\s*\"[^\"]*\"", "\"password\":\"***\"")
                   .replaceAll("(?i)\"token\"\\s*:\\s*\"[^\"]*\"", "\"token\":\"***\"");
    }
}
