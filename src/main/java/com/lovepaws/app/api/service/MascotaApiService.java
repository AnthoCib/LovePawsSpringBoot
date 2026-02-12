package com.lovepaws.app.api.service;

import com.lovepaws.app.api.dto.MascotaRequestDTO;
import com.lovepaws.app.api.dto.MascotaResponseDTO;

import java.util.List;

public interface MascotaApiService {
    List<MascotaResponseDTO> findAll();
    List<MascotaResponseDTO> findDisponibles();
    MascotaResponseDTO findById(Integer id);
    MascotaResponseDTO create(MascotaRequestDTO request);
    MascotaResponseDTO update(Integer id, MascotaRequestDTO request);
    void delete(Integer id);
}
