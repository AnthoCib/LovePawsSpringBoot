package com.lovepaws.app.adopcion.integration;

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
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovepaws.app.adopcion.domain.Adopcion;
import com.lovepaws.app.adopcion.domain.EstadoAdopcion;
import com.lovepaws.app.adopcion.domain.SeguimientoAdopcion;
import com.lovepaws.app.adopcion.repository.AdopcionRepository;
import com.lovepaws.app.adopcion.repository.SeguimientoPostAdopcionRepository;
import com.lovepaws.app.mascota.domain.EstadoMascota;
import com.lovepaws.app.user.service.AuditoriaService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class SeguimientoPostAdopcionApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SeguimientoPostAdopcionRepository seguimientoRepository;
    @MockBean
    private AdopcionRepository adopcionRepository;
    @MockBean
    private AuditoriaService auditoriaService;

    @Test
    void flujoCompleto_trackingPostAdopcion() throws Exception {
        Adopcion adopcionAprobada = crearAdopcion(44, "APROBADA");

        when(adopcionRepository.findByIdWithRelationsAndActivoTrue(44)).thenReturn(Optional.of(adopcionAprobada));
        when(seguimientoRepository.save(any(SeguimientoAdopcion.class))).thenAnswer(invocation -> {
            SeguimientoAdopcion seguimiento = invocation.getArgument(0);
            if (seguimiento.getId() == null) {
                seguimiento.setId(900);
                seguimiento.setFechaCreacion(LocalDateTime.of(2026, 2, 20, 10, 0));
            }
            seguimiento.setFechaModificacion(LocalDateTime.of(2026, 2, 20, 10, 30));
            return seguimiento;
        });

        EstadoMascota estadoVet = new EstadoMascota("ATENCION_VETERINARIA", "Atención veterinaria");
        SeguimientoAdopcion item = new SeguimientoAdopcion();
        item.setId(900);
        item.setAdopcion(adopcionAprobada);
        item.setEstado(estadoVet);
        item.setFechaVisita(LocalDateTime.of(2026, 2, 21, 9, 0));
        when(seguimientoRepository.findAllByFiltros(eq("ATENCION_VETERINARIA"), eq("APROBADA")))
                .thenReturn(List.of(item));
        when(seguimientoRepository.findByIdWithRelations(900)).thenReturn(Optional.of(item));

        String crearBody = """
                {
                  "adopcionId": 44,
                  "fechaSeguimiento": "2026-02-21T09:00:00",
                  "notas": "Primer control ok",
                  "estadoMascota": "ATENCION_VETERINARIA",
                  "activo": true
                }
                """;

        mockMvc.perform(post("/api/seguimientos-post-adopcion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(crearBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(900))
                .andExpect(jsonPath("$.adopcionId").value(44));

        mockMvc.perform(get("/api/seguimientos-post-adopcion")
                        .param("estadoMascota", "ATENCION_VETERINARIA")
                        .param("estadoProceso", "APROBADA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estadoMascota").value("ATENCION_VETERINARIA"));

        String actualizarBody = """
                {
                  "adopcionId": 44,
                  "fechaSeguimiento": "2026-02-22T09:00:00",
                  "notas": "Control actualizado",
                  "estadoMascota": "BIEN",
                  "activo": true
                }
                """;

        mockMvc.perform(patch("/api/seguimientos-post-adopcion/900")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(actualizarBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(900));
    }

    @Test
    void crearSeguimiento_conAdopcionNoAprobada_retornaConflict() throws Exception {
        when(adopcionRepository.findByIdWithRelationsAndActivoTrue(55)).thenReturn(Optional.of(crearAdopcion(55, "PENDIENTE")));

        String body = """
                {
                  "adopcionId": 55,
                  "fechaSeguimiento": "2026-02-21T09:00:00",
                  "notas": "No debería permitir",
                  "estadoMascota": "BIEN"
                }
                """;

        mockMvc.perform(post("/api/seguimientos-post-adopcion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Solo se pueden registrar seguimientos para adopciones aprobadas"));
    }

    private Adopcion crearAdopcion(Integer id, String estadoProceso) {
        Adopcion adopcion = new Adopcion();
        adopcion.setId(id);
        adopcion.setActivo(true);
        EstadoAdopcion estado = new EstadoAdopcion();
        estado.setId(estadoProceso);
        adopcion.setEstado(estado);
        return adopcion;
    }
}
