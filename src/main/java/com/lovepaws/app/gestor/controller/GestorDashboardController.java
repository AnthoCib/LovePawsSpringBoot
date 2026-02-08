package com.lovepaws.app.gestor.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lovepaws.app.adopcion.repository.SolicitudAdopcionRepository;
import com.lovepaws.app.mascota.repository.MascotaRepository;
import com.lovepaws.app.security.UsuarioPrincipal;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/gestor")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
public class GestorDashboardController {

	private final MascotaRepository mascotaRepository;
	private final SolicitudAdopcionRepository solicitudRepository;

	@GetMapping("/dashboard")
	public String dashboard(@AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
		String nombre = principal != null ? principal.getUsuario().getNombre() : "Gestor";
		String primerNombre = nombre != null && !nombre.isBlank() ? nombre.split(" ")[0] : "Gestor";

		model.addAttribute("gestorNombre", primerNombre);
		model.addAttribute("totalMascotas", mascotaRepository.count());
		model.addAttribute("mascotasDisponibles", mascotaRepository.findByEstado_Id("DISPONIBLE").size());
		model.addAttribute("solicitudesPendientes", solicitudRepository.countByEstado_Id("PENDIENTE"));

		return "gestor/dashboard";
	}
}
