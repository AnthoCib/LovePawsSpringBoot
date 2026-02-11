package com.lovepaws.app.user.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lovepaws.app.mail.EmailService;
import com.lovepaws.app.security.UsuarioPrincipal;
import com.lovepaws.app.user.domain.EstadoUsuario;
import com.lovepaws.app.user.domain.Rol;
import com.lovepaws.app.user.domain.Usuario;
import com.lovepaws.app.user.repository.EstadoUsuarioRepository;
import com.lovepaws.app.user.repository.RolRepository;
import com.lovepaws.app.user.repository.UsuarioRepository;
import com.lovepaws.app.user.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

	private final PasswordEncoder passwordEncoder;

	private final UsuarioRepository usuarioRepo;
	private final RolRepository rolRepo;
	private final EstadoUsuarioRepository estadoUsuarioRepo;
	private final EmailService emailService;
	private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);

	@Override
	@Transactional
	public Usuario createUsuario(Usuario usuario) {
		// TODO Auto-generated method stub
		return usuarioRepo.save(usuario);
	}

	@Override
	@Transactional
	public Usuario updateUsuario(Usuario usuario) {
		// TODO Auto-generated method stub
		return usuarioRepo.save(usuario);
	}

	@Override
	public List<Usuario> listarUsuarios() {
		// TODO Auto-generated method stub
		return usuarioRepo.findAll();
	}

	@Override
	public Optional<Usuario> findUsuarioById(Integer idUsuario) {
		// TODO Auto-generated method stub
		return usuarioRepo.findById(idUsuario);
	}

	@Override
	public Optional<Usuario> findByUsername(String username) {
		// TODO Auto-generated method stub
		return usuarioRepo.findByUsername(username);
	}

	@Override
	@Transactional
	public void deleteUsuarioById(Integer idUsuario) {
		usuarioRepo.findById(idUsuario).ifPresent(u -> {
			u.setDeletedAt(LocalDateTime.now());
			usuarioRepo.save(u);
		});

	}

	@Override
	public Optional<Usuario> findByCorreo(String correo) {
		// TODO Auto-generated method stub
		return usuarioRepo.findByCorreo(correo);
	}

	@Override
	public Optional<Usuario> findByResetToken(String resetToken) {
		return usuarioRepo.findByResetToken(resetToken);
	}

	@Override
	public void cambiarEstado(Integer usuarioId) {
		Usuario usuario = usuarioRepo.findById(usuarioId)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		String estadoActual = usuario.getEstado().getId();

		String nuevoEstadoId = estadoActual.equals("ACTIVO") ? "BLOQUEADO" : "ACTIVO";

		EstadoUsuario nuevoEstado = estadoUsuarioRepo.findById(nuevoEstadoId)
				.orElseThrow(() -> new RuntimeException("Estado no encontrado"));

		usuario.setEstado(nuevoEstado);

		usuarioRepo.save(usuario);

	}

	@Override
	public void cambiarRol(Integer usuarioId, Integer rolId) {

		UsuarioPrincipal principal = (UsuarioPrincipal) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();

		Integer usuarioAutenticadoId = principal.getUsuario().getId();

		// Regla de seguridad

		if (usuarioId.equals(usuarioAutenticadoId)) {
			throw new RuntimeException("No puedes cambiar tu propio rol");
		}

		Usuario usuario = usuarioRepo.findById(usuarioId)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		Rol nuevoRol = rolRepo.findById(rolId).orElseThrow(() -> new RuntimeException("Rol no encontrado"));

		if (usuario.getRol().getNombre().equals("ADMIN") && !nuevoRol.getNombre().equals("ADMIN")) {

			long totalAdmins = usuarioRepo.countByRolNombre("ADMIN");

			if (totalAdmins <= 1) {
				throw new RuntimeException("No puedes quitar el último administrador del sistema");
			}
		}

		usuario.setRol(nuevoRol);
		usuarioRepo.save(usuario);

	}

	@Override
	public void crearUsuarioDesdeAdmin(Usuario usuario) {
		if (usuario.getUsername() == null || usuario.getUsername().isBlank()) {
			throw new RuntimeException("Username obligatorio");
		}
		if (usuario.getCorreo() == null || usuario.getCorreo().isBlank()) {
			throw new RuntimeException("Correo obligatorio");
		}
		if (usuario.getPasswordHash() == null || usuario.getPasswordHash().isBlank()) {
			throw new RuntimeException("Contraseña obligatoria");
		}

		Integer rolId = usuario.getRolId();
		if (rolId == null && usuario.getRol() != null) {
			rolId = usuario.getRol().getId();
		}
		if (rolId == null) {
			throw new RuntimeException("Rol obligatorio");
		}

		if (usuarioRepo.existsByUsername(usuario.getUsername())) {
			throw new RuntimeException("El username ya existe");
		}
		if (usuarioRepo.existsByCorreo(usuario.getCorreo())) {
			throw new RuntimeException("El correo ya está registrado");
		}

		Rol rol = rolRepo.findById(rolId).orElseThrow(() -> new RuntimeException("Rol inválido"));
		usuario.setRol(rol);

		EstadoUsuario estado = estadoUsuarioRepo.findById("ACTIVO")
				.orElseThrow(() -> new RuntimeException("Estado ACTIVO no encontrado"));
		usuario.setEstado(estado);

		usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));
		usuario.setFechaCreacion(LocalDateTime.now());
		usuario.setDeletedAt(null);

		usuarioRepo.save(usuario);
	}



	@Override
	@Transactional
	public boolean solicitarRecuperacionPassword(String correo, String baseUrl) {
		if (correo == null || correo.isBlank()) {
			return false;
		}
		Optional<Usuario> usuarioOpt = usuarioRepo.findByCorreo(correo.trim());
		if (usuarioOpt.isEmpty()) {
			return false;
		}
		Usuario usuario = usuarioOpt.get();
		String tokenSeguro = UUID.randomUUID().toString();
		usuario.setResetToken(tokenSeguro);
		usuario.setResetTokenExpira(LocalDateTime.now().plusMinutes(30));
		usuarioRepo.saveAndFlush(usuario);

		String link = baseUrl + "/usuarios/reset-password?token=" + tokenSeguro;
		String contenido = "<p>Hola " + usuario.getNombre() + ",</p>"
				+ "<p>Para restablecer tu contraseña haz clic en el siguiente enlace:</p>"
				+ "<p><a href=\"" + link + "\">Restablecer contraseña</a></p>"
				+ "<p>Este enlace expirará en 30 minutos.</p>";

		try {
			emailService.enviarCorreo(usuario.getCorreo(), "Recuperación de contraseña", contenido);
			logger.info("Token de recuperación generado para usuario {}", usuario.getId());
		} catch (Exception e) {
			logger.error("No se pudo enviar correo de recuperación para usuario {}", usuario.getId(), e);
		}
		return true;
	}

	@Override
	public boolean tokenResetValido(String token) {
		if (token == null || token.isBlank()) {
			return false;
		}
		return usuarioRepo.findByResetTokenAndResetTokenExpiraAfter(token, LocalDateTime.now()).isPresent();
	}

	@Override
	@Transactional
	public void restablecerPassword(String token, String nueva, String confirmar) {
		if (token == null || token.isBlank()) {
			throw new IllegalArgumentException("Token inválido");
		}
		if (nueva == null || nueva.isBlank() || confirmar == null || confirmar.isBlank()) {
			throw new IllegalArgumentException("Completa los campos de contraseña");
		}
		if (nueva.length() < 8) {
			throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
		}
		if (!nueva.equals(confirmar)) {
			throw new IllegalArgumentException("Las contraseñas no coinciden");
		}

		Usuario usuario = usuarioRepo.findByResetTokenAndResetTokenExpiraAfter(token, LocalDateTime.now())
				.orElseThrow(() -> new IllegalArgumentException("Token inválido o expirado"));

		usuario.setPasswordHash(passwordEncoder.encode(nueva));
		// Invalida token luego de uso, conforme requerimiento.
		usuario.setResetToken(null);
		usuario.setResetTokenExpira(null);
		usuarioRepo.saveAndFlush(usuario);
		logger.info("Password restablecida para usuario {}. Token invalidado.", usuario.getId());
	}

}
