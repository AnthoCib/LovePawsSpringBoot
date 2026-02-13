package com.lovepaws.app.admin.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lovepaws.app.admin.service.CategoriaAdminService;
import com.lovepaws.app.mascota.domain.Categoria;
import com.lovepaws.app.mascota.repository.CategoriaRepository;
import com.lovepaws.app.mascota.repository.MascotaRepository;
import com.lovepaws.app.user.domain.Usuario;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaAdminServiceImpl implements CategoriaAdminService {

    private final CategoriaRepository categoriaRepository;
    private final MascotaRepository mascotaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> listarActivas() {
        return categoriaRepository.findByDeletedAtIsNull();
    }

    @Override
    @Transactional(readOnly = true)
    public Categoria obtenerActiva(Integer id) {
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        if (categoria.getDeletedAt() != null) {
            throw new IllegalArgumentException("Categoría no disponible");
        }
        return categoria;
    }

    @Override
    public Categoria crear(Categoria categoria, Integer idUsuarioCreacion) {
        String nombre = categoria.getNombre().trim();
        if (categoriaRepository.existsByNombreIgnoreCaseAndDeletedAtIsNull(nombre)) {
            throw new IllegalStateException("Ya existe una categoría activa con ese nombre");
        }
        categoria.setNombre(nombre);
        categoria.setEstado(categoria.getEstado() == null ? Boolean.TRUE : categoria.getEstado());
        if (idUsuarioCreacion != null) {
            Usuario user = new Usuario();
            user.setId(idUsuarioCreacion);
            categoria.setUsuarioCreacion(user);
        }
        return categoriaRepository.save(categoria);
    }

    @Override
    public Categoria actualizar(Integer id, Categoria categoria) {
        Categoria actual = obtenerActiva(id);
        String nombre = categoria.getNombre().trim();
        if (categoriaRepository.existsByNombreIgnoreCaseAndDeletedAtIsNullAndIdNot(nombre, id)) {
            throw new IllegalStateException("Ya existe una categoría activa con ese nombre");
        }
        actual.setNombre(nombre);
        actual.setDescripcion(categoria.getDescripcion());
        actual.setEstado(categoria.getEstado() == null ? Boolean.TRUE : categoria.getEstado());
        return categoriaRepository.save(actual);
    }

    @Override
    public void eliminar(Integer id) {
        Categoria actual = obtenerActiva(id);
        if (mascotaRepository.countByCategoria_IdAndDeletedAtIsNull(id) > 0) {
            throw new IllegalStateException("No se puede eliminar la categoría porque tiene mascotas asociadas");
        }
        actual.setDeletedAt(LocalDateTime.now());
        categoriaRepository.save(actual);
    }
}
