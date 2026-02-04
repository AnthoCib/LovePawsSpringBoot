package com.lovepaws.app.user.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lovepaws.app.user.domain.EstadoUsuario;
import com.lovepaws.app.user.domain.Rol;
import com.lovepaws.app.user.domain.Usuario;
import com.lovepaws.app.user.service.RolService;
import com.lovepaws.app.user.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final PasswordEncoder passwordEncoder;

    /* =========================
       REGISTRO DE USUARIO
       ========================= */
    @GetMapping("/registro")
    public String showRegistroForm(Model model, Authentication auth) {
        model.addAttribute("usuario", new Usuario());
        cargarRolesSegunUsuario(model, auth);
        return "usuario/registro";
    }

    @PostMapping("/registro")
    public String registerUsuario(@Valid @ModelAttribute("usuario") Usuario usuario,
                                  BindingResult br,
                                  Model model,
                                  Authentication auth) { 	
    	
    	System.out.println("POST /usuarios/registro llamado");
    	System.out.println("Usuario recibido: " + usuario);
    	System.out.println("BindingResult: " + br);

    	
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Validaciones de formulario
        if (br.hasErrors()) {
            cargarRolesSegunUsuario(model, auth);
            model.addAttribute("isAdmin", isAdmin);
            return "usuario/registro";
        }

        // Validación de unicidad
        if (usuarioService.findByUsername(usuario.getUsername()).isPresent()) {
            model.addAttribute("errorUsername", "El username ya está en uso");
            cargarRolesSegunUsuario(model, auth);
            return "usuario/registro";
        }
        if (usuarioService.findByCorreo(usuario.getCorreo()).isPresent()) {
            model.addAttribute("errorCorreo", "El correo ya está registrado");
            cargarRolesSegunUsuario(model, auth);
            return "usuario/registro";
        }

        // Asignar rol automáticamente si no es admin
        if (!isAdmin) {
            Rol adoptante = rolService.listarRoles().stream()
                    .filter(r -> "ADOPTANTE".equalsIgnoreCase(r.getNombre()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Rol ADOPTANTE no existe"));
            usuario.setRol(adoptante);
        }
        // si es admin, el rol ya viene del formulario 

        // Estado por defecto
        
        if (usuario.getEstado() == null) {
            EstadoUsuario estado = new EstadoUsuario();
            estado.setId("ACTIVO");
            usuario.setEstado(estado);
        }
        // Password
        usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));

        usuarioService.createUsuario(usuario);

        // Redirección según tipo de usuario
        // Redirige a login para nuevos adoptantes
        return isAdmin 
        	    ? "redirect:/admin/dashboard?registro=ok"
        	    : "redirect:/login?registro=ok";
 
        
    }

    /* =========================
       LOGIN
       ========================= */
    @GetMapping("/login")
    public String loginForm() {
        return "usuario/login"; // tu template login.html
    }

    /* =========================
       PERFIL
       ========================= */
    @GetMapping("/perfil")
    public String verPerfil(Model model, Integer id) {
        if (id != null) {
            usuarioService.findUsuarioById(id).ifPresent(u -> model.addAttribute("usuario", u));
        } else {
            model.addAttribute("usuario", new Usuario());
        }
        return "usuario/perfil";
    }

    @PostMapping("/perfil")
    public String updatePerfil(@Valid @ModelAttribute("usuario") Usuario usuario,
                               BindingResult br) {
        if (br.hasErrors())
            return "usuario/perfil";

        usuarioService.updateUsuario(usuario);
        return "redirect:/usuarios/perfil?updated";
    }


    /* =========================
       MÉTODOS AUXILIARES
       ========================= */
    private void cargarRolesSegunUsuario(Model model, Authentication auth) {
    	
    	boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("isAdmin", isAdmin);

        List<Rol> roles;

        if (isAdmin) {
            roles = rolService.listarRoles().stream()
                    .filter(r ->
                            r.getNombre().equalsIgnoreCase("ADMIN") ||
                            r.getNombre().equalsIgnoreCase("GESTOR")
                    )
                    .toList();
        } else {
            roles = List.of(); 
        }

        model.addAttribute("roles", roles);
    }
}
