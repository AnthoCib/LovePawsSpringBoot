package com.lovepaws.app.admin.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lovepaws.app.mascota.domain.Mascota;
import com.lovepaws.app.mascota.service.MascotaService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/mascotas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminMascotaController {

	private final MascotaService mascotaService;
	
    @GetMapping
    public String listarMascotas(Model model) {
    	model.addAttribute("mascotas", mascotaService.listarMascotas());
        return "admin/mascotas";
    }
    
    //Veer Detalle
    @GetMapping("/{id}")
    public String detalle(@PathVariable Integer id, Model model) {

        Mascota mascota = mascotaService.findMascotaById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        model.addAttribute("mascota", mascota);
        model.addAttribute("propietario", mascota.getUsuarioCreacion());

        return "admin/mascota-detalle";
    }
}
