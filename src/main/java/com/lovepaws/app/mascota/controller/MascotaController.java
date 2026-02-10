package com.lovepaws.app.mascota.controller;

import com.lovepaws.app.config.storage.FileStorageService;
import com.lovepaws.app.mascota.domain.Mascota;
import com.lovepaws.app.mascota.repository.CategoriaRepository;
import com.lovepaws.app.mascota.repository.RazaRepository;
import com.lovepaws.app.mascota.service.MascotaService;
import com.lovepaws.app.security.UsuarioPrincipal;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class MascotaController {

	private final MascotaService mascotaService;
	private final FileStorageService fileStorageService;
	private final CategoriaRepository categoriaRepository;
	private final RazaRepository razaRepository;

	@GetMapping("/mascotas")
	public String listarCatalogo(@RequestParam(required = false) Integer categoriaId,
							 @RequestParam(required = false) Integer razaId,
							 @RequestParam(required = false) Integer edadMax,
							 @RequestParam(required = false) String q,
							 Authentication authentication,
							 Model model) {
		model.addAttribute("mascotas", mascotaService.buscarMascotasDisponibles(categoriaId, razaId, edadMax, q));
		model.addAttribute("categorias", categoriaRepository.findAll());
		model.addAttribute("razas", razaRepository.findAll());
		model.addAttribute("categoriaId", categoriaId);
		model.addAttribute("razaId", razaId);
		model.addAttribute("edadMax", edadMax);
		model.addAttribute("q", q);

		boolean autenticado = authentication != null && authentication.isAuthenticated()
				&& authentication.getAuthorities().stream().noneMatch(a -> "ROLE_ANONYMOUS".equals(a.getAuthority()));
		boolean esGestorOAdmin = autenticado && authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.anyMatch(r -> "ROLE_GESTOR".equals(r) || "ROLE_ADMIN".equals(r));
		model.addAttribute("esGestorOAdmin", esGestorOAdmin);
		model.addAttribute("esAutenticado", autenticado);

		return "mascota/lista";
	}

	@GetMapping("/mascotas/{id}")
	public String detalleMascota(@PathVariable Integer id, Model model) {
		Mascota mascota = mascotaService.findMascotaById(id)
				.orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
		model.addAttribute("mascota", mascota);
		return "mascota/detalle";
	}

	@PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
	@GetMapping("/gestor/mascotas")
	public String listarParaGestor(Model model) {
		model.addAttribute("mascotas", mascotaService.listarMascotas());
		return "gestor/mascota/lista";
	}

	@PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
	@GetMapping("/gestor/mascotas/nuevo")
	public String showCreateForm(Model model) {
		model.addAttribute("mascota", new Mascota());
		cargarCatalogos(model);
		return "gestor/mascota/nuevo";
	}

	@PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
	@PostMapping("/gestor/mascotas/guardar")
	public String createMascota(@Validated @ModelAttribute("mascota") Mascota mascota,
							BindingResult br,
							@RequestParam(value = "foto", required = false) MultipartFile foto,
							Authentication authentication,
							Model model) {

		if (br.hasErrors()) {
			cargarCatalogos(model);
			return "gestor/mascota/nuevo";
		}

		try {
			if (foto != null && !foto.isEmpty()) {
				String url = fileStorageService.store(foto);
				mascota.setFotoUrl(url);
			}
		} catch (RuntimeException ex) {
			model.addAttribute("fileError", ex.getMessage());
			cargarCatalogos(model);
			return "gestor/mascota/nuevo";
		}

		if (authentication != null && authentication.getPrincipal() instanceof UsuarioPrincipal up) {
			mascota.setUsuarioCreacion(up.getUsuario());
		}

		mascotaService.createMascota(mascota);
		return "redirect:/gestor/mascotas?created";
	}

	@PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
	@PostMapping("/gestor/mascotas/actualizar")
	public String updateMascota(@Validated @ModelAttribute("mascota") Mascota mascota,
							BindingResult br,
							@RequestParam(value = "foto", required = false) MultipartFile foto,
							Model model) {
		if (br.hasErrors()) {
			cargarCatalogos(model);
			return "gestor/mascota/form";
		}

		try {
			if (foto != null && !foto.isEmpty()) {
				String url = fileStorageService.store(foto);
				mascota.setFotoUrl(url);
			}
		} catch (RuntimeException ex) {
			model.addAttribute("fileError", ex.getMessage());
			cargarCatalogos(model);
			return "gestor/mascota/form";
		}

		mascotaService.updateMascota(mascota);
		return "redirect:/gestor/mascotas?updated";
	}

	@PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
	@GetMapping("/gestor/mascotas/editar/{id}")
	public String editarMascota(@PathVariable Integer id, Authentication auth, Model model) {
		Mascota mascota = mascotaService.findMascotaById(id)
				.orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

		model.addAttribute("mascota", mascota);
		cargarCatalogos(model);
		return "gestor/mascota/form";
	}

	@PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
	@PostMapping("/gestor/mascotas/eliminar/{id}")
	public String deleteMascota(@PathVariable Integer id) {
		mascotaService.deleteMascotaById(id);
		return "redirect:/gestor/mascotas?deleted";
	}

	private void cargarCatalogos(Model model) {
		model.addAttribute("categorias", categoriaRepository.findAll());
		model.addAttribute("razas", razaRepository.findAll());
	}
}
