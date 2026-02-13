package com.lovepaws.app.admin.service;

import java.util.List;

import com.lovepaws.app.admin.domain.Categoria;

public interface CategoriaService {
    List<Categoria> listarActivas();
    Categoria obtenerActiva(Integer id);
    Categoria crear(Categoria categoria, Integer idUsuarioCreacion);
    Categoria actualizar(Integer id, Categoria categoria);
    void eliminar(Integer id);
}
