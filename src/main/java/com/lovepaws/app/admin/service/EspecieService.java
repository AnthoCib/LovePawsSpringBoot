package com.lovepaws.app.admin.service;

import java.util.List;

import com.lovepaws.app.admin.domain.Especie;

public interface EspecieService {
    List<Especie> listarActivas();
    Especie obtenerActiva(Integer id);
    Especie crear(Especie especie, Integer idUsuarioCreacion);
    Especie actualizar(Integer id, Especie especie);
    void eliminar(Integer id);
}
