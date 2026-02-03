package com.lovepaws.app.admin.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import com.lovepaws.app.user.domain.Rol;
import com.lovepaws.app.user.domain.Usuario;
import com.lovepaws.app.user.service.RolService;
import com.lovepaws.app.user.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUsuarioController {

	private final UsuarioService usuarioService;
	private final RolService rolService;

	// Listar usuarios
	@GetMapping
	public String listarUsuarios(Model model) {
		List<Usuario> usuarios = usuarioService.listarUsuarios();
		List<Rol> roles = rolService.listarRoles();

		model.addAttribute("usuarios", usuarios);
		model.addAttribute("roles", roles);

		return "admin/usuarios";
	}

	// CAMBIAR ESTADO (ACTIVO / BLOQUEADO)
	@PostMapping("/{id}/estado")
	public String cambiarEstado(@PathVariable Integer id) {
		usuarioService.cambiarEstado(id);
		return "redirect:/admin/usuarios";
	}

	// Cambiar rol
	@PostMapping("/{id}/rol")
	public String cambiarRol(@PathVariable Integer id, @RequestParam Integer rolId) {

		usuarioService.cambiarRol(id, rolId);
		return "redirect:/admin/usuarios";
	}
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/nuevo")
	public String nuevoUsuario(Model model) {

	    model.addAttribute("usuario", new Usuario());
	    model.addAttribute("roles", rolService.listarRoles());
	    model.addAttribute("isAdmin", true);

	    return "admin/usuarios/crear";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/admin/usuarios")
	public String guardarUsuarioAdmin(
	        @ModelAttribute Usuario usuario) {

	    usuarioService.crearUsuarioDesdeAdmin(usuario);
	    return "redirect:/admin/usuarios?creado=true";
	}


}
