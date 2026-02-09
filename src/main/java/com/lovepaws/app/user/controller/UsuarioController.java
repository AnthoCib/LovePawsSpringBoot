package com.lovepaws.app.user.controller;

import java.security.SecureRandom;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lovepaws.app.security.UsuarioPrincipal;
import com.lovepaws.app.mail.EmailService;
import com.lovepaws.app.user.domain.EstadoUsuario;
import com.lovepaws.app.user.domain.Rol;
import com.lovepaws.app.user.domain.Usuario;
import com.lovepaws.app.user.service.RolService;
import com.lovepaws.app.user.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Slf4j
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private static final String TEMP_PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";

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
        	    : "redirect:/usuarios/login?registro=ok";
 
        
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
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/perfil")
    public String verPerfil(Model model, Integer id, Authentication auth) {
        if (!(auth != null && auth.getPrincipal() instanceof UsuarioPrincipal principal)) {
            return "redirect:/usuarios/login";
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Integer targetId = (id != null && isAdmin) ? id : principal.getUsuario().getId();

        usuarioService.findUsuarioById(targetId)
                .ifPresentOrElse(
                        u -> model.addAttribute("usuario", u),
                        () -> model.addAttribute("usuario", new Usuario())
                );

        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isOwnProfile", targetId.equals(principal.getUsuario().getId()));
        return "usuario/perfil";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/perfil")
    public String updatePerfil(@RequestParam Integer id,
                               @RequestParam String nombre,
                               @RequestParam String correo,
                               @RequestParam String telefono,
                               @RequestParam String direccion,
                               @RequestParam(required = false) String fotoUrl,
                               Authentication auth) {

        if (!(auth != null && auth.getPrincipal() instanceof UsuarioPrincipal principal)) {
            return "redirect:/usuarios/login";
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Integer usuarioAutenticadoId = principal.getUsuario().getId();
        if (!isAdmin && !id.equals(usuarioAutenticadoId)) {
            return "redirect:/usuarios/perfil?error=forbidden";
        }

        Usuario usuario = usuarioService.findUsuarioById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean datosValidos = nombre != null && nombre.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ\\s]{2,80}$")
                && correo != null && correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
                && telefono != null && telefono.matches("^\\+?[0-9]{9,15}$")
                && direccion != null && direccion.length() >= 5 && direccion.length() <= 120;

        if (!datosValidos) {
            if (isAdmin && !id.equals(usuarioAutenticadoId)) {
                return "redirect:/usuarios/perfil?id=" + id + "&error=formato";
            }
            return "redirect:/usuarios/perfil?error=formato";
        }

        if (usuarioService.findByCorreo(correo)
                .filter(existente -> !existente.getId().equals(id))
                .isPresent()) {
            if (isAdmin && !id.equals(usuarioAutenticadoId)) {
                return "redirect:/usuarios/perfil?id=" + id + "&error=correo";
            }
            return "redirect:/usuarios/perfil?error=correo";
        }

        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setTelefono(telefono);
        usuario.setDireccion(direccion);
        usuario.setFotoUrl((fotoUrl != null && !fotoUrl.isBlank()) ? fotoUrl : null);

        usuarioService.updateUsuario(usuario);
        if (isAdmin && !id.equals(usuarioAutenticadoId)) {
            return "redirect:/usuarios/perfil?id=" + id + "&updated";
        }
        return "redirect:/usuarios/perfil?updated";
    }


    /* =========================
       CAMBIAR CONTRASEÑA
       ========================= */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/cambiar-password")
    public String cambiarPasswordForm() {
        return "usuario/cambiar-password";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/cambiar-password")
    public String cambiarPassword(@RequestParam String actual,
                                  @RequestParam String nueva,
                                  @RequestParam String confirmar,
                                  Authentication auth) {

        if (actual == null || actual.isBlank() || nueva == null || nueva.isBlank() || confirmar == null || confirmar.isBlank()) {
            return "redirect:/usuarios/cambiar-password?error=campos";
        }

        if (nueva.length() < 8) {
            return "redirect:/usuarios/cambiar-password?error=min";
        }

        if (!nueva.equals(confirmar)) {
            return "redirect:/usuarios/cambiar-password?error=match";
        }

        UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
        Usuario usuario = usuarioService.findUsuarioById(principal.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(actual, usuario.getPasswordHash())) {
            return "redirect:/usuarios/cambiar-password?error=actual";
        }

        usuario.setPasswordHash(passwordEncoder.encode(nueva));
        usuarioService.updateUsuario(usuario);

        return "redirect:/usuarios/cambiar-password?updated=true";
    }

    /* =========================
       RECUPERAR CONTRASEÑA
       ========================= */
    @GetMapping("/recuperar-password")
    public String recuperarPasswordForm() {
        return "usuario/recuperar-password";
    }

    @PostMapping("/recuperar-password")
    public String recuperarPassword(@RequestParam String correo) {
        if (correo == null || !correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return "redirect:/usuarios/recuperar-password?error=formato";
        }

        usuarioService.findByCorreo(correo).ifPresent(usuario -> {
            String tempPassword = generarPasswordTemporal(10);
            usuario.setPasswordHash(passwordEncoder.encode(tempPassword));
            usuarioService.updateUsuario(usuario);

            String asunto = "Recuperación de contraseña - LovePaws";
            String contenido = "<p>Hola " + usuario.getNombre() + ",</p>"
                    + "<p>Generamos una contraseña temporal para que puedas acceder a tu cuenta:</p>"
                    + "<p><strong>" + tempPassword + "</strong></p>"
                    + "<p>Te recomendamos iniciar sesión y cambiarla desde el menú de tu perfil.</p>"
                    + "<p>Equipo LovePaws</p>";

            emailService.enviarCorreo(usuario.getCorreo(), asunto, contenido);
        });

        return "redirect:/usuarios/recuperar-password?sent";
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

    private String generarPasswordTemporal(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = random.nextInt(TEMP_PASSWORD_CHARS.length());
            builder.append(TEMP_PASSWORD_CHARS.charAt(idx));
        }
        return builder.toString();
    }
}
