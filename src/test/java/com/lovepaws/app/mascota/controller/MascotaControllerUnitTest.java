package com.lovepaws.app.mascota.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.lovepaws.app.config.storage.FileStorageService;
import com.lovepaws.app.mascota.domain.Mascota;
import com.lovepaws.app.mascota.repository.CategoriaRepository;
import com.lovepaws.app.mascota.repository.RazaRepository;
import com.lovepaws.app.mascota.service.MascotaService;

@ExtendWith(MockitoExtension.class)
class MascotaControllerUnitTest {

    @Mock
    private MascotaService mascotaService;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private RazaRepository razaRepository;

    @InjectMocks
    private MascotaController controller;

    @Test
    void listarCatalogo_aplicaFiltrosYCargaModelo() {
        Integer categoriaId = 1;
        Integer razaId = 2;
        Integer edadMax = 6;
        String q = "labrador";

        Mascota mascota = new Mascota();
        mascota.setId(100);
        when(mascotaService.buscarMascotasDisponibles(categoriaId, razaId, edadMax, q)).thenReturn(List.of(mascota));

        Model model = new ExtendedModelMap();
        String view = controller.listarCatalogo(categoriaId, razaId, edadMax, q, model);

        assertEquals("mascota/lista", view);
        assertEquals(categoriaId, model.getAttribute("categoriaId"));
        assertEquals(razaId, model.getAttribute("razaId"));
        assertEquals(edadMax, model.getAttribute("edadMax"));
        assertEquals(q, model.getAttribute("q"));

        verify(mascotaService).buscarMascotasDisponibles(categoriaId, razaId, edadMax, q);
        verify(categoriaRepository).findAll();
        verify(razaRepository).findAll();
    }
}
