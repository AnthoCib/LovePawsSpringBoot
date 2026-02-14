package com.lovepaws.app.admin.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lovepaws.app.mascota.domain.Especie;
import com.lovepaws.app.admin.service.EspecieAdminService;
import com.lovepaws.app.security.UsuarioPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/catalogos/especies")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCatalogoEspecieController {

    private final EspecieAdminService especieAdminService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("especies", especieAdminService.listarActivas());
        return "redirect:/admin/catalogos";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("especie", new Especie());
        return "redirect:/admin/catalogos";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("especie") Especie especie,
                          BindingResult bindingResult,
                          @AuthenticationPrincipal UsuarioPrincipal principal,
                          RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("swalError", "Formulario inválido");
            return "redirect:/admin/catalogos";
        }
        try {
            Integer userId = principal != null ? principal.getUsuario().getId() : null;
            especieAdminService.crear(especie, userId);
            ra.addFlashAttribute("swalSuccess", "Especie creada correctamente");
            return "redirect:/admin/catalogos";
        } catch (Exception ex) {
            ra.addFlashAttribute("swalError", ex.getMessage());
            return "redirect:/admin/catalogos";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("especie", especieAdminService.obtenerActiva(id));
            return "redirect:/admin/catalogos";
        } catch (Exception ex) {
            ra.addFlashAttribute("swalError", ex.getMessage());
            return "redirect:/admin/catalogos";
        }
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id,
                             @Valid @ModelAttribute("especie") Especie especie,
                             BindingResult bindingResult,
                             RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("swalError", "Formulario inválido");
            return "redirect:/admin/catalogos";
        }
        try {
            especieAdminService.actualizar(id, especie);
            ra.addFlashAttribute("swalSuccess", "Especie actualizada correctamente");
        } catch (Exception ex) {
            ra.addFlashAttribute("swalError", ex.getMessage());
        }
        return "redirect:/admin/catalogos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            especieAdminService.eliminar(id);
            ra.addFlashAttribute("swalSuccess", "Especie eliminada correctamente");
        } catch (Exception ex) {
            ra.addFlashAttribute("swalError", ex.getMessage());
        }
        return "redirect:/admin/catalogos";
    }
}
