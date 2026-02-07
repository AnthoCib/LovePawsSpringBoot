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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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

	
	@GetMapping
	public String listarUsuarios(Model model) {
		List<Usuario> usuarios = usuarioService.listarUsuarios();
		List<Rol> roles = rolService.listarRoles();

		model.addAttribute("usuarios", usuarios);
		model.addAttribute("roles",
			    rolService.listarRoles().stream()
			        .filter(r ->
			            r.getNombre().equalsIgnoreCase("ADMIN") ||
			            r.getNombre().equalsIgnoreCase("GESTOR")
			        )
			        .toList()
			);

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
	public String nuevoUsuarioAdmin(Model model) {

		Usuario usuario = new Usuario();
	    usuario.setRol(new Rol());
	    
	    model.addAttribute("usuario", new Usuario());
	    model.addAttribute("isAdmin", true);
	    
	    List<Rol> roles = rolService.listarRoles().stream()
	            .filter(r ->
	                    r.getNombre().equalsIgnoreCase("ADMIN") ||
	                    r.getNombre().equalsIgnoreCase("GESTOR")
	            )
	            .toList();
	    model.addAttribute("roles", roles);

	    return "admin/crear";
	}
	
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/nuevo")
	public String crearUsuarioDesdeAdmin(
	        @ModelAttribute Usuario usuario,
	        RedirectAttributes ra
	) {
	    usuarioService.crearUsuarioDesdeAdmin(usuario);
	    ra.addFlashAttribute("exito", "Usuario creado correctamente");
	    return "redirect:/admin/usuarios";
	}



}
