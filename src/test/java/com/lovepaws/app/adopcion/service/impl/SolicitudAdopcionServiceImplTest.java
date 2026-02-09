package com.lovepaws.app.adopcion.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lovepaws.app.adopcion.domain.EstadoAdopcion;
import com.lovepaws.app.adopcion.domain.SolicitudAdopcion;
import com.lovepaws.app.adopcion.repository.SolicitudAdopcionRepository;
import com.lovepaws.app.mail.EmailService;
import com.lovepaws.app.mascota.domain.EstadoMascota;
import com.lovepaws.app.mascota.domain.Mascota;
import com.lovepaws.app.mascota.repository.MascotaRepository;
import com.lovepaws.app.user.domain.Usuario;
import com.lovepaws.app.user.service.AuditoriaService;

@ExtendWith(MockitoExtension.class)
class SolicitudAdopcionServiceImplTest {

	@Mock
	private SolicitudAdopcionRepository solicitudRepo;
	@Mock
	private EmailService emailService;
	@Mock
	private MascotaRepository mascotaRepository;
	@Mock
	private AuditoriaService auditoriaService;

	@InjectMocks
	private SolicitudAdopcionServiceImpl service;

	@Test
	void createSolicitudGuardaYAudita() {
		Usuario usuario = usuarioCompleto();
		Mascota mascota = mascotaDisponible(22);
		SolicitudAdopcion solicitud = solicitudBase(usuario, mascota);

		when(mascotaRepository.findById(22)).thenReturn(Optional.of(mascota));
		when(solicitudRepo.existsByMascota_IdAndEstado_Id(22, "PENDIENTE")).thenReturn(false);
		when(solicitudRepo.existsByUsuario_IdAndEstado_Id(usuario.getId(), "PENDIENTE")).thenReturn(false);
		when(solicitudRepo.save(solicitud)).thenReturn(solicitud);

		SolicitudAdopcion saved = service.createSolicitud(solicitud);

		assertNotNull(saved);
		verify(auditoriaService).registrar(eq("solicitud_adopcion"), eq(saved.getId()), eq("CREAR_SOLICITUD"),
				eq(usuario.getId()), eq(usuario.getNombre()), any());
		verify(emailService).enviarCorreo(eq(usuario.getCorreo()), any(), any());
	}

	@Test
	void createSolicitudFallaSiMascotaNoDisponible() {
		Usuario usuario = usuarioCompleto();
		Mascota mascota = mascotaNoDisponible(10);
		SolicitudAdopcion solicitud = solicitudBase(usuario, mascota);

		when(mascotaRepository.findById(10)).thenReturn(Optional.of(mascota));

		assertThrows(IllegalStateException.class, () -> service.createSolicitud(solicitud));
	}

	@Test
	void createSolicitudFallaSiMascotaTieneSolicitudActiva() {
		Usuario usuario = usuarioCompleto();
		Mascota mascota = mascotaDisponible(4);
		SolicitudAdopcion solicitud = solicitudBase(usuario, mascota);

		when(mascotaRepository.findById(4)).thenReturn(Optional.of(mascota));
		when(solicitudRepo.existsByMascota_IdAndEstado_Id(4, "PENDIENTE")).thenReturn(true);

		assertThrows(IllegalStateException.class, () -> service.createSolicitud(solicitud));
	}

	@Test
	void createSolicitudFallaSiUsuarioIncompleto() {
		Usuario usuario = new Usuario();
		usuario.setId(5);
		Mascota mascota = mascotaDisponible(6);
		SolicitudAdopcion solicitud = solicitudBase(usuario, mascota);

		when(mascotaRepository.findById(6)).thenReturn(Optional.of(mascota));
		when(solicitudRepo.existsByMascota_IdAndEstado_Id(6, "PENDIENTE")).thenReturn(false);
		when(solicitudRepo.existsByUsuario_IdAndEstado_Id(5, "PENDIENTE")).thenReturn(false);

		assertThrows(IllegalStateException.class, () -> service.createSolicitud(solicitud));
	}

	@Test
	void aprobarSolicitudActualizaEstadoYAudita() {
		SolicitudAdopcion solicitud = solicitudConEstado("PENDIENTE");
		solicitud.setId(11);
		solicitud.setMascota(mascotaDisponible(8));
		solicitud.setUsuario(usuarioCompleto());

		when(solicitudRepo.findById(11)).thenReturn(Optional.of(solicitud));
		when(solicitudRepo.save(any(SolicitudAdopcion.class))).thenReturn(solicitud);

		SolicitudAdopcion updated = service.aprobarSolicitud(11, 99);

		assertEquals("APROBADA", updated.getEstado().getId());
		verify(auditoriaService).registrar(eq("solicitud_adopcion"), eq(11), eq("CAMBIO_ESTADO"), eq(99),
				eq("GESTOR"), any());
		verify(emailService).enviarCorreo(eq(updated.getUsuario().getCorreo()), any(), any());
	}

	@Test
	void cancelarSolicitudActualizaEstadoYAudita() {
		Usuario usuario = usuarioCompleto();
		SolicitudAdopcion solicitud = solicitudConEstado("PENDIENTE");
		solicitud.setId(7);
		solicitud.setUsuario(usuario);

		when(solicitudRepo.findById(7)).thenReturn(Optional.of(solicitud));
		when(solicitudRepo.save(any(SolicitudAdopcion.class))).thenReturn(solicitud);

		SolicitudAdopcion updated = service.cancelarSolicitud(7, usuario.getId());

		assertEquals("CANCELADA", updated.getEstado().getId());
		verify(auditoriaService).registrar(eq("solicitud_adopcion"), eq(7), eq("CAMBIO_ESTADO"), eq(usuario.getId()),
				eq(usuario.getNombre()), any());
		verify(emailService).enviarCorreo(eq(usuario.getCorreo()), any(), any());
	}

	private Usuario usuarioCompleto() {
		Usuario usuario = new Usuario();
		usuario.setId(3);
		usuario.setNombre("Luz Perez");
		usuario.setCorreo("luz@example.com");
		usuario.setTelefono("999111222");
		usuario.setDireccion("Av. Principal 123");
		return usuario;
	}

	private Mascota mascotaDisponible(Integer id) {
		Mascota mascota = new Mascota();
		mascota.setId(id);
		EstadoMascota estado = new EstadoMascota();
		estado.setId("DISPONIBLE");
		mascota.setEstado(estado);
		return mascota;
	}

	private Mascota mascotaNoDisponible(Integer id) {
		Mascota mascota = new Mascota();
		mascota.setId(id);
		EstadoMascota estado = new EstadoMascota();
		estado.setId("ADOPTADA");
		mascota.setEstado(estado);
		return mascota;
	}

	private SolicitudAdopcion solicitudBase(Usuario usuario, Mascota mascota) {
		SolicitudAdopcion solicitud = new SolicitudAdopcion();
		solicitud.setUsuario(usuario);
		solicitud.setMascota(mascota);
		EstadoAdopcion estado = new EstadoAdopcion();
		estado.setId("PENDIENTE");
		solicitud.setEstado(estado);
		return solicitud;
	}

	private SolicitudAdopcion solicitudConEstado(String estadoId) {
		SolicitudAdopcion solicitud = new SolicitudAdopcion();
		EstadoAdopcion estado = new EstadoAdopcion();
		estado.setId(estadoId);
		solicitud.setEstado(estado);
		return solicitud;
	}
}
