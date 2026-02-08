package com.lovepaws.app.mascota.service.impl;

import com.lovepaws.app.mascota.domain.EstadoMascota;
import com.lovepaws.app.mascota.domain.Mascota;
import com.lovepaws.app.mascota.repository.EstadoMascotaRepository;
import com.lovepaws.app.mascota.repository.MascotaRepository;
import com.lovepaws.app.mascota.service.MascotaService;
import com.lovepaws.app.user.domain.Usuario;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MascotaServiceImpl implements MascotaService {

    private final MascotaRepository mascotaRepository;
    private final EstadoMascotaRepository estadoRepo;
    
    

    @Override
    public Mascota createMascota(Mascota mascota) {
        if (mascota.getEstado() == null || mascota.getEstado().getId()==null
        		|| mascota.getEstado().getId().isBlank()) {
        	
        	EstadoMascota estado = new EstadoMascota();
            mascota.setEstado(estado);
        }
        return mascotaRepository.save(mascota);	
    }

 
    @Override
    public Mascota updateMascota(Mascota mascota) {
        Mascota existente = mascotaRepository.findById(mascota.getId())
                .orElseThrow(() -> new RuntimeException("Mascota no existe con id: " + mascota.getId()));

        
        existente.setNombre(mascota.getNombre());
        existente.setEdad(mascota.getEdad());
        existente.setDescripcion(mascota.getDescripcion());
        existente.setFotoUrl(mascota.getFotoUrl());
        existente.setSexo(mascota.getSexo());
        existente.setCategoria(mascota.getCategoria());
        existente.setRaza(mascota.getRaza());

       

        return mascotaRepository.save(existente);
    }


    @Override
    public List<Mascota> listarMascotasDisponibles() {
        return mascotaRepository.findByEstado_Id("DISPONIBLE");
    }

    @Override
    public List<Mascota> listarMascotas() {
        return mascotaRepository.findAll();
    }


    @Override
    public List<Mascota> buscarMascotasDisponibles(Integer categoriaId, Integer razaId, Integer edadMax, String q) {
        String query = (q == null || q.isBlank()) ? null : q.trim();
        return mascotaRepository.buscarDisponibles(categoriaId, razaId, edadMax, query);
    }


    @Override
    public Optional<Mascota> findMascotaById(Integer id) {
        return mascotaRepository.findById(id);
    }

    @Override
    public void deleteMascotaById(Integer id) {
        // Hibernate hará soft-delete por @SQLDelete en la entidad
        mascotaRepository.deleteById(id);
    }

	@Override
	@Transactional
	public void cambiarEstado(Integer mascotaId) {
		Mascota mascota = mascotaRepository.findById(mascotaId)
		        .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
		if (mascota.getEstado() == null) {
		    mascota.setEstado(estadoRepo.findById("DISPONIBLE").orElseThrow(() -> new RuntimeException("Estado no encontrado ")));
		}


		String estadoActual = mascota.getEstado().getId();

		 if ("ADOPTADA".equals(estadoActual)) {
		        throw new RuntimeException("No se puede modificar una mascota adoptada");
		    }
		 String nuevoEstado = estadoActual.equals("DISPONIBLE")
		            ? "NO_DISPONIBLE"
		            : "DISPONIBLE";

		 EstadoMascota estado = estadoRepo.findById(nuevoEstado)
		            .orElseThrow(() -> new RuntimeException("Estado no encontrado"));

		 mascota.setEstado(estado);
	    mascotaRepository.save(mascota);
		
	}

	@Override
	public Mascota updateMascotaSegura(Mascota mascota, Usuario usuarioActual) {
		Mascota existente = mascotaRepository.findById(mascota.getId())
	            .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

	    if (!existente.getUsuarioCreacion().getId().equals(usuarioActual.getId())) {
	        throw new AccessDeniedException("No puedes editar esta mascota");
	    }

	    existente.setNombre(mascota.getNombre());
	    existente.setEdad(mascota.getEdad());
	    existente.setDescripcion(mascota.getDescripcion());
	    existente.setFotoUrl(mascota.getFotoUrl());

	    return mascotaRepository.save(existente);
	}

	@Override
	public void deleteMascotaSegura(Integer mascotaId, Usuario usuarioActual) {
		Mascota mascota = mascotaRepository.findById(mascotaId)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        if (!mascota.getUsuarioCreacion().getId().equals(usuarioActual.getId())) {
            throw new AccessDeniedException("No puedes eliminar esta mascota");
        }

        mascotaRepository.delete(mascota);
		
	}
}
