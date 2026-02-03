package com.lovepaws.app.adopcion.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.lovepaws.app.adopcion.domain.Adopcion;
import com.lovepaws.app.adopcion.domain.EstadoAdopcion;
import com.lovepaws.app.adopcion.domain.SolicitudAdopcion;
import com.lovepaws.app.adopcion.repository.AdopcionRepository;
import com.lovepaws.app.adopcion.repository.SolicitudAdopcionRepository;
import com.lovepaws.app.adopcion.service.AdopcionService;
import com.lovepaws.app.mascota.domain.EstadoMascota;
import com.lovepaws.app.mascota.domain.Mascota;
import com.lovepaws.app.mascota.repository.MascotaRepository;
import com.lovepaws.app.user.domain.Usuario;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdopcionServiceImpl implements AdopcionService {

	private final AdopcionRepository adopcionRepo;
	private final SolicitudAdopcionRepository solicitudRepo;
	private final MascotaRepository mascotaRepo;

	@Override
	public Adopcion createAdopcion(Adopcion adopcion) {
		if (adopcion.getFechaAdopcion() == null) adopcion.setFechaAdopcion(LocalDateTime.now());
        return adopcionRepo.save(adopcion);
	}

	@Override
	public Optional<Adopcion> findAdopcionById(Integer id) {
		return adopcionRepo.findById(id);
	}

	@Override
	public List<Adopcion> listarAdopcionesPorUsuario(Integer usuarioId) {
		// TODO Auto-generated method stub
		return adopcionRepo.findByUsuarioAdoptante_Id(usuarioId);
	}

	@Override
	public List<Adopcion> listarAdopciones() {
		// TODO Auto-generated method stub
		return adopcionRepo.findAll();
	}

	/**
     * flujo transaccional:
     * - bloquea solicitud (pessimistic)
     * - bloquea mascota
     * - verifica estados
     * - crea adopcion
     * - actualiza solicitud = APROBADA
     * - actualiza mascota = ADOPTADA
     */
	@Override
	@Transactional
	public Adopcion aprobarSolicitud(Integer solicitudId, Integer gestorId) {	 
		
		
		SolicitudAdopcion solicitud = solicitudRepo.findByIdForUpdate(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

		if (solicitud.getEstado().getId().equals("APROBADA")) {
		    throw new IllegalStateException("Solicitud ya fue aprobada");
		}

        if (!"PENDIENTE".equals(solicitud.getEstado().getId())) {
            throw new IllegalStateException("Solicitud no está pendiente");
        }

        Mascota mascota = mascotaRepo.findByIdForUpdate(solicitud.getMascota().getId())
                .orElseThrow(() -> new IllegalArgumentException("Mascota no encontrada"));

        if (!"DISPONIBLE".equals(mascota.getEstado().getId())) {
            throw new IllegalStateException("Mascota no disponible");
        }

        // Estado adopción = APROBADA
        EstadoAdopcion estadoAdopcion = new EstadoAdopcion();
        estadoAdopcion.setId("APROBADA");

        // Usuario gestor (solo referencia)
        Usuario gestor = new Usuario();
        gestor.setId(gestorId);
        
        // Crear adopción
        Adopcion adopcion = Adopcion.builder()
                .usuarioAdoptante(solicitud.getUsuario())
                .mascota(mascota)
                .estado(estadoAdopcion)
                .solicitud(solicitud)
                .fechaAdopcion(LocalDateTime.now())
                .activo(true)
                .usuarioCreacion(gestor)
                .build();

        Adopcion saved = adopcionRepo.save(adopcion);

        // actualizar solicitud
        solicitud.setEstado(estadoAdopcion);
        solicitudRepo.save(solicitud);

        // actualizar mascota
        EstadoMascota estadoMascota = new EstadoMascota();
        estadoMascota.setId("ADOPTADA");
        mascota.setEstado(estadoMascota);
        mascotaRepo.save(mascota);

        return saved;
    }
	

}
