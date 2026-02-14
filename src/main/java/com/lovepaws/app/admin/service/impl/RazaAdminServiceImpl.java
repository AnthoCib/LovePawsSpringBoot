package com.lovepaws.app.admin.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lovepaws.app.admin.service.RazaAdminService;
import com.lovepaws.app.mascota.domain.Especie;
import com.lovepaws.app.mascota.domain.Raza;
import com.lovepaws.app.mascota.repository.EspecieRepository;
import com.lovepaws.app.mascota.repository.MascotaRepository;
import com.lovepaws.app.mascota.repository.RazaRepository;
import com.lovepaws.app.user.domain.Usuario;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RazaAdminServiceImpl implements RazaAdminService {

    private final RazaRepository razaRepository;
    private final EspecieRepository especieRepository;
    private final MascotaRepository mascotaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Raza> listarActivas() {
        return razaRepository.findByDeletedAtIsNull();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Raza> listarActivasPorEspecie(Integer especieId) {
        return razaRepository.findByEspecieIdAndDeletedAtIsNull(especieId);
    }

    @Override
    @Transactional(readOnly = true)
    public Raza obtenerActiva(Integer id) {
        Raza raza = razaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Raza no encontrada"));
        if (raza.getDeletedAt() != null) {
            throw new IllegalArgumentException("Raza no disponible");
        }
        return raza;
    }

    @Override
    public Raza crear(Raza raza, Integer idUsuarioCreacion) {
        Especie especie = especieRepository.findById(raza.getEspecie().getId())
                .orElseThrow(() -> new IllegalArgumentException("Especie no encontrada"));
        if (especie.getDeletedAt() != null) {
            throw new IllegalStateException("No se puede usar una especie eliminada");
        }
        String nombre = raza.getNombre().trim();
        if (razaRepository.existsByNombreIgnoreCaseAndEspecieAndDeletedAtIsNull(nombre, especie)) {
            throw new IllegalStateException("Ya existe una raza activa con ese nombre para la especie seleccionada");
        }
        raza.setNombre(nombre);
        raza.setEspecie(especie);
        raza.setEstado(raza.getEstado() == null ? Boolean.TRUE : raza.getEstado());
        if (idUsuarioCreacion != null) {
            Usuario user = new Usuario();
            user.setId(idUsuarioCreacion);
            raza.setUsuarioCreacion(user);
        }
        return razaRepository.save(raza);
    }

    @Override
    public Raza actualizar(Integer id, Raza raza) {
        Raza actual = obtenerActiva(id);
        Especie especie = especieRepository.findById(raza.getEspecie().getId())
                .orElseThrow(() -> new IllegalArgumentException("Especie no encontrada"));
        if (especie.getDeletedAt() != null) {
            throw new IllegalStateException("No se puede usar una especie eliminada");
        }
        String nombre = raza.getNombre().trim();
        if (razaRepository.existsByNombreIgnoreCaseAndEspecieAndDeletedAtIsNullAndIdNot(nombre, especie, id)) {
            throw new IllegalStateException("Ya existe una raza activa con ese nombre para la especie seleccionada");
        }
        actual.setNombre(nombre);
        actual.setEspecie(especie);
        actual.setEstado(raza.getEstado() == null ? Boolean.TRUE : raza.getEstado());
        return razaRepository.save(actual);
    }

    @Override
    public void eliminar(Integer id) {
        Raza actual = obtenerActiva(id);
        if (mascotaRepository.countByRaza_IdAndDeletedAtIsNull(id) > 0) {
            throw new IllegalStateException("No se puede eliminar la raza porque tiene mascotas asociadas");
        }
        actual.setDeletedAt(LocalDateTime.now());
        razaRepository.save(actual);
    }
}
