package com.lovepaws.app.admin.controller;

import java.time.LocalDate;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lovepaws.app.admin.service.ReporteService;
import com.lovepaws.app.admin.service.pdf.ReportePdfService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/reportes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminReporteController {

	private final ReporteService reporteService;
	private final ReportePdfService reportePdfService;

	@GetMapping
	public String dashboard( @RequestParam(required = false) LocalDate desde,
		    @RequestParam(required = false) LocalDate hasta,Model model) {
		

		model.addAttribute("totalUsuarios", reporteService.totalUsuarios());

		model.addAttribute("totalMascotas", reporteService.totalMascotas());

		model.addAttribute("mascotasPorEstado", reporteService.mascotasPorEstado(desde,hasta));

		model.addAttribute("usuariosPorRol", reporteService.usuariosPorRol(desde, hasta));

		return "admin/reportes/dashboard-reportes";
	}
	
	@GetMapping("/pdf")
	public ResponseEntity<byte[]> exportarPdf() {

	    byte[] pdf = reportePdfService.generarReporte(
	            reporteService.totalUsuarios(),
	            reporteService.totalMascotas(),
	            reporteService.mascotasPorEstado(),
	            reporteService.usuariosPorRol()
	    );

	    return ResponseEntity.ok()
	            .header("Content-Disposition", "attachment; filename=reporte-lovepaws.pdf")
	            .contentType(MediaType.APPLICATION_PDF)
	            .body(pdf);
	}

}