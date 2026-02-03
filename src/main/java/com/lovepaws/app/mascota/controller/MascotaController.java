package com.lovepaws.app.mascota.controller;

import com.lovepaws.app.mascota.domain.Mascota;
import com.lovepaws.app.mascota.service.MascotaService;
import com.lovepaws.app.config.storage.FileStorageService;
import com.lovepaws.app.security.UsuarioPrincipal; 
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class MascotaController {

	private final MascotaService mascotaService;
	private final FileStorageService fileStorageService;

	// PUBLIC
	@GetMapping("/mascotas")
	public String listarCatalogo(Model model) {
		model.addAttribute("mascotas", mascotaService.listarMascotasDisponibles());
		return "mascota/lista";
	}

	@GetMapping("/mascotas/{id}")
	public String detalleMascota(@PathVariable Integer id, Model model) {
		mascotaService.findMascotaById(id).ifPresent(m -> model.addAttribute("mascota", m));
		return "mascota/detalle";
	}

	// GESTOR/ADMIN LIST
	@PreAuthorize("hasAnyRole('GESTOR')")
	@GetMapping("/gestor/mascotas")
	public String listarParaGestor(Model model) {
		model.addAttribute("mascotas", mascotaService.listarMascotas());
		return "gestor/mascotas/lista";
	}

	// NEW FORM
	@PreAuthorize("hasAnyRole('GESTOR')")
	@GetMapping("/gestor/mascotas/nuevo")
	public String showCreateForm(Model model) {
		model.addAttribute("mascota", new Mascota());
		return "gestor/mascotas/form";
	}

	// CREATE
	@PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
	@PostMapping("/gestor/mascotas/guardar")
	public String createMascota(@Validated @ModelAttribute("mascota") Mascota mascota, BindingResult br,
			@RequestParam(value = "foto", required = false) MultipartFile foto, Authentication authentication,
			Model model) {

		if (br.hasErrors()) {
			return "gestor/mascotas/form";
		}

		try {
			if (foto != null && !foto.isEmpty()) {
				String url = fileStorageService.store(foto);
				mascota.setFotoUrl(url);
			}
		} catch (RuntimeException ex) {
			// pasar mensaje básico de error al cliente (podrías mejorarlo)
			model.addAttribute("fileError", ex.getMessage());
			return "gestor/mascotas/form";
		}

		// set usuario creador si el principal lo expone
		if (authentication != null && authentication.getPrincipal() instanceof UsuarioPrincipal up) {
			try {
				mascota.setUsuarioCreacion(up.getUsuario());
			} catch (Exception ignored) {
			}
		}

		mascotaService.createMascota(mascota);
		return "redirect:/gestor/mascotas?created";
	}

	// UPDATE
	@PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
	@PostMapping("/gestor/mascotas/actualizar")
	public String updateMascota(@Validated @ModelAttribute("mascota") Mascota mascota,
								BindingResult br,
								@RequestParam(value = "foto", required = false) MultipartFile foto,
								Authentication auth,
								Model model) {
		if (br.hasErrors()) {
			return "gestor/mascotas/form";
		}

		try {
			if (foto != null && !foto.isEmpty()) {
				String url = fileStorageService.store(foto);
				mascota.setFotoUrl(url);
			}
		} catch (RuntimeException ex) {
			model.addAttribute("fileError", ex.getMessage());
			return "gestor/mascotas/form";
		}

		mascotaService.updateMascota(mascota);
		return "redirect:/gestor/mascotas?updated";
	}

	@PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
	@GetMapping("/gestor/mascotas/editar/{id}")
	public String editarMascota(@PathVariable Integer id, Authentication auth, Model model) {

		Mascota mascota = mascotaService.findMascotaById(id)
				.orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

		UsuarioPrincipal up = (UsuarioPrincipal) auth.getPrincipal();

		model.addAttribute("mascota", mascota);
		if (!mascota.getUsuarioCreacion().getId().equals(up.getUsuario().getId())) {
			throw new AccessDeniedException("No puedes editar esta mascota");
		}

		return "gestor/mascotas/form";
	}

	// DELETE
	@PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
	@PostMapping("/gestor/mascotas/eliminar/{id}")
	public String deleteMascota(@PathVariable Integer id) {
		mascotaService.deleteMascotaById(id);
		return "redirect:/gestor/mascotas?deleted";
	}

}
