package com.lovepaws.app.user.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.lovepaws.app.config.SecurityConfig;
import com.lovepaws.app.security.CustomSuccessHandler;
import com.lovepaws.app.security.CustomUserDetailsService;
import com.lovepaws.app.security.UsuarioPrincipal;
import com.lovepaws.app.user.domain.EstadoUsuario;
import com.lovepaws.app.user.domain.Usuario;
import com.lovepaws.app.user.service.RolService;
import com.lovepaws.app.user.service.UsuarioService;

@WebMvcTest(controllers = UsuarioController.class)
@Import(SecurityConfig.class)
class UsuarioControllerSecurityIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;
    @MockBean
    private RolService rolService;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @MockBean
    private CustomSuccessHandler customSuccessHandler;

    @Test
    void adoptantePuedeVerCambiarPassword() throws Exception {
        mockMvc.perform(get("/usuarios/cambiar-password")
                        .with(authentication(auth(1, "ADOPTANTE"))))
                .andExpect(status().isOk());
    }

    @Test
    void gestorNoPuedeVerCambiarPassword() throws Exception {
        mockMvc.perform(get("/usuarios/cambiar-password")
                        .with(authentication(auth(2, "GESTOR"))))
                .andExpect(status().isForbidden());
    }

    private Authentication auth(Integer userId, String role) {
        Usuario usuario = new Usuario();
        usuario.setId(userId);
        usuario.setEstado(new EstadoUsuario("ACTIVO", "Activo"));
        UsuarioPrincipal principal = new UsuarioPrincipal(
                usuario,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }
}
