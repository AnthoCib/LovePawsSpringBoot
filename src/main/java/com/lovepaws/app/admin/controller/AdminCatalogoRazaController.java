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

import com.lovepaws.app.mascota.domain.Raza;
import com.lovepaws.app.admin.service.EspecieAdminService;
import com.lovepaws.app.admin.service.RazaAdminService;
import com.lovepaws.app.security.UsuarioPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/catalogos/razas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCatalogoRazaController {

    private final RazaAdminService razaAdminService;
    private final EspecieAdminService especieAdminService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("razas", razaAdminService.listarActivas());
        return "redirect:/admin/catalogos";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("raza", new Raza());
        model.addAttribute("especies", especieAdminService.listarActivas());
        return "redirect:/admin/catalogos";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("raza") Raza raza,
                          BindingResult bindingResult,
                          @AuthenticationPrincipal UsuarioPrincipal principal,
                          Model model,
                          RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("especies", especieAdminService.listarActivas());
            return "redirect:/admin/catalogos";
        }
        try {
            Integer userId = principal != null ? principal.getUsuario().getId() : null;
            razaAdminService.crear(raza, userId);
            ra.addFlashAttribute("swalSuccess", "Raza creada correctamente");
            return "redirect:/admin/catalogos";
        } catch (Exception ex) {
            ra.addFlashAttribute("swalError", ex.getMessage());
            return "redirect:/admin/catalogos";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("raza", razaAdminService.obtenerActiva(id));
            model.addAttribute("especies", especieAdminService.listarActivas());
            return "redirect:/admin/catalogos";
        } catch (Exception ex) {
            ra.addFlashAttribute("swalError", ex.getMessage());
            return "redirect:/admin/catalogos";
        }
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id,
                             @Valid @ModelAttribute("raza") Raza raza,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("especies", especieAdminService.listarActivas());
            return "redirect:/admin/catalogos";
        }
        try {
            razaAdminService.actualizar(id, raza);
            ra.addFlashAttribute("swalSuccess", "Raza actualizada correctamente");
        } catch (Exception ex) {
            ra.addFlashAttribute("swalError", ex.getMessage());
        }
        return "redirect:/admin/catalogos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            razaAdminService.eliminar(id);
            ra.addFlashAttribute("swalSuccess", "Raza eliminada correctamente");
        } catch (Exception ex) {
            ra.addFlashAttribute("swalError", ex.getMessage());
        }
        return "redirect:/admin/catalogos";
    }
}
