package com.lovepaws.app.admin.service;

import java.util.List;

import com.lovepaws.app.mascota.domain.Categoria;

public interface CategoriaAdminService {
    List<Categoria> listarActivas();
    Categoria obtenerActiva(Integer id);
    Categoria crear(Categoria categoria, Integer idUsuarioCreacion);
    Categoria actualizar(Integer id, Categoria categoria);
    void eliminar(Integer id);
}
