package com.grupo05.negociar.service.seguridad.impl;

import com.grupo05.negociar.common.exception.ReglaNegocioException;
import com.grupo05.negociar.model.entidades.seguridad.Rol;
import com.grupo05.negociar.repository.seguridad.RolRepository;
import com.grupo05.negociar.service.seguridad.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;

    @Override
    public List<Rol> listarActivos() {
        return rolRepository.findByEstadoTrue();
    }

    @Override
    public List<Rol> listarTodos() {
        return rolRepository.findAll();
    }

    @Override
    public Rol obtenerPorId(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("Rol no encontrado con ID: " + id));
    }
}
