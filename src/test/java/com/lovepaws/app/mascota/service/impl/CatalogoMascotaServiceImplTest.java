package com.lovepaws.app.mascota.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.lovepaws.app.mascota.domain.Especie;
import com.lovepaws.app.mascota.dto.EspecieRequestDTO;
import com.lovepaws.app.mascota.repository.CategoriaRepository;
import com.lovepaws.app.mascota.repository.EspecieRepository;
import com.lovepaws.app.mascota.repository.RazaRepository;
import com.lovepaws.app.user.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class CatalogoMascotaServiceImplTest {

    @Mock
    private EspecieRepository especieRepository;
    @Mock
    private RazaRepository razaRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private UsuarioRepository usuarioRepository;

    private CatalogoMascotaServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CatalogoMascotaServiceImpl(especieRepository, razaRepository, categoriaRepository, usuarioRepository);
    }

    @Test
    void crearEspecie_conUsuarioInvalido_guardaUsuarioCreacionNulo() {
        EspecieRequestDTO request = new EspecieRequestDTO(" Perro ", true);

        when(especieRepository.existsByNombreIgnoreCase("Perro")).thenReturn(false);
        when(usuarioRepository.existsById(999)).thenReturn(false);
        when(especieRepository.save(any(Especie.class))).thenAnswer(invocation -> {
            Especie especie = invocation.getArgument(0);
            especie.setId(3001);
            return especie;
        });

        var response = service.crearEspecie(request, 999);

        ArgumentCaptor<Especie> captor = ArgumentCaptor.forClass(Especie.class);
        verify(especieRepository).save(captor.capture());
        assertNull(captor.getValue().getIdUsuarioCreacion());
        assertEquals(3001, response.id());
        assertEquals("Perro", response.nombre());
    }

    @Test
    void listarRazas_conEspecieInexistente_lanzaNotFound() {
        when(especieRepository.findById(777)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.listarRazas(777));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void eliminarEspecie_conIntegridadReferencial_lanzaConflict() {
        Especie especie = new Especie();
        especie.setId(3000);

        when(especieRepository.findById(3000)).thenReturn(Optional.of(especie));
        when(especieRepository.existsByNombreIgnoreCase("Perro")).thenReturn(false);

        org.mockito.Mockito.doThrow(new DataIntegrityViolationException("fk"))
                .when(especieRepository).flush();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.eliminarEspecie(3000));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }
}
