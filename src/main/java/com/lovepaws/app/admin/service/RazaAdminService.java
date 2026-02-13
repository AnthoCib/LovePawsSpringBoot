package com.lovepaws.app.admin.service;

import java.util.List;

import com.lovepaws.app.mascota.domain.Raza;

public interface RazaAdminService {
    List<Raza> listarActivas();
    List<Raza> listarActivasPorEspecie(Integer especieId);
    Raza obtenerActiva(Integer id);
    Raza crear(Raza raza, Integer idUsuarioCreacion);
    Raza actualizar(Integer id, Raza raza);
    void eliminar(Integer id);
}
