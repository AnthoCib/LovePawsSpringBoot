package com.lovepaws.app.adopcion.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import com.lovepaws.app.adopcion.domain.SolicitudAdopcion;
import com.lovepaws.app.adopcion.service.AdopcionService;
import com.lovepaws.app.adopcion.service.SolicitudAdopcionService;
import com.lovepaws.app.config.SecurityConfig;
import com.lovepaws.app.mascota.domain.Mascota;
import com.lovepaws.app.mascota.service.MascotaService;
import com.lovepaws.app.security.CustomSuccessHandler;
import com.lovepaws.app.security.CustomUserDetailsService;
import com.lovepaws.app.security.UsuarioPrincipal;
import com.lovepaws.app.user.domain.EstadoUsuario;
import com.lovepaws.app.user.domain.Usuario;

@WebMvcTest(controllers = AdopcionController.class)
@Import(SecurityConfig.class)
class AdopcionControllerSecurityIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SolicitudAdopcionService solicitudService;
    @MockBean
    private AdopcionService adopcionService;
    @MockBean
    private MascotaService mascotaService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @MockBean
    private CustomSuccessHandler customSuccessHandler;

    @Test
    void gestorPuedeAprobarSolicitud() throws Exception {
        Mascota mascota = new Mascota();
        mascota.setId(15);
        SolicitudAdopcion solicitud = new SolicitudAdopcion();
        solicitud.setMascota(mascota);
        when(solicitudService.findSolicitudById(5)).thenReturn(Optional.of(solicitud));

        mockMvc.perform(post("/adopcion/gestor/aprobar/5")
                        .with(csrf())
                        .with(authentication(auth(9, "GESTOR"))))
                .andExpect(status().is3xxRedirection());

        verify(adopcionService).aprobarSolicitud(5, 9);
    }

    @Test
    void adoptanteNoPuedeAprobarSolicitud() throws Exception {
        mockMvc.perform(post("/adopcion/gestor/aprobar/5")
                        .with(csrf())
                        .with(authentication(auth(20, "ADOPTANTE"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void gestorPuedeRechazarSolicitud() throws Exception {
        when(solicitudService.findSolicitudById(7)).thenReturn(Optional.empty());

        mockMvc.perform(post("/adopcion/gestor/rechazar/7")
                        .param("motivo", "No cumple")
                        .with(csrf())
                        .with(authentication(auth(9, "GESTOR"))))
                .andExpect(status().is3xxRedirection());

        verify(solicitudService).rechazarSolicitud(7, 9, "No cumple");
    }

    @Test
    void gestorNoPuedeSolicitarAdopcion() throws Exception {
        mockMvc.perform(post("/adopcion/solicitar")
                        .with(csrf())
                        .with(authentication(auth(9, "GESTOR"))))
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
