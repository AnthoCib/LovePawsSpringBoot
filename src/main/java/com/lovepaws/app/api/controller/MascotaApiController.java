package com.lovepaws.app.api.controller;

import com.lovepaws.app.api.dto.MascotaRequestDTO;
import com.lovepaws.app.api.dto.MascotaResponseDTO;
import com.lovepaws.app.api.service.MascotaApiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
@RequiredArgsConstructor
public class MascotaApiController {

    private final MascotaApiService mascotaApiService;

    @GetMapping
    public ResponseEntity<List<MascotaResponseDTO>> listarMascotas() {
        return ResponseEntity.ok(mascotaApiService.findAll());
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<MascotaResponseDTO>> listarDisponibles() {
        return ResponseEntity.ok(mascotaApiService.findDisponibles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MascotaResponseDTO> detalle(@PathVariable Integer id) {
        return ResponseEntity.ok(mascotaApiService.findById(id));
    }

    @PostMapping
    public ResponseEntity<MascotaResponseDTO> crear(@Valid @RequestBody MascotaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mascotaApiService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MascotaResponseDTO> actualizar(@PathVariable Integer id,
                                                          @Valid @RequestBody MascotaRequestDTO request) {
        return ResponseEntity.ok(mascotaApiService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        mascotaApiService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
