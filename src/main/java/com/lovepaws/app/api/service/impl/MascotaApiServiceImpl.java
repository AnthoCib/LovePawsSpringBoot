package com.lovepaws.app.api.service.impl;

import com.lovepaws.app.api.dto.MascotaRequestDTO;
import com.lovepaws.app.api.dto.MascotaResponseDTO;
import com.lovepaws.app.api.exception.ApiBadRequestException;
import com.lovepaws.app.api.exception.ApiNotFoundException;
import com.lovepaws.app.api.mapper.MascotaApiMapper;
import com.lovepaws.app.api.service.MascotaApiService;
import com.lovepaws.app.mascota.domain.Categoria;
import com.lovepaws.app.mascota.domain.EstadoMascota;
import com.lovepaws.app.mascota.domain.Mascota;
import com.lovepaws.app.mascota.domain.Raza;
import com.lovepaws.app.mascota.repository.CategoriaRepository;
import com.lovepaws.app.mascota.repository.EstadoMascotaRepository;
import com.lovepaws.app.mascota.repository.RazaRepository;
import com.lovepaws.app.mascota.service.MascotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MascotaApiServiceImpl implements MascotaApiService {

    private final MascotaService mascotaService;
    private final CategoriaRepository categoriaRepository;
    private final RazaRepository razaRepository;
    private final EstadoMascotaRepository estadoMascotaRepository;
    private final MascotaApiMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<MascotaResponseDTO> findAll() {
        return mascotaService.listarMascotas().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaResponseDTO> findDisponibles() {
        return mascotaService.listarMascotasDisponibles().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MascotaResponseDTO findById(Integer id) {
        Mascota mascota = mascotaService.findMascotaById(id)
                .orElseThrow(() -> new ApiNotFoundException("Mascota no encontrada con id: " + id));
        return mapper.toResponse(mascota);
    }

    @Override
    public MascotaResponseDTO create(MascotaRequestDTO request) {
        Mascota mascota = mapForCreate(request);
        return mapper.toResponse(mascotaService.createMascota(mascota));
    }

    @Override
    public MascotaResponseDTO update(Integer id, MascotaRequestDTO request) {
        Mascota mascota = mascotaService.findMascotaById(id)
                .orElseThrow(() -> new ApiNotFoundException("Mascota no encontrada con id: " + id));

        applyRequest(mascota, request);
        return mapper.toResponse(mascotaService.updateMascota(mascota));
    }

    @Override
    public void delete(Integer id) {
        if (mascotaService.findMascotaById(id).isEmpty()) {
            throw new ApiNotFoundException("Mascota no encontrada con id: " + id);
        }
        mascotaService.deleteMascotaById(id);
    }

    private Mascota mapForCreate(MascotaRequestDTO request) {
        Mascota mascota = new Mascota();
        applyRequest(mascota, request);
        return mascota;
    }

    private void applyRequest(Mascota mascota, MascotaRequestDTO request) {
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ApiBadRequestException("Categoría inválida"));

        Raza raza = razaRepository.findById(request.getRazaId())
                .orElseThrow(() -> new ApiBadRequestException("Raza inválida"));

        mascota.setNombre(request.getNombre());
        mascota.setEdad(request.getEdad());
        mascota.setSexo(request.getSexo());
        mascota.setDescripcion(request.getDescripcion());
        mascota.setFotoUrl(request.getFotoUrl());
        mascota.setCategoria(categoria);
        mascota.setRaza(raza);

        String estadoId = (request.getEstadoId() == null || request.getEstadoId().isBlank())
                ? "DISPONIBLE"
                : request.getEstadoId();

        EstadoMascota estado = estadoMascotaRepository.findById(estadoId)
                .orElseThrow(() -> new ApiBadRequestException("Estado inválido: " + estadoId));
        mascota.setEstado(estado);
    }
}
