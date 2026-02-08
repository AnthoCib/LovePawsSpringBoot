package com.lovepaws.app.admin.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lovepaws.app.admin.dto.MascotaPorEstadoDTO;
import com.lovepaws.app.admin.dto.UsuarioPorRolDTO;
import com.lovepaws.app.admin.repository.ReporteRepository;
import com.lovepaws.app.admin.service.ReporteService;
import com.lovepaws.app.mascota.repository.MascotaRepository;
import com.lovepaws.app.user.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final UsuarioRepository usuarioRepo;
    private final MascotaRepository mascotaRepo;
    private final ReporteRepository reporteRepo;

    @Override
    public Long totalUsuarios() {
        return usuarioRepo.count();
    }

    @Override
    public Long totalMascotas() {
        return mascotaRepo.count();
    }

    @Override
    public List<MascotaPorEstadoDTO> mascotasPorEstado(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null || hasta.isBefore(desde)) {
            return reporteRepo.mascotasPorEstado();
        }
        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime finExclusivo = hasta.plusDays(1).atStartOfDay();
        return reporteRepo.mascotasPorEstado(inicio, finExclusivo);
    }

    @Override
    public List<UsuarioPorRolDTO> usuariosPorRol(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null || hasta.isBefore(desde)) {
            return reporteRepo.usuariosPorRol();
        }
        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime finExclusivo = hasta.plusDays(1).atStartOfDay();
        return reporteRepo.usuariosPorRol(inicio, finExclusivo);
    }

    @Override
    public List<MascotaPorEstadoDTO> mascotasPorEstado() {
        return reporteRepo.mascotasPorEstado();
    }

    @Override
    public List<UsuarioPorRolDTO> usuariosPorRol() {
        return reporteRepo.usuariosPorRol();
    }
}
