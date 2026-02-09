package com.lovepaws.app.user.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.lovepaws.app.security.UsuarioPrincipal;
import com.lovepaws.app.user.domain.EstadoUsuario;
import com.lovepaws.app.user.domain.Usuario;
import com.lovepaws.app.user.service.RolService;
import com.lovepaws.app.user.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerUnitTest {

    @Mock
    private UsuarioService usuarioService;
    @Mock
    private RolService rolService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioController controller;

    @Test
    void updatePerfil_noAdminNoPuedeEditarOtroUsuario() {
        Usuario autenticado = new Usuario();
        autenticado.setId(1);
        autenticado.setEstado(new EstadoUsuario("ACTIVO", "Activo"));

        UsuarioPrincipal principal = new UsuarioPrincipal(autenticado, List.of(new SimpleGrantedAuthority("ROLE_ADOPTANTE")));
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        String view = controller.updatePerfil(2, "Nombre Valido", "mail@demo.com", "999888777", "Calle 12345", auth);

        assertEquals("redirect:/usuarios/perfil?error=forbidden", view);
    }

    @Test
    void updatePerfil_rechazaFormatoInvalido() {
        Usuario autenticado = new Usuario();
        autenticado.setId(1);
        autenticado.setEstado(new EstadoUsuario("ACTIVO", "Activo"));

        UsuarioPrincipal principal = new UsuarioPrincipal(autenticado, List.of(new SimpleGrantedAuthority("ROLE_ADOPTANTE")));
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        Usuario target = new Usuario();
        target.setId(1);
        when(usuarioService.findUsuarioById(1)).thenReturn(Optional.of(target));

        String view = controller.updatePerfil(1, "Nombre", "correo-invalido", "123", "dir", auth);

        assertEquals("redirect:/usuarios/perfil?error=formato", view);
    }
}
