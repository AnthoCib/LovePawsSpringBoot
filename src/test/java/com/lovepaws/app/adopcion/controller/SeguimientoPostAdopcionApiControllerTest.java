package com.lovepaws.app.adopcion.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovepaws.app.adopcion.dto.EstadoMascotaTracking;
import com.lovepaws.app.adopcion.dto.SeguimientoPostAdopcionRequestDTO;
import com.lovepaws.app.adopcion.dto.SeguimientoPostAdopcionResponseDTO;
import com.lovepaws.app.adopcion.service.SeguimientoPostAdopcionApiService;

@WebMvcTest(controllers = SeguimientoPostAdopcionApiController.class)
@AutoConfigureMockMvc(addFilters = false)
class SeguimientoPostAdopcionApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SeguimientoPostAdopcionApiService seguimientoApiService;

    @Test
    void crearSeguimiento_debeRetornarCreated() throws Exception {
        SeguimientoPostAdopcionRequestDTO request = new SeguimientoPostAdopcionRequestDTO();
        request.setAdopcionId(10);
        request.setFechaSeguimiento(LocalDateTime.of(2026, 2, 10, 10, 30));
        request.setNotas("Mascota estable");
        request.setEstadoMascota(EstadoMascotaTracking.BIEN);

        SeguimientoPostAdopcionResponseDTO response = SeguimientoPostAdopcionResponseDTO.builder()
                .id(1)
                .adopcionId(10)
                .estadoMascota(EstadoMascotaTracking.BIEN)
                .build();

        when(seguimientoApiService.crearSeguimiento(any(SeguimientoPostAdopcionRequestDTO.class), any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/seguimientos-post-adopcion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.adopcionId").value(10))
                .andExpect(jsonPath("$.estadoMascota").value("BIEN"));
    }

    @Test
    void listarSeguimientos_debeRetornarLista() throws Exception {
        SeguimientoPostAdopcionResponseDTO item = SeguimientoPostAdopcionResponseDTO.builder()
                .id(2)
                .adopcionId(11)
                .estadoMascota(EstadoMascotaTracking.ATENCION_VETERINARIA)
                .build();

        when(seguimientoApiService.listarSeguimientos(eq(EstadoMascotaTracking.ATENCION_VETERINARIA)))
                .thenReturn(List.of(item));

        mockMvc.perform(get("/api/seguimientos-post-adopcion")
                        .param("estadoMascota", "ATENCION_VETERINARIA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].estadoMascota").value("ATENCION_VETERINARIA"));
    }

    @Test
    void actualizarSeguimiento_debeRetornarOk() throws Exception {
        SeguimientoPostAdopcionRequestDTO request = new SeguimientoPostAdopcionRequestDTO();
        request.setAdopcionId(12);
        request.setFechaSeguimiento(LocalDateTime.of(2026, 2, 11, 9, 15));
        request.setNotas("Se detectó control veterinario");
        request.setEstadoMascota(EstadoMascotaTracking.ATENCION_VETERINARIA);

        SeguimientoPostAdopcionResponseDTO response = SeguimientoPostAdopcionResponseDTO.builder()
                .id(3)
                .adopcionId(12)
                .estadoMascota(EstadoMascotaTracking.ATENCION_VETERINARIA)
                .build();

        when(seguimientoApiService.actualizarSeguimiento(eq(3), any(SeguimientoPostAdopcionRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/seguimientos-post-adopcion/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.estadoMascota").value("ATENCION_VETERINARIA"));
    }

    @Test
    void crearSeguimiento_sinCamposObligatorios_debeRetornarBadRequest() throws Exception {
        String bodyInvalido = "{}";

        mockMvc.perform(post("/api/seguimientos-post-adopcion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyInvalido))
                .andExpect(status().isBadRequest());
    }
}
