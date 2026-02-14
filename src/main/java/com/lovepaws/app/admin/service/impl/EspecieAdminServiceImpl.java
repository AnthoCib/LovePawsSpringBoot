package com.lovepaws.app.admin.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lovepaws.app.admin.service.EspecieAdminService;
import com.lovepaws.app.mascota.domain.Especie;
import com.lovepaws.app.mascota.repository.EspecieRepository;
import com.lovepaws.app.mascota.repository.RazaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EspecieAdminServiceImpl implements EspecieAdminService {

    private final EspecieRepository especieRepository;
    private final RazaRepository razaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Especie> listarActivas() {
        return especieRepository.findByDeletedAtIsNull();
    }

    @Override
    @Transactional(readOnly = true)
    public Especie obtenerActiva(Integer id) {
        Especie especie = especieRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Especie no encontrada"));
        if (especie.getDeletedAt() != null) {
            throw new IllegalArgumentException("Especie no disponible");
        }
        return especie;
    }

    @Override
    public Especie crear(Especie especie, Integer idUsuarioCreacion) {
        String nombre = especie.getNombre().trim();
        if (especieRepository.existsByNombreIgnoreCaseAndDeletedAtIsNull(nombre)) {
            throw new IllegalStateException("Ya existe una especie activa con ese nombre");
        }
        especie.setNombre(nombre);
        especie.setEstado(especie.getEstado() == null ? Boolean.TRUE : especie.getEstado());
        especie.setIdUsuarioCreacion(idUsuarioCreacion);
        return especieRepository.save(especie);
    }

    @Override
    public Especie actualizar(Integer id, Especie especie) {
        Especie actual = obtenerActiva(id);
        String nombre = especie.getNombre().trim();
        if (especieRepository.existsByNombreIgnoreCaseAndDeletedAtIsNullAndIdNot(nombre, id)) {
            throw new IllegalStateException("Ya existe una especie activa con ese nombre");
        }
        actual.setNombre(nombre);
        actual.setEstado(especie.getEstado() == null ? Boolean.TRUE : especie.getEstado());
        return especieRepository.save(actual);
    }

    @Override
    public void eliminar(Integer id) {
        Especie actual = obtenerActiva(id);
        if (razaRepository.countByEspecieIdAndDeletedAtIsNullAndEstadoTrue(id) > 0) {
            throw new IllegalStateException("No se puede eliminar la especie porque tiene razas activas asociadas");
        }
        actual.setDeletedAt(LocalDateTime.now());
        especieRepository.save(actual);
    }
}
