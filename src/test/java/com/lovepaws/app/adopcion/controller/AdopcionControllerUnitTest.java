package com.lovepaws.app.adopcion.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.validation.BeanPropertyBindingResult;

import com.lovepaws.app.adopcion.domain.SolicitudAdopcion;
import com.lovepaws.app.adopcion.service.AdopcionService;
import com.lovepaws.app.adopcion.service.SolicitudAdopcionService;
import com.lovepaws.app.mascota.domain.EstadoMascota;
import com.lovepaws.app.mascota.domain.Mascota;
import com.lovepaws.app.mascota.service.MascotaService;
import com.lovepaws.app.security.UsuarioPrincipal;
import com.lovepaws.app.user.domain.EstadoUsuario;
import com.lovepaws.app.user.domain.Usuario;

@ExtendWith(MockitoExtension.class)
class AdopcionControllerUnitTest {

    @Mock
    private SolicitudAdopcionService solicitudService;
    @Mock
    private AdopcionService adopcionService;
    @Mock
    private MascotaService mascotaService;

    @InjectMocks
    private AdopcionController controller;

    @Test
    void solicitarAdopcion_creaSolicitudCuandoDatosValidos() {
        Mascota mascota = new Mascota();
        mascota.setId(10);
        mascota.setEstado(new EstadoMascota("DISPONIBLE", "Disponible"));

        SolicitudAdopcion solicitud = new SolicitudAdopcion();
        solicitud.setMascota(mascota);
        solicitud.setPqAdoptar("Porque tengo espacio y tiempo.");

        Usuario usuario = new Usuario();
        usuario.setId(20);
        usuario.setNombre("Ana Perez");
        usuario.setCorreo("ana@mail.com");
        usuario.setTelefono("999888777");
        usuario.setDireccion("Av. Central 123");
        usuario.setEstado(new EstadoUsuario("ACTIVO", "Activo"));

        UsuarioPrincipal principal = new UsuarioPrincipal(
                usuario,
                List.of(new SimpleGrantedAuthority("ROLE_ADOPTANTE"))
        );
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(mascotaService.findMascotaById(10)).thenReturn(Optional.of(mascota));
        when(solicitudService.listarSolicitudesPorMascota(10)).thenReturn(List.of());
        when(solicitudService.listarSolicitudesPorUsuario(20)).thenReturn(List.of());

        String view = controller.solicitarAdopcion(solicitud, new BeanPropertyBindingResult(solicitud, "solicitud"), auth);

        assertEquals("redirect:/adopcion/mis-adopciones?created", view);

        ArgumentCaptor<SolicitudAdopcion> captor = ArgumentCaptor.forClass(SolicitudAdopcion.class);
        verify(solicitudService).createSolicitud(captor.capture());
        assertEquals("PENDIENTE", captor.getValue().getEstado().getId());
        assertEquals(20, captor.getValue().getUsuario().getId());
        assertEquals(10, captor.getValue().getMascota().getId());
    }

    @Test
    void aprobarSolicitud_redirigeASolicitudesDeMascota() {
        Usuario gestor = new Usuario();
        gestor.setId(7);
        gestor.setEstado(new EstadoUsuario("ACTIVO", "Activo"));
        UsuarioPrincipal principal = new UsuarioPrincipal(gestor, List.of(new SimpleGrantedAuthority("ROLE_GESTOR")));
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        Mascota mascota = new Mascota();
        mascota.setId(5);
        SolicitudAdopcion solicitud = new SolicitudAdopcion();
        solicitud.setMascota(mascota);

        when(solicitudService.findSolicitudById(11)).thenReturn(Optional.of(solicitud));

        String view = controller.aprobarSolicitud(11, auth);

        assertEquals("redirect:/adopcion/gestor/solicitudes/5?aprobada", view);
        verify(adopcionService).aprobarSolicitud(11, 7);
    }

    @Test
    void rechazarSolicitud_redirigeAGlobalCuandoNoHayMascota() {
        Usuario gestor = new Usuario();
        gestor.setId(7);
        gestor.setEstado(new EstadoUsuario("ACTIVO", "Activo"));
        UsuarioPrincipal principal = new UsuarioPrincipal(gestor, List.of(new SimpleGrantedAuthority("ROLE_GESTOR")));
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(solicitudService.findSolicitudById(11)).thenReturn(Optional.empty());

        String view = controller.rechazarSolicitud(11, "No cumple criterios", auth);

        assertEquals("redirect:/adopcion/gestor/solicitudes?rechazada", view);
        verify(solicitudService).rechazarSolicitud(11, 7, "No cumple criterios");
    }
}
