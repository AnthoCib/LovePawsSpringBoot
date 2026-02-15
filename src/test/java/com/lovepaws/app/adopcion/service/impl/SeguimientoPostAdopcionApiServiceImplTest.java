package com.lovepaws.app.adopcion.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.lovepaws.app.adopcion.domain.Adopcion;
import com.lovepaws.app.adopcion.domain.EstadoAdopcion;
import com.lovepaws.app.adopcion.domain.SeguimientoAdopcion;
import com.lovepaws.app.adopcion.dto.EstadoMascotaTracking;
import com.lovepaws.app.adopcion.dto.SeguimientoPostAdopcionRequestDTO;
import com.lovepaws.app.adopcion.mapper.SeguimientoPostAdopcionMapper;
import com.lovepaws.app.adopcion.repository.AdopcionRepository;
import com.lovepaws.app.adopcion.repository.SeguimientoAdopcionRepository;
import com.lovepaws.app.user.service.AuditoriaService;
import com.lovepaws.app.mascota.domain.EstadoMascota;
import com.lovepaws.app.user.domain.Usuario;

@ExtendWith(MockitoExtension.class)
class SeguimientoPostAdopcionApiServiceImplTest {

    @Mock
    private SeguimientoAdopcionRepository seguimientoRepository;
    @Mock
    private AuditoriaService auditoriaService;
    @Mock
    private AdopcionRepository adopcionRepository;

    private SeguimientoPostAdopcionApiServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new SeguimientoPostAdopcionApiServiceImpl(
                seguimientoRepository,
                adopcionRepository,
                new SeguimientoPostAdopcionMapper(),
                auditoriaService);
    }

    @Test
    void crearSeguimiento_conAdopcionAprobada_guardaSeguimiento() {
        SeguimientoPostAdopcionRequestDTO request = new SeguimientoPostAdopcionRequestDTO();
        request.setAdopcionId(15);
        request.setFechaSeguimiento(LocalDateTime.of(2026, 2, 12, 10, 0));
        request.setNotas("  Mascota estable  ");
        request.setEstadoMascota(EstadoMascotaTracking.BIEN);

        Adopcion adopcion = adopcionAprobada(15);

        when(adopcionRepository.findByIdWithRelationsAndActivoTrue(15)).thenReturn(Optional.of(adopcion));
        when(seguimientoRepository.save(any(SeguimientoAdopcion.class))).thenAnswer(invocation -> {
            SeguimientoAdopcion seguimiento = invocation.getArgument(0);
            seguimiento.setId(300);
            seguimiento.setAdopcion(adopcion);
            return seguimiento;
        });

        var response = service.crearSeguimiento(request, 99);

        ArgumentCaptor<SeguimientoAdopcion> captor = ArgumentCaptor.forClass(SeguimientoAdopcion.class);
        verify(seguimientoRepository).save(captor.capture());
        assertEquals("Mascota estable", captor.getValue().getObservaciones());
        assertEquals("BIEN", captor.getValue().getEstado().getId());
        assertEquals(99, captor.getValue().getUsuarioCreacion().getId());

        assertEquals(300, response.getId());
        assertEquals(15, response.getAdopcionId());
        assertEquals("APROBADA", response.getEstadoProcesoAdopcion());
        assertEquals(EstadoMascotaTracking.BIEN, response.getEstadoMascota());
    }

    @Test
    void crearSeguimiento_conAdopcionNoAprobada_retornaConflict() {
        SeguimientoPostAdopcionRequestDTO request = new SeguimientoPostAdopcionRequestDTO();
        request.setAdopcionId(16);
        request.setFechaSeguimiento(LocalDateTime.now());
        request.setEstadoMascota(EstadoMascotaTracking.RETORNADO);

        Adopcion adopcion = adopcionAprobada(16);
        EstadoAdopcion estado = new EstadoAdopcion();
        estado.setId("PENDIENTE");
        adopcion.setEstado(estado);

        when(adopcionRepository.findByIdWithRelationsAndActivoTrue(16)).thenReturn(Optional.of(adopcion));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.crearSeguimiento(request, 1));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void listarSeguimientos_filtraPorEstadoMascota() {
        SeguimientoAdopcion seguimiento = new SeguimientoAdopcion();
        seguimiento.setId(401);
        seguimiento.setFechaVisita(LocalDateTime.of(2026, 2, 15, 12, 0));
        seguimiento.setAdopcion(adopcionAprobada(18));
        EstadoMascota estadoMascota = new EstadoMascota();
        estadoMascota.setId("ATENCION_VETERINARIA");
        seguimiento.setEstado(estadoMascota);
        Usuario gestor = new Usuario();
        gestor.setId(11);
        seguimiento.setUsuarioCreacion(gestor);

        when(seguimientoRepository.findAllByFiltros("ATENCION_VETERINARIA", null))
                .thenReturn(List.of(seguimiento));

        var response = service.listarSeguimientos(EstadoMascotaTracking.ATENCION_VETERINARIA, null);

        assertEquals(1, response.size());
        assertEquals(EstadoMascotaTracking.ATENCION_VETERINARIA, response.get(0).getEstadoMascota());
        assertEquals("APROBADA", response.get(0).getEstadoProcesoAdopcion());
    }

    @Test
    void actualizarSeguimiento_conNotasEnBlanco_persisteNull() {
        SeguimientoPostAdopcionRequestDTO request = new SeguimientoPostAdopcionRequestDTO();
        request.setAdopcionId(21);
        request.setFechaSeguimiento(LocalDateTime.of(2026, 2, 20, 8, 0));
        request.setNotas("   ");
        request.setEstadoMascota(EstadoMascotaTracking.BIEN);

        SeguimientoAdopcion existente = new SeguimientoAdopcion();
        existente.setId(701);

        when(seguimientoRepository.findByIdAndDeletedAtIsNull(701)).thenReturn(Optional.of(existente));
        when(adopcionRepository.findByIdWithRelationsAndActivoTrue(21)).thenReturn(Optional.of(adopcionAprobada(21)));
        when(seguimientoRepository.save(any(SeguimientoAdopcion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.actualizarSeguimiento(701, request, 10);

        assertNull(response.getNotas());
    }

    private Adopcion adopcionAprobada(Integer id) {
        Adopcion adopcion = new Adopcion();
        adopcion.setId(id);
        EstadoAdopcion estado = new EstadoAdopcion();
        estado.setId("APROBADA");
        adopcion.setEstado(estado);
        adopcion.setActivo(true);
        Usuario adoptante = new Usuario();
        adoptante.setId(55);
        adopcion.setUsuarioAdoptante(adoptante);
        return adopcion;
    }
}
