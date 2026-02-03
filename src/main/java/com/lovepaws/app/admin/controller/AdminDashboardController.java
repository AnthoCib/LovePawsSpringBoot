package com.lovepaws.app.admin.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.lovepaws.app.admin.service.AdminDashboardService;
import com.lovepaws.app.security.UsuarioPrincipal;
import com.lovepaws.app.user.domain.Usuario;
import com.lovepaws.app.user.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@PreAuthorize("hasRole('ADMIN')")

public class AdminDashboardController {

	private final AdminDashboardService dashboardService;
	private final UsuarioService usuarioService;

	@GetMapping("/admin/dashboard")
	public String dashboard(@AuthenticationPrincipal UsuarioPrincipal principal, Model model) {

		String nombre = principal.getUsuario().getNombre();
		String primerNombre = nombre.split(" ")[0];

		model.addAttribute("adminNombre", primerNombre);
		model.addAttribute("totalUsuarios", dashboardService.totalUsuarios());
		model.addAttribute("totalMascotas", dashboardService.totalMascotas());
		model.addAttribute("adopcionesPendientes", dashboardService.adopcionesPendientes());
		model.addAttribute("solicitudesPendientes", dashboardService.solicitudesPendientes());

		return "admin/dashboard";
	}
	@GetMapping("/admin/usuarios/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public String verUsuario(@PathVariable Integer id, Model model) {

	    Usuario usuario = usuarioService.findUsuarioById(id)
	            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

	    model.addAttribute("usuario", usuario);
	    return "admin/usuario-detalle";
	}

}
