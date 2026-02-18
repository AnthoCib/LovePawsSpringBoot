package com.lovepaws.app.mascota.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lovepaws.app.api.dto.MascotaResponseDTO;
import com.lovepaws.app.mascota.service.MascotaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mascotas")
@RequiredArgsConstructor
public class MascotaControllerApi {

	  private final MascotaService mascotaservice;

	  
	    @GetMapping
	    public List<MascotaResponseDTO> listarMascotas() {
	        return mascotaservice.listar();
	    }
}
