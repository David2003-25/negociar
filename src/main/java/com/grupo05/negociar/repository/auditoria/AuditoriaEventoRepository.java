package com.grupo05.negociar.repository.auditoria;

import com.grupo05.negociar.model.entidades.auditoria.AuditoriaEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaEventoRepository extends JpaRepository<AuditoriaEvento, Long> {

    List<AuditoriaEvento> findByEntidadAndIdRegistro(String entidad, String idRegistro);

    List<AuditoriaEvento> findByUsuario_IdUsuario(Long idUsuario);
}
