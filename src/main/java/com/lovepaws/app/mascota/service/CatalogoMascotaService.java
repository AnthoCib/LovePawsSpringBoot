package com.lovepaws.app.mascota.service;

import java.util.List;

import com.lovepaws.app.mascota.dto.CategoriaRequestDTO;
import com.lovepaws.app.mascota.dto.CategoriaResponseDTO;
import com.lovepaws.app.mascota.dto.EspecieRequestDTO;
import com.lovepaws.app.mascota.dto.EspecieResponseDTO;
import com.lovepaws.app.mascota.dto.RazaRequestDTO;
import com.lovepaws.app.mascota.dto.RazaResponseDTO;

public interface CatalogoMascotaService {

    List<EspecieResponseDTO> listarEspecies();

    EspecieResponseDTO obtenerEspecie(Integer especieId);

    EspecieResponseDTO crearEspecie(EspecieRequestDTO request, Integer usuarioId);

    EspecieResponseDTO actualizarEspecie(Integer especieId, EspecieRequestDTO request);

    void eliminarEspecie(Integer especieId);

    List<RazaResponseDTO> listarRazas(Integer especieId);

    RazaResponseDTO obtenerRaza(Integer razaId);

    RazaResponseDTO crearRaza(RazaRequestDTO request, Integer usuarioId);

    RazaResponseDTO actualizarRaza(Integer razaId, RazaRequestDTO request);

    void eliminarRaza(Integer razaId);

    List<CategoriaResponseDTO> listarCategorias();

    CategoriaResponseDTO obtenerCategoria(Integer categoriaId);

    CategoriaResponseDTO crearCategoria(CategoriaRequestDTO request, Integer usuarioId);

    CategoriaResponseDTO actualizarCategoria(Integer categoriaId, CategoriaRequestDTO request);

    void eliminarCategoria(Integer categoriaId);
}
