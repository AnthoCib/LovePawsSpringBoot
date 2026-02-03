package com.lovepaws.app.mascota.service;

import java.util.List;
import java.util.Optional;

import com.lovepaws.app.mascota.domain.Mascota;
import com.lovepaws.app.user.domain.Usuario;

public interface MascotaService {

	Mascota createMascota(Mascota mascota);

	Mascota updateMascota(Mascota mascota);

	List<Mascota> listarMascotasDisponibles();

	List<Mascota> listarMascotas(); // all non-deleted

	Optional<Mascota> findMascotaById(Integer id);

	void deleteMascotaById(Integer id); // soft delet

	Mascota updateMascotaSegura(Mascota mascota, Usuario usuarioActual);

	void deleteMascotaSegura(Integer mascotaId, Usuario usuarioActual);

	void cambiarEstado(Integer mascotaId);

}
